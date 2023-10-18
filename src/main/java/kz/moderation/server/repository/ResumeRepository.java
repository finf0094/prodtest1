package kz.moderation.server.repository;

import kz.moderation.server.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, String> {
}
