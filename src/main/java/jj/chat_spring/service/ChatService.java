package jj.chat_spring.service;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;
import jj.chat_spring.domain.*;
import jj.chat_spring.repository.ChatRepository;
import jj.chat_spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public class ChatService {

    private final ChatRepository chatRepository;


    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Transactional
    public Long createChatRoom(Long userId, Long opponetId) {

        ChatRoom room = new ChatRoom();
        ChatRoom savedRoom = chatRepository.createChatRoom(room);

        Long roomId = savedRoom.getId();

        ChatParticipants me = new ChatParticipants();
        me.setRoomId(roomId);
        me.setUserId(userId);
        chatRepository.createParticipants(me);

        ChatParticipants opponet = new ChatParticipants();
        opponet.setRoomId(roomId);
        opponet.setUserId(opponetId);
        chatRepository.createParticipants(opponet);

        return  roomId;
    }

    public UserDto getChatOpponetInfo(Long roomId, Long userId) {
        Tuple result = chatRepository.findChatOpponetInfo(roomId, userId);

        UserDto user = new UserDto();

        user.setId(result.get(0, Long.class));
        user.setEmail(result.get(1, String.class));
        user.setAvatar(result.get(2, String.class));
        user.setFirstName(result.get(3, String.class));
        user.setLastName(result.get(4, String.class));
        user.setUsername(result.get(5, String.class));

       return  user;
    }

    public ChatMessage writeChatMessage(ChatMessage chatMessage) {
        return chatRepository.createChatMessage(chatMessage);
    }

    public Boolean readChatMessage(Long roomId, Long userId) {
        return chatRepository.updateChatMessageRead(roomId, userId);
    }

    public List<ChatRoomDto> getChatRoomListByUserId(Long userId) {

        List<ChatRoomDto> roomList = chatRepository.findChatRoomsByUserId(userId);

        // 각 채팅방의 상대 정보를 추가
        for (ChatRoomDto room : roomList) {
            // 상대방 정보를 조회하는 메서드 (예: findChatOpponentInfo)
            UserDto opponent = getChatOpponetInfo(room.getId(), userId);
            room.setOpponent(opponent);
        }

        return roomList;
    }

//    public List<ChatMessageDto> getChatMessageListByRoomId(Long roomId) {
//
//    }
}
