package kms.onlinezoostore.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
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
@Table(name = "colors", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @Where(clause = "owner_class = 'Color'")
    private List<AttachedFile> images;

    public Color(Long id, String name) {
        this.id = id;
        this.name = name;
        images = new ArrayList<>(1);
    }

    public Color(Long id, String name, List<AttachedFile> images) {
        this.id = id;
        this.name = name;
        if (images != null && !images.isEmpty()) {
            this.images = Collections.singletonList(images.get(0));
        }
    }
}
