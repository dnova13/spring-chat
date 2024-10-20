package jj.chat_spring.domain;


import lombok.Data;
import jakarta.validation.constraints.NotEmpty;

@Data
public class LoginForm {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;
}