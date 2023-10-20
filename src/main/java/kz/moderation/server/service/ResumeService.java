package kz.moderation.server.service;

import kz.moderation.server.entity.Resume;
import kz.moderation.server.entity.User;
import kz.moderation.server.repository.ResumeRepository;
import kz.moderation.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    public ResponseEntity<?> getResumeForUser(String iin) {
        User user = userRepository.findByItin(iin).orElse(null); // Используйте orElse(null), чтобы избежать выбрасывания исключения, если пользователя не найдено.

        System.out.println(user);

        if (user != null) {
            Resume userResume = resumeRepository.findByIin(user.getItin()).orElse(null);
            System.out.println(userResume);
            if (userResume != null) {
                return ResponseEntity.ok(userResume);
            } else {
                // Отправьте сообщение, что резюме отсутствует
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Резюме не найдено");
            }
        } else {
            // Отправьте сообщение, что пользователя не найдено
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь не найден");
        }
    }

}
