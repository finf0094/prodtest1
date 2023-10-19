package kz.moderation.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Resume")
@NoArgsConstructor
@Getter
@Setter
public class Resume {
    @Id
    private String id;
    private Long iin;
    private String fileName;
    private String filePath;
}