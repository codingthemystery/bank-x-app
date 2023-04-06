package za.co.cajones.bankx.api.config;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import za.co.cajones.bankx.dto.TransactionDto;


@Slf4j
@Configuration
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class TransactionProducer {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Bean
	public ProducerFactory<String, TransactionDto> transactionProducerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

		log.info("***** Created Transaction Producer Factory.");
		return new DefaultKafkaProducerFactory<>(props);
	}
	
	@Bean
	public KafkaTemplate<String, TransactionDto> transactionKafkaTemplate() {
		log.info("***** Created Transaction Template.");
		return new KafkaTemplate<>(transactionProducerFactory());
	}

}