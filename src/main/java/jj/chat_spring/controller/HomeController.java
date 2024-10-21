package jj.chat_spring.controller;

import jj.chat_spring.domain.*;
import jj.chat_spring.repository.ChatRepository;
import jj.chat_spring.service.ChatService;
import jj.chat_spring.service.UserService;
import jj.chat_spring.web.argumentresolver.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {


    private final UserService userService;
    private final ChatService chatService;

    private final ChatRepository chatRepository;

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

        int defaultLimit = 15;

        Long userId = loginUser.getId();
        UserDto user = userService.getUserInfo(userId);

        List<UserSimpleDto> opponentList = chatService.getChatOpponetList(roomId);

//        System.out.println(opponentList);
//        System.out.println(opponentList.size());
//        System.out.println(opponentList.isEmpty());

        if (opponentList == null || opponentList.isEmpty()) {
            model.addAttribute("error","401");
            return  "redirect:/conversations/list";
        }

        List<Long> userIds = opponentList.stream()
                .map(UserSimpleDto::getId).toList();

        if (!userIds.contains(userId)) {
            model.addAttribute("error","401");
            return  "redirect:/conversations/list";
        }

        opponentList.removeIf(UserSimpleDto -> UserSimpleDto.getId().equals(userId));

        ArrayList<ChatMessageDto> messageList = (ArrayList<ChatMessageDto>) chatService.getChatMessageListByRoomId(roomId, defaultLimit, 0);
        Long totalMessage = chatRepository.getTotalChatMessageById(roomId);

        messageList.sort((m1, m2) -> m1.getId().compareTo(m2.getId()));

//        System.out.println(messageList);
//        System.out.println(opponentList);

        model.addAttribute("roomId", roomId);
        model.addAttribute("user", user);
        model.addAttribute("messageList",messageList);
        model.addAttribute("totalMessage", totalMessage);
        model.addAttribute("opponent", opponentList.get(0));

        return "conversations/conversation_detail";
    }
}