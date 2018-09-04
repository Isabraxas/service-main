package cc.viridian.service.statement.repository;

import cc.viridian.provider.payload.ResponseAdapterCode;
import cc.viridian.provider.payload.ResponseErrorCode;
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

        String errorCode = validateErrorCode(data.getErrorCode());
        String adapterType = validateAdapterCode(data.getAdapterType());

        if (errorCode == null || adapterType == null) {
            log.error("update job message has invalid codes");
            log.error("errorCode: " + errorCode);
            log.error("adapterType: " + adapterType);
            return;
        }

        log.info(data.getAccount() + " " + adapterType + " " + data.getAdapterCode());
        log.info(errorCode + ": " + data.getErrorDesc());
        log.info("id: " + data.getId().toString() + " at " + data.getLocalDateTime().toString());
        log.info("retry: " + data.getShouldTryAgain().toString());

        log.info("key: " + headers.get("kafka_receivedMessageKey")
                     + " partition:" + headers.get("kafka_receivedPartitionId"));
        log.info("topic:" + headers.get("kafka_receivedTopic") + " offset:" + headers.get("kafka_offset"));

        if (adapterType.equals(ResponseAdapterCode.ADAPTER_COREBANK.name())) {
            jobService.updateJobCorebank(errorCode, data);
        }
        if (adapterType.equals(ResponseAdapterCode.ADAPTER_FORMATTER.name())) {
            jobService.updateJobFormatter(errorCode, data);
        }
        if (adapterType.equals(ResponseAdapterCode.ADAPTER_SENDER.name())) {
            jobService.updateJobSender(errorCode, data);
        }
    }

    private String validateErrorCode(final String code) {

        for (ResponseErrorCode value : ResponseErrorCode.values()) {
            if (value.name().equalsIgnoreCase(code)) {
                return value.name();
            }
        }
        return null;
    }

    private String validateAdapterCode(final String code) {

        for (ResponseAdapterCode value : ResponseAdapterCode.values()) {
            if (value.name().equalsIgnoreCase(code)) {
                return value.name();
            }
        }

        return null;
    }
}
