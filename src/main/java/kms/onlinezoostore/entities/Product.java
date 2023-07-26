package kms.onlinezoostore.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotBlank(message = "Name should not be empty")
    @Size(max = 150, message = "Name should be less then 150 characters")
    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private ProductCategory category;

    @ManyToOne
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "color_id", referencedColumnName = "id")
    private Color color;

    @ManyToOne
    @JoinColumn(name = "material_id", referencedColumnName = "id")
    private Material material;

    @ManyToOne
    @JoinColumn(name = "weight_id", referencedColumnName = "id")
    private Weight weight;

    @ManyToOne
    @JoinColumn(name = "size_id", referencedColumnName = "id")
    private ProductSize productSize;

    @Column(name = "description")
    private String description;
    @Column(name = "prescription")
    private String prescription;

    @Column(name = "price")
    private Double price;

    @Column(name = "new_arrival")
    private Boolean newArrival;
    @Column(name = "not_available")
    private Boolean notAvailable;

    protected Product() {
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", brand=" + brand +
                ", color=" + color +
                ", material=" + material +
                ", weight=" + weight +
                ", productSize=" + productSize +
                ", description='" + description + '\'' +
                ", prescription='" + prescription + '\'' +
                ", price=" + price +
                ", newArrival=" + newArrival +
                ", notAvailable=" + notAvailable +
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

    public ProductCategory getCategory() {
        return category;
    }
    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getNewArrival() {
        return newArrival;
    }
    public void setNewArrival(Boolean newArrival) {
        this.newArrival = newArrival;
    }

    public Boolean getNotAvailable() {
        return notAvailable;
    }
    public void setNotAvailable(Boolean notAvailable) {
        this.notAvailable = notAvailable;
    }

    public Brand getBrand() {
        return brand;
    }
    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public Color getColor() {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public Material getMaterial() {
        return material;
    }
    public void setMaterial(Material material) {
        this.material = material;
    }

    public Weight getWeight() {
        return weight;
    }
    public void setWeight(Weight weight) {
        this.weight = weight;
    }

    public ProductSize getProductSize() {
        return productSize;
    }
    public void setProductSize(ProductSize productSize) {
        this.productSize = productSize;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrescription() {
        return prescription;
    }
    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }
}
