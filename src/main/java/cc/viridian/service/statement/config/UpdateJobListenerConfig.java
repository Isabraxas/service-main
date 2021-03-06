package cc.viridian.service.statement.config;

import cc.viridian.service.statement.model.UpdateJobTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class UpdateJobListenerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public Map<String, Object> consumerConfigs() {
            Map<String, Object> props = new HashMap<>();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "statement-service");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            return props;
    }

    @Bean
    public ConsumerFactory<String, UpdateJobTemplate> consumerFactory() {
        JsonDeserializer<UpdateJobTemplate> jsonDeserializer =
            new JsonDeserializer(UpdateJobTemplate.class, objectMapper);

        DefaultKafkaConsumerFactory<String, UpdateJobTemplate> consumerFactory =
            new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                                              new StringDeserializer(),
                                                jsonDeserializer
            );
        return consumerFactory;

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UpdateJobTemplate> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UpdateJobTemplate> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
