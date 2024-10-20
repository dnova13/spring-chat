package jj.chat_spring.controller;

import jj.chat_spring.domain.ChatRoomDto;
import jj.chat_spring.domain.LoginForm;
import jj.chat_spring.domain.User;
import jj.chat_spring.domain.UserDto;
import jj.chat_spring.service.ChatService;
import jj.chat_spring.service.UserService;
import jj.chat_spring.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {


    private final UserService userService;
    private final ChatService chatService;

    @RequestMapping("/")
    public String index(@Login UserDto loginUser, Model model) {

        System.out.println(loginUser);

        // 세션이 유지되면 로그인으로 이동
        if (loginUser != null) {
            System.out.println(loginUser.toString());
            return  "redirect:/conversations/list";
//            model.addAttribute("user", loginUser);
//            return "conversations/conversation_list";
        }

        model.addAttribute("loginForm", new LoginForm()); // User 객체를 모델에 추가
        return "login_form";
    }

    @RequestMapping("/login")
    public String login(@Login UserDto loginUser, Model model) {

//        if (loginUser != null) {
//            System.out.println(loginUser.toString());
//            model.addAttribute("user", loginUser);
//            return "conversations/conversation_list";
//        }

        model.addAttribute("loginForm", new LoginForm()); // User 객체를 모델에 추가
        return "login_form";
    }

    @RequestMapping("/conversations/list")
    public String chatList( Model model, @Login UserDto loginUser) {

//        System.out.println(loginUser);
//        System.out.println(loginUser.getId());

        Long userId = loginUser.getId();
        UserDto user = userService.getUserInfo(userId);
        List<ChatRoomDto> roomList = chatService.getChatRoomListByUserId(userId);
        System.out.println(user);
        System.out.println(roomList);

        model.addAttribute("user", user);
        model.addAttribute("room_list", roomList);

        return "conversations/conversation_list";
    }

    @RequestMapping("/conversations/{roomId}")
    public String chatDetail(Model model, @Login UserDto loginUser, @PathVariable Long roomId) {

        Long userId = loginUser.getId();
        UserDto user = userService.getUserInfo(userId);
        UserDto opponent = chatService.getChatOpponetInfo(roomId, userId);

        System.out.println(opponent.toString());

        model.addAttribute("user", user);
        model.addAttribute("opponent", opponent);

        return "conversations/conversation_detail";
    }
}