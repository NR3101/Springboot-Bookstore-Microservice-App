package com.neeraj.orderservice.domain;

import com.neeraj.orderservice.domain.models.OrderStatus;
import com.neeraj.orderservice.domain.models.OrderSummary;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByStatus(OrderStatus orderStatus);

    Optional<OrderEntity> findByOrderNumber(String orderNumber);

    default void updateOrderStatus(String orderNumber, OrderStatus orderStatus) {
        OrderEntity order = this.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderNumber));
        order.setStatus(orderStatus);
        save(order);
    }

    @Query(
            """
            SELECT new com.neeraj.orderservice.domain.models.OrderSummary(o.orderNumber, o.status)
            FROM OrderEntity o
            WHERE o.userName = :userName
            """)
    List<OrderSummary> findByUserName(String userName);

    @Query(
            """
            SELECT distinct o
            FROM OrderEntity o left join fetch o.orderItems
            WHERE o.userName = :userName AND o.orderNumber = :orderNumber
            """)
    Optional<OrderEntity> findByUserNameAndOrderNumber(String userName, String orderNumber);
}
