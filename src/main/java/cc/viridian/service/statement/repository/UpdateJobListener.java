package cc.viridian.service.statement.repository;

import cc.viridian.service.statement.model.UpdateJobTemplate;
import cc.viridian.service.statement.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UpdateJobListener {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    private JobService jobService;

    @KafkaListener(topics = "${topic.statement.update}")
    public void receive(@Payload final UpdateJobTemplate data,
                        @Headers final MessageHeaders headers) {
        log.info("received UpdateJob Message:");

        log.info(data.getAccount() + " " + data.getAdapterType() + " " + data.getAdapterCode());
        log.info(data.getErrorCode());
        log.info(data.getErrorDesc());
        log.info("id: " + data.getId().toString() + " at " + data.getLocalDateTime().toString());
        log.info("retry: " + data.getShouldTryAgain().toString());

        log.info("key: " + headers.get("kafka_receivedMessageKey")
                     + " partition:" + headers.get("kafka_receivedPartitionId"));
        log.info("topic:" + headers.get("kafka_receivedTopic") + " offset:" + headers.get("kafka_offset"));

        //todo: catch errors
        jobService.updateJob(data);
    }
}
