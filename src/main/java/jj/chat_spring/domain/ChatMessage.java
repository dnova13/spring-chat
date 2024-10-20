package jj.chat_spring.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long roomId;
    private Long userId;
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user; // User 객체와의 관계

    private String message;

    @Column(insertable = false)
    private Boolean isRead;

    @Column(insertable = false)
    private LocalDateTime createdAt;

    @Column(insertable = false)
    private LocalDateTime updatedAt;
}
