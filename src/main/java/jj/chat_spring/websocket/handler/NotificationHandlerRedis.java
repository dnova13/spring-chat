package jj.chat_spring.websocket.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jj.chat_spring.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component // Spring이 이 클래스를 Bean으로 관리하도록 어노테이션 추가
public class NotificationHandlerRedis extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper

    private final Map<String, List<WebSocketSession>> groups = new ConcurrentHashMap<>();

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public NotificationHandlerRedis(RedisTemplate<String, Object> redisTemplate
            , @Qualifier("notificationTopic") ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("########################################");

//        sessions.add(session);
        String userId = extractIdFromSession(session);
        System.out.println("afterConnectionEstablished");
        System.out.println("userId: "+userId);

        String groupName = "noti_" + userId;
        joinGroup(groupName, session);
    }

    private String extractIdFromSession(WebSocketSession session) {
        String uri = session.getUri().toString();
        String[] parts = uri.split("/");

        // 마지막 부분이 roomId
        return parts[parts.length - 1];
    }

    public void joinGroup(String groupName, WebSocketSession session) {
        System.out.println("joinGroup : "+ groupName);

        groups.computeIfAbsent(groupName, k -> new CopyOnWriteArrayList<>()).add(session);

        System.out.println("joinGroup : " + groups);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        // 클라이언트로부터 받은 메시지
        String payload = message.getPayload();
        Map<String, Object> messageMap = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {
        });

        System.out.println("messageMap : " + messageMap);
        String userId = extractIdFromSession(session);
        System.out.println("handleTextMessage");
        System.out.println("userId: "+userId);

        String groupName = "noti_" + userId;
        System.out.println("groupName: "+ groupName);

        HashMap<String, Object> sendMessage = new HashMap<>();
//        sendMessage.put("created", LocalDateTime.now());
        sendMessage.put("message", messageMap);

        HashMap<String, Object> groupMessage = new HashMap<>();

        groupMessage.put("group", groupName);
        groupMessage.put("message", sendMessage);

        // Objects.requireNonNull : null 이면 NullPointerException 발생
        redisTemplate.convertAndSend(topic.getTopic(), Objects.requireNonNull(JsonUtil.jsonStringify(groupMessage)));

        // HashMap을 JSON 문자열로 변환
//        String jsonString = JsonUtil.jsonStringify(sendMessage);

        // 결과 출력
//        System.out.println(jsonString);
//        sendMessageToGroup(groupName, jsonString);

    }

    public void sendMessageToGroup(String groupName, String message) throws Exception {
        List<WebSocketSession> groupSessions = groups.get(groupName);

        System.out.println("send groupSessions: " + groupSessions);

        if (groupSessions != null) {
            for (WebSocketSession webSocketSession : groupSessions) {
                if (webSocketSession.isOpen()) {
                    System.out.println("send msg?");
                    webSocketSession.sendMessage(new TextMessage(message));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        sessions.remove(session);

        System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");

        String userId = extractIdFromSession(session);
        String groupName = "noti_" + userId;

        System.out.println("leave group");
        System.out.println(groupName);

        leaveGroup(groupName, session);
    }

    public void leaveGroup(String groupName, WebSocketSession session) {
        List<WebSocketSession> groupSessions = groups.get(groupName);

        System.out.println("leave groupSessions: " + groupSessions);

        if (groupSessions != null) {
            groupSessions.remove(session);
//            groups.remove(groupName);
            if (groupSessions.isEmpty()) {
                groups.remove(groupName);
            }
        }
    }
}