package jj.chat_spring.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDto {
    private Long id;
    private String message;
    private Long roomId;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserSimpleDto user;

    public ChatMessageDto() {}

    public ChatMessageDto(Long id, String message, Long roomId, boolean isRead, LocalDateTime createdAt, LocalDateTime updatedAt, UserSimpleDto user) {
        this.id = id;
        this.message = message;
        this.roomId = roomId;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.user = user;
    }
}