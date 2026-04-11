package com.example.MyBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DiscordBotApplication - The main entry point.
 *
 * The entry point. Intentionally minimal — just boots Spring. All setup is handled by @Configuration classes.
 * @SpringBootApplication is a shortcut for 3 annotations:
 *   @Configuration      - This class can define beans
 *   @EnableAutoConfiguration - Auto-configure Spring based on classpath
 *   @ComponentScan      - Find all @Component, @Service, @Repository, @Controller
 *
 * That's it. Spring takes it from here.
 */
@SpringBootApplication
public class MyBotApplication {


	private static final Logger log = LoggerFactory.getLogger(MyBotApplication.class);

	public static void main(String[] args) {
		log.info(" Starting Discord Bot...");

		// This single line:
		// 1. Creates Spring's ApplicationContext (the DI container)
		// 2. Scans for all @Component classes
		// 3. Creates @Bean objects in correct order
		// 4. Injects dependencies everywhere
		// 5. Calls BotConfig.jda() to connect to Discord
		// 6. Keeps app running (spring.main.keep-alive=true)
		SpringApplication.run(MyBotApplication.class, args);

		log.info("✅ Discord Bot is running. Press Ctrl+C to stop.");
	}
}