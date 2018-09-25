package cc.viridian.service.statement.repository;

import cc.viridian.service.statement.model.SenderTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SenderProducer {

    @Autowired
    private KafkaTemplate<String, SenderTemplate> kafkaTemplate;

    @Autowired
    public SenderProducer(KafkaTemplate<String, SenderTemplate> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(final String messageKey, final SenderTemplate data) {
        log.debug("sending statement for account  "
                      + data.getStatement().getHeader().getAccountCode()
                      + ", attempt " + data.getAttemptNumber()
                      + " with key " + messageKey
        );

        Message<SenderTemplate> message = MessageBuilder
            .withPayload(data)
            .setHeader(KafkaHeaders.MESSAGE_KEY, messageKey)
            .build();

        kafkaTemplate.send(message);
    }
}
