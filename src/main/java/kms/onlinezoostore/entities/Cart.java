package kms.onlinezoostore.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "shopping_carts", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public double getTotalPrice() {
        return items.stream()
                .mapToDouble(CartItem::getSum)
                .sum();
    }

    public List<Product> getProducts() {
        return items.stream()
                .map(CartItem::getProduct)
                .collect(Collectors.toList());
    }

    public CartItem getItemByProductId(Long productId) {
        return this.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findAny().orElse(null);
    }
}
