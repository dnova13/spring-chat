package jj.chat_spring.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDto {

    private Long id;
    private String username;
    private String avatar;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;

    public UserDto() {
    }

    public UserDto(Long id, String username, String avatar, String firstName, String lastName, String email, LocalDateTime joinedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.joinedAt = joinedAt;
        this.updatedAt = updatedAt;
    }
}
