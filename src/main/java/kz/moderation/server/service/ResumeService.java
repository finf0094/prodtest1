package kz.moderation.server.service;

import kz.moderation.server.entity.Resume;
import kz.moderation.server.entity.User;
import kz.moderation.server.repository.ResumeRepository;
import kz.moderation.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public List<Resume> getResumesForUser(String iin) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Resume" + iin);
        User user = userRepository.findByItin(iin).orElseThrow();


        List<Resume> userResume = resumeRepository.findAllByIin(user.getItin());

//        List<String> resumeIds = userResumes.stream()
//                .map(Resume::getId)
//                .collect(Collectors.toList());

        return userResume;
    }

}
