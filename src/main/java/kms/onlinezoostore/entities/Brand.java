package kms.onlinezoostore.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "brands", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @Where(clause = "owner_class = 'Brand'")
    private List<AttachedFile> images;

    public Brand(Long id, String name) {
        this.id = id;
        this.name = name;
        images = new ArrayList<>(1);
    }

    public Brand(Long id, String name, List<AttachedFile> images) {
        this.id = id;
        this.name = name;
        if (images != null && !images.isEmpty()) {
            this.images = Collections.singletonList(images.get(0));
        }
    }
}
