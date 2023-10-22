package kz.moderation.server.controller;


import kz.moderation.server.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/getCurrentUserTest")
    public ResponseEntity<?> getUserTest() {
        try {
            return ResponseEntity.ok(testService.findTestByCurrentUser());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{iin}/markTestCompleted")
    public ResponseEntity<?> markTestCompleted(@PathVariable String iin) {
        return testService.markTestAsCompleted(iin);
    }
}