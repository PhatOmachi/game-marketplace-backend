package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.entity.Account;

import java.time.LocalDate;


public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByUsernameOrEmailAndIsEnabledTrue(@Param("username") String username, @Param("email") String email);

    Account findByUsername(@Param("username") String username);

    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.roles WHERE a.username = :username OR a.email = :username")
    Account findByUsernameQuerySecurity(@Param("username") String username);

    Account findByEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.hashPassword = :hashPassword WHERE a.email = :email")
    int updatePassAccountByEmail(@Param("hashPassword") String hashPassword, @Param("email") String email);

    @Query(value = "SELECT insert_user_and_role(:username, :email, :role)", nativeQuery = true)
    void insertUserAndRole(@Param("username") String username, @Param("email") String email, @Param("role") String role);

    @Query(value = "SELECT insert_account_user_and_role(:username, :email, :hashPassword, :hoVaTen, :phoneNumber)", nativeQuery = true)
    void insertAccountUserAndRole(@Param("username") String username,
                                  @Param("email") String email,
                                  @Param("hashPassword") String hashPassword,
                                  @Param("hoVaTen") String hoVaTen,
                                  @Param("phoneNumber") String phoneNumber);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.email = :email WHERE a.username = :username")
    int updateEmailByUsername(@Param("email") String email, @Param("username") String username);


}