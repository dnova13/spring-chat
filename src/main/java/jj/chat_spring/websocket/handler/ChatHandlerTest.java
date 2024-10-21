package jj.chat_spring.websocket.handler;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jj.chat_spring.utils.JsonUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


@Component // Spring이 이 클래스를 Bean으로 관리하도록 어노테이션 추가
public class ChatHandlerTest extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper

    /*
     * ConcurrentHashMap은 Java의 java.util.concurrent 패키지에 포함된 클래스 중 하나로,
     * 동시성(Concurrency)을 고려하여 설계된 해시 맵입니다.
     * 이 클래스는 멀티스레드 환경에서 안전하게 사용할 수 있도록 구현되어 있습니다
     * */
    private final Map<String, List<WebSocketSession>> groups = new ConcurrentHashMap<>();

    /*
     * 사용자에게 소켓 연결을 하고
     * 그 소켓에 대해 전체로 할건지 특정 그룹으로 할건지 결정
     * */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("########################################");
        // 전체 전송
//        sessions.add(session);
        
        // 그룹 전송
        String roomId = extractRoomIdFromSession(session);
        System.out.println("afterConnectionEstablished");
        System.out.println(roomId);

        String groupName = "chat_" + roomId;
        joinGroup(groupName, session);
    }


    private String extractRoomIdFromSession(WebSocketSession session) {
        String uri = session.getUri().toString();
        String[] parts = uri.split("/");
        return parts[parts.length - 1]; // 마지막 부분이 roomId
    }

    // 그룹에 추가하는 메서드
    public void joinGroup(String groupName, WebSocketSession session) {
        groups.computeIfAbsent(groupName, k -> new CopyOnWriteArrayList<>()).add(session);

        System.out.println("groups : " + groups);
    }



    /*
     * 한 클라이언트에 대한 메세지를 수신받고,
     * 그 해당 메세지를 다른 사람들에게 전달(송신) 함.
     * */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        System.out.println(message);
        // 클라이언트로부터 받은 메시지
        String payload = message.getPayload();

        System.out.println("payload");
        System.out.println(payload);

        // JSON을 객체로 변환
        Map<String, Object> messageMap = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>() {
        });

        System.out.println(messageMap);
        System.out.println(messageMap.get("type"));

//        String roomId = extractRoomIdFromSession(session);
//        String groupName = "chat_" + roomId;
//        System.out.println("groupName");
//        System.out.println(groupName);

        // 그룹 이름을 추출 (pk를 이용해 그룹을 찾음)
        String groupName = "chat_" + messageMap.get("pk");
        System.out.println("groupName");
        System.out.println(groupName);

        /*HashMap<String, Object> messageTest = new HashMap<>();
        messageTest.put("type", "chat_message");
        messageTest.put("message", messageMap);*/

        HashMap<String, Object> sendMessage = new HashMap<>();
        sendMessage.put("created", LocalDateTime.now());
        sendMessage.put("message",messageMap);

        // HashMap을 JSON 문자열로 변환
        String jsonString = JsonUtil.jsonStringify(sendMessage);

        // 결과 출력
        System.out.println(jsonString);
        sendMessageToGroup(groupName, jsonString);

//        // 가공된 메시지를 다시 전체 클라이언트에게 전송
//        for (WebSocketSession webSocketSession : sessions) {
//            if (webSocketSession.isOpen()) {
//                webSocketSession.sendMessage(new TextMessage(jsonString));
//            }
//        }
    }

    // 그룹에 메시지를 전송하는 메서드
    private void sendMessageToGroup(String groupName, String message) throws Exception {
        List<WebSocketSession> groupSessions = groups.get(groupName);

        if (groupSessions != null) {
            for (WebSocketSession webSocketSession : groupSessions) {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(new TextMessage(message));
                }
            }
        }
    }

    /*
     * 작업 끝낸 후 연결한 소켓 세션 닫기 위해 이용.
     * */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
        
        // 전부 제거
//        sessions.remove(session);

        String roomId = extractRoomIdFromSession(session);
        String groupName = "chat_" + roomId;

        System.out.println("leave group");
        System.out.println(groupName);

        leaveGroup(groupName, session);
    }

    // 그룹에서 제거하는 메서드
    public void leaveGroup(String groupName, WebSocketSession session) {
        System.out.println("groups : " + groups);
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAA");
        List<WebSocketSession> groupSessions = groups.get(groupName);

        System.out.println("leave groups : " + groupSessions);

        if (groupSessions != null) {
            groupSessions.remove(session);
            if (groupSessions.isEmpty()) {
                groups.remove(groupName);
            }
        }
    }
}