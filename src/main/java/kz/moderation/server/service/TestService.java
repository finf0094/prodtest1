package kz.moderation.server.service;

import kz.moderation.server.entity.Test;
import kz.moderation.server.entity.User;
import kz.moderation.server.exception.AppError;
import kz.moderation.server.repository.TestRepository;
import kz.moderation.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestService {
    private final TestRepository testRepository;
    private final UserRepository userRepository;

    public Optional<Test> findByIin(String itin) {
        return testRepository.findByIin(itin);
    }

    public Test findTestByCurrentUser() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            User user = userRepository.findByEmail(authentication.getName()).get();
                return findByIin(user.getItin()).orElseThrow(Exception::new);
        }
        return null;
    }



    public ResponseEntity<?> markTestAsCompleted(String iin) {
        Optional<Test> testOptional = findByIin(iin);

        if (!testOptional.isPresent()) {
            new ResponseEntity<>(new AppError(
                    HttpStatus.BAD_REQUEST.value(), "Тест не найден"
            ), HttpStatus.BAD_REQUEST);
        }

        if (!testOptional.get().isCompleted()) {
            new ResponseEntity<>(new AppError(
                    HttpStatus.BAD_REQUEST.value(), "Тест уже выполнен!"
            ), HttpStatus.BAD_REQUEST);
        }

        Test test = testOptional.get();
        test.setCompleted(true);
        testRepository.save(test);
        return ResponseEntity.ok("Successful");
    }
}

