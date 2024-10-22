package jj.chat_spring.websocket;

import jj.chat_spring.websocket.sub.ChatMessageSubscriber;
import jj.chat_spring.websocket.sub.NotificationSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * RedisTemplate을 설정하는 Bean 메서드입니다.
     * Redis에 데이터를 읽고 쓸 때 사용할 RedisTemplate을 설정합니다.
     * 이 템플릿은 RedisConnectionFactory를 사용하여 Redis 서버와 연결합니다.
     * 키와 값을 직렬화/역직렬화하는 방법도 설정합니다.
     *
     * @param connectionFactory RedisConnection을 생성하는 팩토리로, Redis 서버와의 연결을 관리합니다.
     * @return RedisTemplate을 반환하여 Redis에 데이터를 저장하거나 조회할 때 사용됩니다.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        // RedisTemplate 인스턴스를 생성
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // RedisConnectionFactory를 사용하여 Redis 서버와 연결을 설정
        // application.properties 를 통해 redis 연결 설정함.
        redisTemplate.setConnectionFactory(connectionFactory);

        // Redis에 저장되는 키를 문자열로 직렬화하는 방식을 설정 (StringRedisSerializer 사용)
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Redis에 저장되는 값을 문자열로 직렬화하는 방식을 설정 (StringRedisSerializer 사용)
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }


    // Redis 메시지를 수신하기 위한 RedisMessageListenerContainer
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
                                                        MessageListenerAdapter chatListenerAdapter,
                                                        MessageListenerAdapter notificationListenerAdapter,
                                                        ChannelTopic chatTopic,
                                                        ChannelTopic notificationTopic) {

        // Redis 메시지를 수신하기 위한 RedisMessageListenerContainer 생성
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // chatTopic 구독
        container.addMessageListener(chatListenerAdapter, chatTopic);

        // notificationTopic 구독
        container.addMessageListener(notificationListenerAdapter, notificationTopic);
        return container;
    }

    // Redis의 Pub/Sub 메시징을 처리할 때 사용되는 어댑터 클래스
    @Bean
    public MessageListenerAdapter chatListenerAdapter(ChatMessageSubscriber chatSubscriber) {
        return new MessageListenerAdapter(chatSubscriber, "handleMessage");
    }

    // Redis의 Pub/Sub 메시징을 처리할 때 사용되는 어댑터 클래스
    @Bean
    public MessageListenerAdapter notificationListenerAdapter(NotificationSubscriber notificationSubscriber) {
        return new MessageListenerAdapter(notificationSubscriber, "handleMessage");
    }

    // 메시지가 발행되고 구독되는 Redis 채널을 정의 : chat 채널 생성
    @Bean
    public ChannelTopic chatTopic() {
        return new ChannelTopic("chat");
    }

    // 메시지가 발행되고 구독되는 Redis 채널을 정의 : notification 채널 생성 
    @Bean
    public ChannelTopic notificationTopic() {
        return new ChannelTopic("notification");
    }

//    @Bean
//    public ChannelTopic topic() {
//        return new ChannelTopic("chatRoom");
//    }
}