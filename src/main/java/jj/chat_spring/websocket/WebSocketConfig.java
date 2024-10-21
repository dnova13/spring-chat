package jj.chat_spring.websocket;

import jj.chat_spring.websocket.handler.ChatHandler;
import jj.chat_spring.websocket.handler.NotificationHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatHandler chatHandler;

    private final NotificationHandler notificationHandler;

    public WebSocketConfig(ChatHandler chatHandler, NotificationHandler notificationHandler) {
        this.chatHandler = chatHandler;
        this.notificationHandler = notificationHandler;
    }


    // 웹소켓 엔드포인트 설정
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        
        // 채팅 엔드 포인트 셋팅
//        registry.addHandler(chatHandler, "/ws/chat").setAllowedOrigins("*");
        registry.addHandler(chatHandler, "/ws/conversation/{roomId}").setAllowedOrigins("*");


        // 알림 엔드 포인트 셋팅
        registry.addHandler(notificationHandler, "/ws/noti/{userId}").setAllowedOrigins("*");

    }
}