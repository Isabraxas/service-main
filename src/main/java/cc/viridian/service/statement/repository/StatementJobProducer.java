package cc.viridian.service.statement.repository;

import cc.viridian.service.statement.model.JobTemplate;
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

    public void send(final String messageKey, final JobTemplate data) {
        log.debug("sending message with key=" + messageKey + " " + data);

        Message<JobTemplate> message = MessageBuilder
            .withPayload(data)
            .setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
            .build();

        kafkaTemplate.send(message);
    }
}
