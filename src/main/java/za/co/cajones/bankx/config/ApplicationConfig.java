package za.co.cajones.bankx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class ApplicationConfig {

}
