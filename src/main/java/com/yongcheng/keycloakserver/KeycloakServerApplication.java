package com.yongcheng.keycloakserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.yongcheng.keycloakserver.config.KeycloakServerProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.boot.autoconfigure.web.ServerProperties;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
public class KeycloakServerApplication {
  private static final Logger LOG = LoggerFactory.getLogger(KeycloakServerApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(KeycloakServerApplication.class, args);
  }
  
  // *** Requires spring-boot-starter-web dependency for ServerProperties.
  @Bean
	ApplicationListener<ApplicationReadyEvent> onApplicationReadyEventListener(ServerProperties serverProperties,
			KeycloakServerProperties keycloakServerProperties) {

		return (evt) -> {

			Integer port = serverProperties.getPort();
			String keycloakContextPath = keycloakServerProperties.getContextPath();

			LOG.info("Embedded Keycloak started: http://localhost:{}{} to use keycloak", port, keycloakContextPath);
		};
	}

}
