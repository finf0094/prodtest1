package kz.moderation.server.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CollectionId;

@Entity
@Table(name = "Resume")
@NoArgsConstructor
@Getter
@Setter
public class Resume {
    @Id
    private String id;

    @Column(unique = true)
    private String iin;
    private String fileName;
    private String filePath;
}