package jj.chat_spring.domain;

import lombok.Data;

@Data
public class UserSimpleDto {

    private Long id;
    private String username;
    private String avatar;
    private String firstName;
    private String lastName;
    private String email;

    public UserSimpleDto() {
    }

    public UserSimpleDto(Long id, String username, String avatar, String firstName, String lastName, String email) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
