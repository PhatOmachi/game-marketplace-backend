package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.entity.Users;

import java.time.LocalDateTime;

@Repository
public interface UsersRepository extends JpaRepository<Users , Integer> {
    Users findByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.email = :email, u.hoVaTen = :hoVaTen, u.balance = :balance, u.joinTime = :joinTime, u.avatar = :avatar WHERE u.username = :username")
    int updateUsersByUsername(
            @Param("email") String email,
            @Param("hoVaTen") String hoVaTen,
            @Param("balance") String balance,
            @Param("joinTime") LocalDateTime joinTime,
            @Param("avatar") String avatar,
            @Param("username") String username
    );

}
