package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.entity.Users;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Users findByUsername(@Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.email = :email, u.hoVaTen = :hoVaTen, u.avatar = :avatar, u.gender = :gender, u.DOB = :dob, u.phoneNumber = :phoneNumber WHERE u.username = :username")
    int updateUsersByUsername(
            @Param("email") String email,
            @Param("hoVaTen") String hoVaTen,
            @Param("avatar") String avatar,
            @Param("gender") boolean gender,
            @Param("dob") LocalDate dob,
            @Param("phoneNumber") String phoneNumber,
            @Param("username") String username
    );


    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.balance = :balance WHERE u.username = :username")
    void updateUsersByUsername(@Param("balance") String balance, @Param("username") String username);

    @Query("SELECT u FROM Users u order by u.sysIdUser asc")
    List<Users> findAll();

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.avatar = :avatar WHERE u.username = :username")
    void updateAvatarByUsername(@Param("avatar") String avatar, @Param("username") String username);

    @Modifying
    @Transactional
    @Query("UPDATE Users u set u.avatar = :avatar, u.hoVaTen = :hoVaTen where u.username = :username")
    void updateAvatarAndHoVaTenByUsername(@Param("avatar") String avatar,
                                          @Param("hoVaTen") String hoVaTen,
                                          @Param("username") String username);

    @Query(value = "SELECT * FROM get_user_statistics()", nativeQuery = true)
    List<Object[]> getUserStatistics();
}
