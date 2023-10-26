package kms.onlinezoostore.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "product_categories", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "parent_id"}))
public class ProductCategory {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private ProductCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE)
    private Set<ProductCategory> innerCategories;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Set<Product> products;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @Where(clause = "owner_class = 'ProductCategory'")
    private List<AttachedFile> images;

    @Transient
    private Long productCount;

    public ProductCategory(Long id, String name, ProductCategory parent) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        images = new ArrayList<>(1);
    }

    public ProductCategory(Long id, String name, ProductCategory parent, List<AttachedFile> images) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        if (images != null && !images.isEmpty()) {
            this.images = Collections.singletonList(images.get(0));
        }
    }
}
