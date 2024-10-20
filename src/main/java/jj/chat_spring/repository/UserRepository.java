package jj.chat_spring.repository;

import jakarta.persistence.Tuple;
import jj.chat_spring.domain.User;
import jj.chat_spring.domain.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

//    @Query("SELECT u FROM User u WHERE u.username = :username")
    User findByUsername(String username);

    @Query("SELECT new jj.chat_spring.domain.UserDto(u.id, u.username,u.avatar, u.firstName, u.lastName,u.email, u.joinedAt, u.updatedAt) FROM User u WHERE u.id = :id")
    UserDto findUserV1ById(@Param("id") Long userId);

    @Query("SELECT u.id, u.password, u.email FROM User u WHERE u.username = :username")
    Object findV2ByUsername(@Param("username") String username);

    @Query("SELECT u.id, u.password, u.email FROM User u WHERE u.username = :username")
    Object[] findV3ByUsername(@Param("username") String username);

    @Query("SELECT u.id, u.password, u.email, u.avatar, u.firstName, u.lastName FROM User u WHERE u.username = :username")
    Tuple findByUsernameTuple(@Param("username") String username);

    @Query("SELECT count(u) FROM User u WHERE u.id = :id")
    int isUser(@Param("id") Long id);

    @Query("SELECT u.password FROM User u WHERE u.username = :username" )
    String checkPassword(@Param("username") String username);
}
