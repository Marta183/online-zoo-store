package kms.onlinezoostore.entities;

import jakarta.persistence.*;
import kms.onlinezoostore.entities.enums.DeliveryMethod;
import kms.onlinezoostore.entities.enums.DeliveryService;
import kms.onlinezoostore.entities.enums.OrderStatus;
import kms.onlinezoostore.entities.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "payment_option", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "delivery_service", nullable = false)
    private DeliveryService deliveryService;

    @Column(name = "delivery_method", nullable = false)
    private DeliveryMethod deliveryMethod;

    @ManyToOne
    @JoinColumn(name = "delivery_address_id", referencedColumnName = "id", nullable = false)
    private Address deliveryAddress;

    @Column(name = "delivery_number")
    private String deliveryNumber;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}
