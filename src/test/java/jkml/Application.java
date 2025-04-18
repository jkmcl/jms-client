package jkml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	ArtemisConfigurationCustomizer artemisConfigurationCustomizer() {
		return config -> {
			config.getAddressSettings().values().forEach(settings -> {
				settings.setAutoCreateAddresses(false);
				settings.setAutoCreateQueues(false);
			});
		};
	}

}
