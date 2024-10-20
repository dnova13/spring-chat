package jj.chat_spring;

import jj.chat_spring.config.JpaConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(JpaConfig.class)
public class ChatSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatSpringApplication.class, args);
	}

}
