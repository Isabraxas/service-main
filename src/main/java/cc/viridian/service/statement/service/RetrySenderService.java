package cc.viridian.service.statement.service;

import cc.viridian.service.statement.model.SenderTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

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

        AtomicReference<SenderTemplate> senderTemplate = new AtomicReference<>(new SenderTemplate());
        Consumer<String, SenderTemplate> consumer = consumerSenderFactory.createConsumer();
        consumer.subscribe(Arrays.asList(topic));

        ConsumerRecords<String, SenderTemplate> records = null;
        TopicPartition topicPartition = new TopicPartition(topic, partition);
        int i = 0;
        boolean flag = true;
        do {
            records = consumer.poll(100);
            if(flag) {
                consumer.seek(
                    topicPartition,
                    offset
                );
                flag = false;
            }
            System.out.println(i++);

          if(records.iterator().hasNext() && records.iterator().next().offset() == offset){
                break;
            }
        }while (flag == false);

        records.iterator().forEachRemaining(consumerRecord -> {
            if (consumerRecord.offset() == offset) {
                log.info("offset = " + consumerRecord.offset()
                             + ", key = " + consumerRecord.key()
                             + ", attempt = " + consumerRecord.value().getAttemptNumber()
                             + ", account = " + consumerRecord.value().getAccount()
                             + ", formater = " + consumerRecord.value().getFormatAdapter()
                             + ", sender = " + consumerRecord.value().getSendAdapter()
                );
                senderTemplate.set(consumerRecord.value());
            }
        });

        consumer.close();
        return senderTemplate.get();

    }

}
