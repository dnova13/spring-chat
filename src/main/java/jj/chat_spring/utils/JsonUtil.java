package jj.chat_spring.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();


    // object JSON 형식으로 출력하는 static 메서드
    public static String jsonStringify(Object obj) {
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 모듈 등록

        try {
            // 객체를 JSON 문자열로 변환
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
//            e.printStackTrace();
            return null; // 변환 실패 시 null 반환
        }
    }

    public static Map<String, Object> jsonStringToMap(String jsonString) {

        try {
            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return null; // 변환 실패 시 null 반환
        }
    }

    // JSON 문자열을 List<Map<String, Object>>로 변환
    public static ArrayList<Map<String, Object>> jsonStringToList(String jsonString) {

        try {
            return objectMapper.readValue(jsonString, new TypeReference<ArrayList<Map<String, Object>>>() {});
        } catch (Exception e) {
            return null; // 변환 실패 시 null 반환
        }
    }
}