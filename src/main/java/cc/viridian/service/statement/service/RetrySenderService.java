package cc.viridian.service.statement.service;

import cc.viridian.service.statement.model.SenderTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Slf4j
@Service
public class RetrySenderService {
    @Autowired
    private ConsumerFactory<String, SenderTemplate> consumerSenderFactory;

    @Autowired
    public RetrySenderService(ConsumerFactory<String, SenderTemplate> consumerSenderFactory) {
        this.consumerSenderFactory = consumerSenderFactory;
    }

    public SenderTemplate getSendersTemplateByOffset(
        final String topic, final Integer partition, final Long offset) {

        SenderTemplate senderTemplate = new SenderTemplate();
        Consumer<String, SenderTemplate> consumer = consumerSenderFactory.createConsumer();
        consumer.subscribe(Arrays.asList(topic));

        boolean flag = true;
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
                log.info("offset = " + records.iterator().next().offset()
                             + ", key = " + records.iterator().next().key()
                             + ", value = " + records.iterator().next().value()
                );

                senderTemplate = records.iterator().next().value();
                consumer.paused();
                break;
            }
        }

        return senderTemplate;
    }
}
