package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import poly.gamemarketplacebackend.core.entity.TransactionHistory;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Integer> {
    @Modifying
    @Transactional
    @Query("UPDATE TransactionHistory SET status = :status, userBalance = :userBalance WHERE description = :orderCode")
    void updatePaymentuser(@Param("status") Boolean status, @Param("userBalance") double userBalance, @Param("orderCode") String orderCode);

    TransactionHistory findByUsername(String userName);

    List<TransactionHistory> findAllByUsername(String userName);

    List<TransactionHistory> findByDescriptionStartingWithAndUsername(String descriptionPrefix, String username);

    Optional<TransactionHistory> findByDescription(String description);

    @Query(value = "SELECT * FROM get_transaction_statistics()", nativeQuery = true)
    List<Object[]> getTransactionStatistics();

    @Query(value = "SELECT * FROM get_transaction_sums()", nativeQuery = true)
    List<Object[]> getTransactionSums();

    @Query(value = "SELECT * FROM get_transaction_summary()", nativeQuery = true)
    List<Object[]> getTransactionSummary();

}