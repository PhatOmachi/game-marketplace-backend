package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.entity.Account;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Account findByUsernameOrEmailAndIsEnabledTrue(@Param("username") String username, @Param("email") String email);

    // Changed to JPA method naming
    Account findByUsername(@Param("username") String username);

    // Changed to JPQL
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.roles WHERE a.username = :username OR a.email = :username")
    Account findByUsernameQuerySecurity(@Param("username") String username);

    // Changed to JPA method naming
    Account findByEmail(@Param("email") String email);

    // Changed to JPQL
    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.hashPassword = :hashPassword WHERE a.email = :email")
    int updatePassAccountByEmail(@Param("hashPassword") String hashPassword, @Param("email") String email);

    @Query(value = "SELECT insert_user_and_role(:username, :email, :role)", nativeQuery = true)
    void insertUserAndRole(@Param("username") String username, @Param("email") String email, @Param("role") String role);

}