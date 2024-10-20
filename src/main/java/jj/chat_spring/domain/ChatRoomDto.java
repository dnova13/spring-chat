package jj.chat_spring.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ChatRoomDto {

    private Long id;

    private Long userId;

    private String message;

    private LocalDateTime createdAt;

    private boolean isRead;

    private UserDto opponent;

    public ChatRoomDto() {
    }

    // JPQL 선택에 맞는 생성자
    public ChatRoomDto(Long id, Long userId, String message, LocalDateTime createdAt, boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

}
