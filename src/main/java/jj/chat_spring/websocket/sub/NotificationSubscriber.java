package jj.chat_spring.websocket.sub;

import jj.chat_spring.utils.JsonUtil;
import jj.chat_spring.websocket.handler.NotificationHandlerRedis;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationSubscriber {

    private final NotificationHandlerRedis notificationHandlerRedis;

    public NotificationSubscriber(NotificationHandlerRedis notificationHandlerRedis) {
        this.notificationHandlerRedis = notificationHandlerRedis;
    }

    public void handleMessage(String message) throws Exception {
        // 메시지를 받아서 필요한 처리 로직을 구현
        System.out.println("Received message from redis : " + message);

        Map<String, Object> messageMap = JsonUtil.jsonStringToMap(message);
        String groupName = (String) messageMap.get("group");

        notificationHandlerRedis.sendMessageToGroup(groupName, JsonUtil.jsonStringify(messageMap.get("message")));
    }
}