package jj.chat_spring.websocket;

import jj.chat_spring.websocket.handler.ChatHandler;
import jj.chat_spring.websocket.handler.ChatHandlerRedis;
import jj.chat_spring.websocket.handler.NotificationHandler;
import jj.chat_spring.websocket.handler.NotificationHandlerRedis;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;

    private final NotificationHandler notificationHandler;

    private final ChatHandlerRedis chatHandlerRedis;

    private final NotificationHandlerRedis notificationHandlerRedis;

//    public WebSocketConfig(ChatHandler chatHandler, NotificationHandler notificationHandler) {
//        this.chatHandler = chatHandler;
//        this.notificationHandler = notificationHandler;
//    }


    public WebSocketConfig(ChatHandler chatHandler, NotificationHandler notificationHandler, ChatHandlerRedis chatHandlerRedis, NotificationHandlerRedis notificationHandlerRedis) {
        this.chatHandler = chatHandler;
        this.notificationHandler = notificationHandler;
        this.chatHandlerRedis = chatHandlerRedis;
        this.notificationHandlerRedis = notificationHandlerRedis;
    }

    // 웹소켓 엔드포인트 설정
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        
        // 채팅 엔드 포인트 셋팅
//        registry.addHandler(chatHandler, "/ws/conversation/{roomId}").setAllowedOrigins("*");
        registry.addHandler(chatHandlerRedis, "/ws/conversation/{roomId}").setAllowedOrigins("*");

        // 알림 엔드 포인트 셋팅
        registry.addHandler(notificationHandlerRedis, "/ws/noti/{userId}").setAllowedOrigins("*");

    }
}