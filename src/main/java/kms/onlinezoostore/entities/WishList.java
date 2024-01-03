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
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "wish_lists", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}))
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "wishList", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WishListItem> items = new HashSet<>();

    public WishListItem getItemByProductId(Long productId) {
        return this.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findAny().orElse(null);
    }
}
