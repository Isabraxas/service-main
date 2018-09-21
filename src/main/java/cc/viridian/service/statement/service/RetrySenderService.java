package cc.viridian.service.statement.service;

import cc.viridian.service.statement.model.SenderTemplate;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class RetrySenderService {
    @Autowired
    private KafkaConsumer<String, SenderTemplate> consumerFactory2;

    @Autowired
    public RetrySenderService(KafkaConsumer<String, SenderTemplate> consumerFactory2) {
        this.consumerFactory2 = consumerFactory2;
    }

    public SenderTemplate getSendersTemplateByOffset(
        final String topic, final Integer partition, final Integer offset) {
        SenderTemplate senderTemplate = new SenderTemplate();
        consumerFactory2.subscribe(Arrays.asList(topic));

        boolean flag = true;
        while (true) {
            ConsumerRecords<String, SenderTemplate> records = consumerFactory2.poll(100);
            if (flag) {

                TopicPartition topicPartition = new TopicPartition(topic, partition);
                consumerFactory2.seek(
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
                //todo : add consumerFactory2 pause that needed a topicPartition collection
                //consumerFactory2.pause();
                break;
            }
        }
        System.out.println(senderTemplate);
        return senderTemplate;
    }
}
