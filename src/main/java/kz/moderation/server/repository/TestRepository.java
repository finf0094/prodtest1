package kz.moderation.server.repository;


import kz.moderation.server.entity.RefreshToken;
import kz.moderation.server.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    Optional<Test> findById(Long id);
    Optional<Test> findByIin(String iin);
//    Optional<Test> findByUserInfoEmail(String email);
}
