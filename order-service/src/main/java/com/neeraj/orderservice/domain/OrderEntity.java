package com.neeraj.orderservice.domain;

import com.neeraj.orderservice.domain.models.Address;
import com.neeraj.orderservice.domain.models.Customer;
import com.neeraj.orderservice.domain.models.OrderStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_id_generator")
    @SequenceGenerator(name = "order_id_generator", sequenceName = "order_id_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "username", nullable = false)
    private String userName;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItemEntity> orderItems;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "name", column = @Column(name = "customer_name")),
        @AttributeOverride(name = "email", column = @Column(name = "customer_email")),
        @AttributeOverride(name = "phone", column = @Column(name = "customer_phone"))
    })
    private Customer customer;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "addressLine1", column = @Column(name = "delivery_address_line1")),
        @AttributeOverride(name = "addressLine2", column = @Column(name = "delivery_address_line2")),
        @AttributeOverride(name = "city", column = @Column(name = "delivery_address_city")),
        @AttributeOverride(name = "state", column = @Column(name = "delivery_address_state")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "delivery_address_zip_code")),
        @AttributeOverride(name = "country", column = @Column(name = "delivery_address_country"))
    })
    private Address deliveryAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String comments;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
