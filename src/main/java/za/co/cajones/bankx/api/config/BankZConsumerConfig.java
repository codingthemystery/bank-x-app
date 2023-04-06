package za.co.cajones.bankx.api.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import org.springframework.kafka.annotation.EnableKafka;

import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import za.co.cajones.bankx.dto.TransactionDto;

@Slf4j
@EnableKafka
@Configuration
@PropertySource("classpath:application-${spring.profiles.active}.properties")
class BankZConsumerConfig {

	@Value(value = "${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	// BankZ groupId;
	@Value(value = "${bankz.groupid}")
	private String groupId;

	public ConsumerFactory<String, TransactionDto> transactionConsumerFactory() {

		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

		log.info("***** Created Transaction Consumer Factory.");

		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
				new JsonDeserializer<>(TransactionDto.class));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, TransactionDto> transactionKafkaListenerContainerFactory() {

		ConcurrentKafkaListenerContainerFactory<String, TransactionDto> factory = new ConcurrentKafkaListenerContainerFactory<>();

		factory.setConsumerFactory(transactionConsumerFactory());
		return factory;
	}
}
