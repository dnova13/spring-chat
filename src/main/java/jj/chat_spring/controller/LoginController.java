package jj.chat_spring.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jj.chat_spring.domain.User;
import jj.chat_spring.domain.UserDto;
import jj.chat_spring.service.UserService;
import jj.chat_spring.utils.SessionConst;
import jj.chat_spring.domain.LoginForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @PostMapping("/login")
    public String loginV1(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return "login_form";
        }

        UserDto loginUser = userService.loginUser(form.getUsername(), form.getPassword());

        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login_form";
        }

        System.out.println(loginUser.toString());

        // 로그인 성공 처리
        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession(); // request.getSession(true); : default가 true라서 생략

        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginUser);

        return "redirect:/conversations/list";
    }

    @PostMapping("/api/v1/login")
    @ResponseBody
    public  ResponseEntity<Map<String, Object>> loginV2(@RequestParam String username, @RequestParam String password,  HttpServletRequest request) {


        UserDto loginUser = userService.loginUser(username, password);
        Map<String, Object> result = new HashMap<>();

        if (loginUser == null) {
            result.put("success", false);
            result.put("message", "아이디 또는 비밀번호가 맞지 않습니다.");

            return ResponseEntity.ok(result);
        }

        System.out.println(loginUser.toString());

        // 로그인 성공 처리
        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession(); // request.getSession(true); : default가 true라서 생략

        //세션에 로그인 회원 정보 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginUser);

        result.put("success", true);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/log-out")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> logout(HttpServletRequest request) {

        // 세션 무효화
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        Map<String, Boolean> result = new HashMap<>();
        result.put("success", true);

        return ResponseEntity.ok(result);
    }
}
