package jj.chat_spring.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String avatar;
    private String firstName;
    private String lastName;
    private String email;

    @Column(insertable = false)
    private LocalDateTime joinedAt;

    @Column(insertable = false)
    private LocalDateTime updatedAt;

}
