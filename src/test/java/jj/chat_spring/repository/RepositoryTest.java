package jj.chat_spring.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.Tuple;
import jj.chat_spring.domain.*;
import jj.chat_spring.utils.HashUtil;
import jj.chat_spring.utils.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
public class RepositoryTest {

    @Autowired
    ChatRepository chatRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    public void initChatSetting() {

//        System.out.println(chatRepository);
//        ChatRoom room = new ChatRoom();
////
//        ChatRoom saveRoom = chatRepository.createChatRoom(room);
//        System.out.println(saveRoom.toString());
//
//        ChatParticipants participants = new ChatParticipants();
//        participants.setUserId(1L);
//        participants.setRoomId(1L);
//
//        ChatParticipants saveParticipants = chatRepository.createParticipants(participants);
//
//        System.out.println(saveParticipants.toString());

        ChatMessage chatMessage = new ChatMessage();

//        chatMessage.setUserId(1L);
        chatMessage.setRoomId(1L);
        chatMessage.setMessage("!@SDFAFSDFasdffas");

        ChatMessage saveChatMessage = chatRepository.createChatMessage(chatMessage);

        System.out.println(saveChatMessage.toString());
    }

    @Test
    public void 채팅_메세지_찾기() {

        List<Tuple> test = chatRepository.findChatMessageById(1L, 3 ,0);

        System.out.println(chatRepository.getTotalChatMessageById(1L));

        System.out.println(test);
        System.out.println("AAA");
    }

    @Test
    public void 채팅방_찾기() {

        List<ChatRoomDto> chatRooms = chatRepository.findChatRoomsByUserId(3L);

        chatRooms.forEach(System.out::println); // toString() 사용

        String test = JsonUtil.jsonStringify(chatRooms);
        System.out.println(test);

        Map<String, Object> test1 =  JsonUtil.jsonStringToMap(test);
        System.out.println(test1);

        ArrayList<Map<String, Object>> test2 =  JsonUtil.jsonStringToList(test);
        System.out.println(test2);
        System.out.println(test2 != null ? test2.get(0) : null);


        Tuple test3 = chatRepository.findChatOpponetInfo(1L, 3L);

        System.out.println(test3);

//        System.out.println(JsonUtil.jsonStringToList(test));

//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JavaTimeModule());
//
//        try {
//            // ChatRoomDto 리스트를 JSON 문자열로 변환
//            String json = objectMapper.writeValueAsString(chatRooms);
//            System.out.println(json);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // ObjectMapper를 사용하여 JSON 문자열로 변환
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            String json = objectMapper.writeValueAsString(chatRooms);
//            System.out.println(json); // JSON 출력
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    @Test
    public void readMessage() {
        System.out.println(chatRepository.updateChatMessageRead(1L, 1L));
        System.out.println(chatRepository.updateChatMessageRead(5L, 1L));
    }


    @Test
    public void saveUser() {
        User user = new User();

        user.setEmail("fsdfsdsf");
        user.setFirstName("ddd");
        user.setUsername("dddddd");

        String pw = "fsjkfjl2112";
        String hashPw = HashUtil.hashSha256Password(pw);

        System.out.println(hashPw);
        user.setPassword(hashPw);

        System.out.println(user.toString());

        User saveUser = userRepository.save(user);

        System.out.println(saveUser.toString());
    }

    @Test
    public void findUser() {

        String username = "dddddd";

        Optional<User> userI21d = userRepository.findById(3L);

        System.out.println(userI21d.toString());

        User user = userRepository.findByUsername(username);

        System.out.println(user.toString());

//        System.out.println(userRepository.findV2ByUsername(username));

        Object cols1 =  userRepository.findV2ByUsername(username);
        Object[] cols2 =  userRepository.findV3ByUsername(username);
        Object cols3 =  userRepository.findV2ByUsername("username");

        Object[] result = (Object[]) cols1; // 형변환

        System.out.println(cols1);
        System.out.println(cols1.toString());
        System.out.println(Arrays.toString(result));

        System.out.println(cols2);

        for (Object item : cols2) {
//            System.out.println(Arrays.toString(item));
            System.out.println(Arrays.toString((Object[]) item));
//            Arrays a = (Arrays) item;
        }

//        System.out.println(user.toString());

        System.out.println(userRepository.isUser(2L));
        System.out.println(userRepository.isUser(3L));

        String userPw = userRepository.checkPassword(username);

        String pw = "fsjkfjl2112";
        String hashPw = HashUtil.hashSha256Password(pw);

        username = "ddddddas";

        Tuple result1 = userRepository.findByUsernameTuple(username);


        System.out.println(userPw);
        System.out.println(hashPw);
        System.out.println(userPw.equals(hashPw));
        System.out.println(result1);
        System.out.println(result1.get(0));
        System.out.println(result1.getElements());
//        System.out.println(result1.get("u.id"));
//        System.out.println(result1.get(User.id));

//        System.out.println(test.get("id"));


    }

}

