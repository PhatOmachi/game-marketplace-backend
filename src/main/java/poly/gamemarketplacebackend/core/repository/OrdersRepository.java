package poly.gamemarketplacebackend.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.gamemarketplacebackend.core.entity.Orders;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {

    @Query("SELECT o FROM Orders o WHERE o.users.username = :username order by o.sysIdOrder asc")
    List<Orders> findByUsersUsername(String username);

    @Query("SELECT o from Orders o join Users u on o.users = u order by o.sysIdOrder asc")
    List<Orders> findAll();

    @Query("SELECT o from Orders o join Users u on o.users = u where o.sysIdOrder = :sysIdOrder order by o.sysIdOrder asc")
    Orders findBySysIdOrder(@Param("sysIdOrder") Integer sysIdOrder);

    List<Orders> findByOrderCode(String orderCode);

    @Procedure(procedureName = "public.get_analytics_summary")
    List<Object[]> getAnalyticsSummary(); // Trả về mảng Object (sẽ chứa các giá trị OUT)

}
