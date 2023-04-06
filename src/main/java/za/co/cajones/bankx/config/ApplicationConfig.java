package za.co.cajones.bankx.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class ApplicationConfig {

}
