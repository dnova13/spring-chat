package jj.chat_spring.controller.api;

import jj.chat_spring.domain.ChatMessage;
import jj.chat_spring.domain.ChatMessageDto;
import jj.chat_spring.domain.ChatRoomDto;
import jj.chat_spring.domain.UserDto;
import jj.chat_spring.repository.ChatRepository;
import jj.chat_spring.service.ChatService;
import jj.chat_spring.utils.JsonResponse;
import jj.chat_spring.web.argumentresolver.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/conversations")
public class ChatController {

    private final ChatService chatService;

    private final ChatRepository chatRepository;

    @Autowired
    public ChatController(ChatService chatService, ChatRepository chatRepository) {
        this.chatService = chatService;
        this.chatRepository = chatRepository;
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
    public ResponseEntity<Map<String, Object>> getChatMessageInRoom(@PathVariable Long roomId,
                                                                    @RequestParam(defaultValue = "15") int limit,
                                                                    @RequestParam(defaultValue = "0") int start) {

        int offset = (start > 0) ? (start - 1) : 0;
        ArrayList<ChatMessageDto> ChatMessageList = (ArrayList<ChatMessageDto>) chatService.getChatMessageListByRoomId(roomId, limit, offset);
        int totalMessage = Math.toIntExact(chatRepository.getTotalChatMessageById(roomId));

        Map<String, Object> result = new HashMap<>();

        System.out.println(ChatMessageList);

//        result.put("total", totalMessage);
//        result.put("data", ChatMessageList);
//        result.put("success", true);
//
//        return ResponseEntity.ok(result);
        return JsonResponse.success(ChatMessageList, totalMessage);
    }

    // 메시지 쓰기
    @PostMapping(value = "/{roomId}/send", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> writeChatMessage(@PathVariable Long roomId
            , @Login UserDto loginUser
            , @RequestBody Map<String, String> requestBody) {

//        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
//        System.out.println(requestBody);
        String message = requestBody.get("msg");

//        UserDto loginUser = new UserDto();
//        loginUser.setId(1L);

        Long userId = loginUser.getId();

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setUserId(userId);
        chatMessage.setRoomId(roomId);

        ChatMessage savedChatMessage;

        try {
            savedChatMessage = chatService.writeChatMessage(chatMessage);
        } catch (Exception e) {
            savedChatMessage = null;
        }


        Map<String, Object> result = new HashMap<>();

        if (savedChatMessage == null) {
            return JsonResponse.fail("invalid value", HttpStatus.BAD_REQUEST);
//            result.put("success", false);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }


//        result.put("success", true);
//        result.put("data", savedChatMessage);
        return JsonResponse.success(savedChatMessage);
    }

    // 메시지 읽음 처리
    @PostMapping(value = "/{roomId}/read", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> readChatMessage(@PathVariable Long roomId
            , @Login UserDto loginUser
    ) {

        Long userId = loginUser.getId();

        Boolean isRead = chatService.readChatMessage(roomId, userId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", isRead);

//        return JsonResponse.success()
        return ResponseEntity.ok(result);
    }


}
