package jj.chat_spring.repository;

import jj.chat_spring.domain.ChatRoomDto;
import jj.chat_spring.domain.User;
import jj.chat_spring.domain.UserDto;
import jj.chat_spring.service.ChatService;
import jj.chat_spring.service.UserService;
import jj.chat_spring.utils.HashUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class serviceTest {

    @Autowired
    UserService userService;

    @Autowired
    ChatService chatService;

    @Test
    public void 챗_테스트() {

        Long userId = 3L;
        Long roomId = 1L;

        System.out.println(chatService.getChatOpponetInfo(1L, 3L).toString());

        List<ChatRoomDto> roomList = chatService.getChatRoomListByUserId(userId);
        System.out.println(roomList);


        System.out.println(chatService.getChatMessageListByRoomId(1L, 3, 0));
    }

    @Test
    public void 회원가입_테스트() {

        User user = new User();

        user.setEmail("fsdfsdsf");
        user.setFirstName("ddd");
        user.setUsername("afasdf");

        String pw = "fsjkfjl2112";
        String hashPw = HashUtil.hashSha256Password(pw);

        user.setPassword(hashPw);

        User saveUser = userService.joinUser(user);

        System.out.println(saveUser.toString());

    }

    @Test
    public void 로깅_테스트() {

        String username = "test_account11";
        String pw = "1q2w3e4r1";

        String hashPw = HashUtil.hashSha256Password(pw);

        System.out.println(hashPw);

        System.out.println(userService.loginUser(username, pw));

        UserDto test1 = userService.getUserInfo(3L);

        System.out.println(test1.toString());

    }
}
