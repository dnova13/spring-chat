package jj.chat_spring.service;

import jakarta.persistence.Tuple;
import jj.chat_spring.domain.User;
import jj.chat_spring.domain.UserDto;
import jj.chat_spring.repository.UserRepository;
import jj.chat_spring.utils.HashUtil;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto getUserInfo(Long userId) {
        return userRepository.findUserV1ById(userId);
    }

    public User joinUser(User user) {

        String pw = user.getPassword();
        String hashPw = HashUtil.hashSha256Password(pw);

        user.setPassword(hashPw);

        return userRepository.save(user);
    }

    public UserDto loginUser(String username, String password) {

        Tuple result = userRepository.findByUsernameTuple(username);

        if (result == null) return null;

        Long userId = result.get(0, Long.class);
        String userPw = result.get(1, String.class);
        String hashPw = HashUtil.hashSha256Password(password);

        if (!userPw.equals(hashPw)) return null;

        String email = result.get(2, String.class);
        String avatar = result.get(3, String.class);
        String firstName = result.get(4, String.class);
        String lastName = result.get(5, String.class);

        UserDto user = new UserDto();
        user.setId(userId);
        user.setEmail(email);
        user.setAvatar(avatar);
        user.setFirstName(firstName);
        user.setLastName(lastName);

        return user;
    }

}
