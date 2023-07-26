package kms.onlinezoostore.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "product_categories")
public class ProductCategory {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name should not be empty")
    @Size(max = 150, message = "Name should be less then 150 characters")
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private ProductCategory parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<ProductCategory> innerCategories;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;

    protected ProductCategory() {
    }

    public ProductCategory(String name) {
        this.name = name;
    }

    public ProductCategory(String name, ProductCategory parent) {
        this.name = name;
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "ProductCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parent=" + parent +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductCategory getParent() {
        return parent;
    }

    public void setParent(ProductCategory parent) {
        this.parent = parent;
    }

    public List<ProductCategory> getInnerCategories() {
        return innerCategories;
    }

    public void setInnerCategories(List<ProductCategory> innerCategories) {
        this.innerCategories = innerCategories;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
