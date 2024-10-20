package jj.chat_spring.controller.api;

import jj.chat_spring.domain.ChatMessage;
import jj.chat_spring.domain.ChatRoomDto;
import jj.chat_spring.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/conversations")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 채팅방 생성
    @PostMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Long> createChatRoom(@RequestParam Long userId, @RequestParam Long opponentId) {
        Long roomId = chatService.createChatRoom(userId, opponentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomId);
    }

    // 사용자 ID로 채팅방 찾기
    @GetMapping(value = "/rooms/list")
    @ResponseBody
    public ResponseEntity<ArrayList<ChatRoomDto>> findChatRoomsByUserId(@RequestParam Long userId) {
        ArrayList<ChatRoomDto> chatRooms = (ArrayList<ChatRoomDto>) chatService.getChatRoomListByUserId(userId);
        return ResponseEntity.ok(chatRooms);
    }


    // 채팅 룸 메세지 보기
    @GetMapping(value = "/{roomId}/list")
    @ResponseBody
    public ResponseEntity<ArrayList<ChatRoomDto>> getChatMessageInRoom(@RequestParam Long userId, @PathVariable String roomId) {
        ArrayList<ChatRoomDto> chatRooms = (ArrayList<ChatRoomDto>) chatService.getChatRoomListByUserId(userId);
        return ResponseEntity.ok(chatRooms);
    }

    // 메시지 쓰기
    @PostMapping(value = "/{roomId}/send", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> writeChatMessage(@PathVariable Long roomId, @RequestParam Long userId, @RequestParam String msg) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(msg);
//        chatMessage.setUserId(userId);
        chatMessage.setRoomId(roomId);

        ChatMessage savedChatMessage = chatService.writeChatMessage(chatMessage);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", savedChatMessage);

        return ResponseEntity.ok(result);
    }

    // 메시지 읽음 처리
    @PostMapping(value = "/{roomId}/read", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> readChatMessage(@PathVariable Long roomId, @RequestParam Long userId) {
        Boolean result = chatService.readChatMessage(roomId, userId);
        return ResponseEntity.ok(result);
    }


}
