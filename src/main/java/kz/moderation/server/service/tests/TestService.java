package kz.moderation.server.service.tests;


import kz.moderation.server.entity.tests.Question;
import kz.moderation.server.entity.tests.Test;
import kz.moderation.server.repository.tests.QuestionRepository;
import kz.moderation.server.repository.tests.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("testQuestionService")
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;
    private final QuestionRepository questionRepository;

    public void createTest(Test testRequest) {
        Test test = new Test();
        Question question = new Question();


    }

    public List<Test> findAllTests() {
        return testRepository.findAll();
    }

    public void save(Test test) {
        testRepository.save(test);
    }

}
