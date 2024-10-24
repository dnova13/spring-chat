package jj.chat_spring.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonResponse {

    // 성공 응답 (List일 경우)
    public static ResponseEntity<Map<String, Object>> success(List<?> data, int total) {
        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("data", data);
        response.put("total", total);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 성공 응답 (객체일 경우)
    public static ResponseEntity<Map<String, Object>> success(Object data) {
        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("data", data);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 실패 응답
    public static ResponseEntity<Map<String, Object>> fail(String msg, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", msg);

        return new ResponseEntity<>(response, status);
    }
}
