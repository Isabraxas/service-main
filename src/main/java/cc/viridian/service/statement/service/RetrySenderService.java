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

        ConsumerRecords<String, SenderTemplate> records;
        final ConsumerRecord<String, SenderTemplate>[] recordST = new ConsumerRecord[]{null};
        while (true) {
            records = consumer.poll(100);
            if (records != null) {
                break;
            }
        }

        TopicPartition topicPartition = new TopicPartition(topic, partition);
        consumer.seek(
            topicPartition,
            offset
        );

        records.iterator().forEachRemaining(consumerRecord -> {
            if (consumerRecord.offset() == offset) {
                recordST[0] = consumerRecord;
            }
        });

        log.info("offset = " + recordST[0].offset()
                     + ", key = " + recordST[0].key()
                     + ", attempt = " + recordST[0].value().getAttemptNumber()
                     + ", account = " + recordST[0].value().getAccount()
                     + ", formater = " + recordST[0].value().getFormatAdapter()
                     + ", sender = " + recordST[0].value().getSendAdapter()
        );

        senderTemplate = recordST[0].value();
        consumer.close();
        return senderTemplate;
    }
}
