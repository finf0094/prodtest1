package kz.moderation.server.controller.tests;


import kz.moderation.server.dto.Test.TestRequest;
import kz.moderation.server.entity.tests.Test;
import kz.moderation.server.service.tests.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestQuestionController {
    private final TestService testService;

    @GetMapping
    public List<Test> getAllTests() {
        return testService.findAllTests();
    }

    @PostMapping
    public ResponseEntity<?> createTest(TestRequest testRequest) {
        Test test = new Test();
        test.setTestName(test.getTestName());
        testService.save(test);
        return ResponseEntity.ok("Success");
    }
}
