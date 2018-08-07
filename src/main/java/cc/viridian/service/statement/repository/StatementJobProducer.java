package cc.viridian.service.statement.repository;

import cc.viridian.service.statement.payload.JobTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StatementJobProducer {

    @Autowired
    private KafkaTemplate<String, JobTemplate> kafkaTemplate;

    public void send(String messageKey, JobTemplate data){
        log.debug("sending data= "+ data + " with key=" + messageKey);

        Message<JobTemplate> message = MessageBuilder
            .withPayload(data)
            .setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
            .build();

        kafkaTemplate.send(message);
    }
}
