package kz.moderation.server.repository;

import kz.moderation.server.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, String> {
    Optional<Resume> findByIin(String iin);
}
