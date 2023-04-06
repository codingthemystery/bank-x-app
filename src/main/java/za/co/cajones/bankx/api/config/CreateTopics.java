package za.co.cajones.bankx.api.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
class KafkaTopicConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

	@Bean
	public NewTopic reconciliation() {
		log.info("Creating Topic: reconciliations");
		return TopicBuilder.name("reconciliations").build();
	}

	@Bean
	public NewTopic reconciliationReesponse() {
		log.info("Creating Topic: reconciliations-responses");
		return TopicBuilder.name("reconciliations-responses").build();
	}

	@Bean
	public NewTopic transaction() {
		log.info("Creating Topic: transactions");
		return TopicBuilder.name("transactions").build();
	}

	@Bean
	public NewTopic transactionResponse() {
		log.info("Creating Topic: transactions-responses");
		return TopicBuilder.name("transactions-responses").build();
	}
}
