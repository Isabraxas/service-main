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

        ConsumerRecords<String, SenderTemplate> records = null;
        ConsumerRecord<String, SenderTemplate> recordST;
        TopicPartition topicPartition = new TopicPartition(topic, partition);

        boolean flag = true;
        do {
            records = consumer.poll(100);

            if (records.iterator().hasNext()) {
                recordST = records.iterator().next();

                if (recordST.offset() == offset) {
                    senderTemplate = recordST.value();

                    log.info("offset = " + recordST.offset()
                                 + ", key = " + recordST.key()
                                 + ", attempt = " + recordST.value().getAttemptNumber()
                                 + ", account = " + recordST.value().getAccount()
                                 + ", formater = " + recordST.value().getFormatAdapter()
                                 + ", sender = " + recordST.value().getSendAdapter()
                    );

                    consumer.close();
                    return senderTemplate;
                }
            }

            if (flag) {
                consumer.seek(
                    topicPartition,
                    offset
                );
                flag = false;
            }

        } while (true);
    }
}
