package jj.chat_spring.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jj.chat_spring.domain.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
//@Repository
@Transactional
public class ChatRepositoryImpl implements ChatRepository {

    private final EntityManager em;

    public ChatRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public ChatRoom createChatRoom(ChatRoom chatRoom) {

        chatRoom.setCreatedAt(LocalDateTime.now());
        chatRoom.setUpdatedAt(LocalDateTime.now());

        em.persist(chatRoom);
        return chatRoom;
    }

    @Override
    public ChatParticipants createParticipants(ChatParticipants chatParticipants) {
        em.persist(chatParticipants);
        return chatParticipants;
    }

    @Override
    public ChatMessage createChatMessage(ChatMessage chatMessage) {
        em.persist(chatMessage);
        return chatMessage;
    }

    @Override
    public Boolean updateChatMessageRead(Long chatRoomId, Long userId) {

        String jpql = "UPDATE ChatMessage cm SET cm.isRead = true WHERE cm.roomId = :roomId AND cm.userId != :userId";

        int updatedCount = em.createQuery(jpql)
                .setParameter("roomId", chatRoomId)
                .setParameter("userId", userId)
                .executeUpdate();

        if (updatedCount > 0) {
            return true;
        }

        return false;
    }

    @Override
    public List<ChatRoomDto> findChatRoomsByUserId(Long userId) {
        String jpql =
//                "select  cr.id, cp.userId , cm.message , cm.createdAt, cm.isRead \n" +
                "select new jj.chat_spring.domain.ChatRoomDto(cr.id, cp.userId, cm.userId, cm.message, cm.createdAt, cm.isRead) " +
                "from ChatRoom cr \n" +
                "inner join ChatMessage cm \n" +
                "on cr.id = cm.roomId \n" +
                "inner join ChatParticipants cp \n" +
                "on cr.id = cp.roomId \n" +
                "where cp.userId = :userId \n" +
                "and cm.id = (select max(cm2.id) from ChatMessage cm2 where cm2.roomId = cr.id)\n" +
                "order by cr.id desc";

        return em.createQuery(jpql, ChatRoomDto.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<UserSimpleDto> findChatOpponetList(Long chatRoomId) {

        String jpql =
                "select u.id, u.username,u.avatar, u.firstName , u.lastName, u.email \n" +
                        "from ChatParticipants cp \n" +
                        "inner join User u \n" +
                        "on u.id = cp.userId  \n" +
                        "where cp.roomId = :roomId \n";
//                        "and u.id != :userId \n";

        return em.createQuery(jpql, UserSimpleDto.class)
//                .setParameter("userId", userId)
                .setParameter("roomId", chatRoomId)
                .getResultList();
    }

    @Override
    public Tuple findChatOpponetInfo(Long chatRoomId, Long userId) {
        String jpql =
                "select u.id, u.email,u.avatar, u.firstName , u.lastName, u.username \n" +
                        "from ChatParticipants cp \n" +
                        "inner join User u \n" +
                        "on u.id = cp.userId  \n" +
                        "where cp.roomId = :roomId \n" +
                        "and u.id != :userId \n";
        return em.createQuery(jpql, Tuple.class)
                .setParameter("userId", userId)
                .setParameter("roomId", chatRoomId)
                .setMaxResults(1)
                .getSingleResult();
    }

    @Override
    public List<Tuple> findChatMessageById(Long chatRoomId, int limit, int offset) {
        String jpql =
                """
                    select cm.id ,cm.message, cm.roomId ,cm.isRead ,cm.createdAt , cm.updatedAt
                            , u.id, u.username, u.avatar , u.firstName , u.lastName , u.email
                    from ChatMessage cm
                    inner join User u
                    on u.id = cm.userId
                    where cm.roomId = :roomId
                    order by cm.id desc """;

        TypedQuery<Tuple> query = em.createQuery(jpql, Tuple.class);
        query.setParameter("roomId", chatRoomId);

        // OFFSET 설정
        query.setFirstResult(offset);

        // LIMIT 설정
        if (limit > 0) {
            query.setMaxResults(limit);
        }

        return query.getResultList();

//                "select new  jj.chat_spring.domain.ChatMessageDto( cm.id ,cm.message, cm.roomId ,cm.isRead ,cm.createdAt , cm.updatedAt, new UserSimpleDto(u.id, u.username, u.avatar , u.firstName , u.lastName , u.email))" +
//                        "from ChatMessage cm \n" +
//                        "inner join User u  \n" +
//                        "on u.id = cm.user.id \n" +
//                        "where cm.roomId = :roomId \n" +
//                        "order by cm.id desc ";

//        "select cm.id ,cm.message, cm.roomId ,cm.isRead ,cm.createdAt , cm.updatedAt \n" +
//                ", u.id, u.username, u.avatar , u.firstName , u.lastName , u.email \n" +
//                "from ChatMessage cm \n" +
//                "inner join User u  \n" +
//                "on u.id = cm.user.id \n" +
//                "where cm.roomId = :roomId \n" +
//                "order by cm.id desc ";


    }


    @Override
    public Long getTotalChatMessageById(Long chatRoomId) {
        String jpql =
                """
                    select count (cm)
                    from ChatMessage cm
                    where cm.roomId = :roomId
                    """;


        return em.createQuery(jpql, Long.class)
                .setParameter("roomId", chatRoomId)
                .getSingleResult();
    }
}
