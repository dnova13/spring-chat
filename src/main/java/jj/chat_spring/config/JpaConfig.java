package jj.chat_spring.config;

import jakarta.persistence.EntityManager;
import jj.chat_spring.repository.ChatRepository;
import jj.chat_spring.repository.ChatRepositoryImpl;
import jj.chat_spring.repository.UserRepository;
import jj.chat_spring.service.ChatService;
import jj.chat_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JpaConfig {

    private final EntityManager em;
    private final UserRepository userRepository;


    @Bean
    public ChatService chatService() {
        return new ChatService(chatRepository());
    }

    @Bean
    public UserService userService() {
        return new UserService(userRepository);
    }


    @Bean
    public ChatRepository chatRepository() {
        return new ChatRepositoryImpl(em);
    }

}
