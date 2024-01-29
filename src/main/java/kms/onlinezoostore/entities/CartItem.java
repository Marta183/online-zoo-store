package kms.onlinezoostore.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "shopping_cart_items", uniqueConstraints = @UniqueConstraint(columnNames = {"shopping_cart_id", "product_id"}))
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shopping_cart_id", referencedColumnName = "id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id", nullable = false)
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    public double getPrice() {
        return (this.product.getPriceWithDiscount() != null) ?
                    this.product.getPriceWithDiscount() :
                    this.product.getPrice();
    }

    public double getSum() {
        return this.quantity * getPrice();
    }
}
