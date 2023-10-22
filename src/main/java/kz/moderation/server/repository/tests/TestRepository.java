package kz.moderation.server.repository.tests;


import kz.moderation.server.entity.tests.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("testQuestionRepository")
public interface TestRepository extends JpaRepository<Test, Long> {
}
