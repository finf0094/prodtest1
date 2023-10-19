package kz.moderation.server.service;

import kz.moderation.server.entity.Resume;
import kz.moderation.server.entity.User;
import kz.moderation.server.repository.ResumeRepository;
import kz.moderation.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    public List<String> getResumesForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getPrincipal().toString()).orElseThrow();

        List<Resume> userResumes = resumeRepository.findAllByIin(user.getItin());

        List<String> resumeIds = userResumes.stream()
                .map(Resume::getId)
                .collect(Collectors.toList());

        return resumeIds;
    }

}
