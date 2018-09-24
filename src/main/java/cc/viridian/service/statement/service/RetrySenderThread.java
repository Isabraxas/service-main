package cc.viridian.service.statement.service;

import cc.viridian.service.statement.model.JobTemplate;
import cc.viridian.service.statement.model.SenderTemplate;
import cc.viridian.service.statement.persistence.StatementJob;
import cc.viridian.service.statement.repository.SenderProducer;
import cc.viridian.service.statement.repository.StatementJobProducer;
import cc.viridian.service.statement.repository.StatementJobRepository;
import cc.viridian.service.statement.repository.UpdateJobListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.CayenneRuntimeException;
import org.apache.cayenne.ResultIterator;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class RetrySenderThread extends Thread {

    static final int MILLISECONDS_PER_SECOND = 1000;

    private StatementJobRepository statementJobRepository;

    private ScheduleService scheduleService;

    private SenderProducer senderProducer;

    @Autowired
    private RetrySenderService retrySenderService;


    public RetrySenderThread(String name) {
        super(name);
    }

    public void setStatementJobRepository(final StatementJobRepository statementJobRepository) {
        this.statementJobRepository = statementJobRepository;
    }

    public void setSenderProducer(final SenderProducer senderProducer) {
        this.senderProducer = senderProducer;
    }

    public void setParent(final ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public void run() {
        if (scheduleService == null) {
            log.error("scheduleService is null");
            return;
        }
        if (statementJobRepository == null) {
            log.error("statementJobRepository is null");
            scheduleService.setIdle();
            return;
        }

        ResultIterator iterator = null;

        try {
            iterator = statementJobRepository.getJobsToRetrySenderIterator();

            Map row = null;
            do {
                row = statementJobRepository.getJobsToRetrySenderNextRow(iterator);
                if (row != null) {
                    log.info("    account " + row.get("ACCOUNT_CODE").toString());
                    Long id = Long.valueOf(row.get("ID").toString());
                    StatementJob statementJob = statementJobRepository.findById(id);
                    statementJob.setStatus("QUEUED");
                    statementJob.setSenderRetries(statementJob.getSenderRetries() + 1);
                    statementJobRepository.updateStatementJob(statementJob);

                    //todo: get offset,(topic and partiton) from Update Queue
                    //todo: get SenderTemplate throght of service and her fuction
                    //todo: send SenderTemplate instead a JobTemplate
                    //todo: increment the AttemptNumber
                    //todo: the offset should be a new field in UpdateJob
                    //Test fake vars
                    String topic = "dev-sender2";
                    Integer partition = statementJob.getPartition();
                    Long offset = Long.valueOf(statementJob.getSenderOffset());

                    SenderTemplate senderTemplate = getSendersTemplateByOffset(topic, partition, offset);
                    senderTemplate.setAttemptNumber(senderTemplate.getAttemptNumber() + 1);
                    senderProducer.send("" + senderTemplate.getId(), senderTemplate);
                }
            } while (row != null);

        } catch (CayenneRuntimeException e) {
            log.error(e.getMessage());
            log.error(e.getCause().toString());
        } finally {
            statementJobRepository.getJobsToRetryCorebankNextFinally(iterator);

            scheduleService.setIdle();
        }
    }


    public SenderTemplate getSendersTemplateByOffset(
        final String topic, final Integer partition, final Long offset) {
        SenderTemplate senderTemplate = new SenderTemplate();
        boolean flag = true;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka.lab.viridian.cc:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JsonDeserializer<SenderTemplate> jsonDeserializer = new JsonDeserializer(SenderTemplate.class, objectMapper);

        KafkaConsumer<String, SenderTemplate> consumer = new KafkaConsumer<>(props, new StringDeserializer(),
                                                                             jsonDeserializer
        );
        consumer.subscribe(Arrays.asList(topic));

        while (true) {
            ConsumerRecords<String, SenderTemplate> records = consumer.poll(100);
            if (flag) {

                TopicPartition topicPartition = new TopicPartition(topic, partition);
                consumer.seek(
                    topicPartition,
                    offset
                );
                flag = false;
            }

            if (records.iterator().hasNext() && records.iterator().next().offset() == offset) {
                System.out.printf("offset = %d, key = %s, value = %s%n"
                    , records.iterator().next().offset()
                    , records.iterator().next().key()
                    , records.iterator().next().value());

                senderTemplate = records.iterator().next().value();
                //todo : add consumer pause that needed a topicPartition collection
                consumer.paused();
                break;
            }
        }
        System.out.println(senderTemplate);
        return senderTemplate;
    }


}
