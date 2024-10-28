package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.entity.TransactionHistory;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Integer> {
    @Modifying
    @Transactional
    @Query("UPDATE TransactionHistory SET status = :status WHERE description = :orderCode")
    void updatePaymentuser(@Param("status") Boolean status, @Param("orderCode") String orderCode);

    TransactionHistory findByUsername(String userName);

    List<TransactionHistory> findAllByUsername(String userName);
}