package jj.chat_spring.repository;

import jakarta.persistence.Tuple;
import jj.chat_spring.domain.ChatMessage;
import jj.chat_spring.domain.ChatParticipants;
import jj.chat_spring.domain.ChatRoom;
import jj.chat_spring.domain.ChatRoomDto;

import java.util.ArrayList;
import java.util.List;

public interface ChatRepository {

    ChatRoom createChatRoom(ChatRoom chatRoom);

    ChatParticipants createParticipants(ChatParticipants chatParticipants);
    ChatMessage createChatMessage(ChatMessage chatRoom);

    Boolean updateChatMessageRead(Long chatRoomId, Long userId);

    List<ChatRoomDto> findChatRoomsByUserId(Long userId);

    Tuple findChatOpponetInfo(Long chatRoomId, Long userId);

    List<Tuple> findChatMessageById(Long chatRoomId, int limit, int offset);

    Long getTotalChatMessageById(Long chatRoomId);
}
