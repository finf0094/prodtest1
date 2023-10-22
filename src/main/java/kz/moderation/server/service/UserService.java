package kz.moderation.server.service;


import kz.moderation.server.config.CustomUserDetails;
import kz.moderation.server.dto.RegistrationUserDto;
import kz.moderation.server.entity.User;
import kz.moderation.server.repository.RoleRepository;
import kz.moderation.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private static String djangoApiGetAllUser = "http://185.125.88.26:3000/api/users/";
    private static String djangoApiGetUserByItin = "http://185.125.88.26:3000/api/user/";

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private RestTemplate restTemplate;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    @Transactional
    public CustomUserDetails loadUserByUsername(String itin) throws UsernameNotFoundException {
        User user = findByItin(itin).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь с ИИН '%s' не найден!", itin)
        ));

        CustomUserDetails customUserDetails = new CustomUserDetails(
                user.getItin(),
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );

        return customUserDetails;
    }


    public User save(User user) {
        return userRepository.save(user);
    }


    // пока не использую
    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setItin(registrationUserDto.getIin());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setEmail(registrationUserDto.getEmail());
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));



        userRepository.save(user);
        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Optional<User> findByItin(String itin) {
        return userRepository.findByItin(itin);
    }
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User[] getUsersFromDjangoApi() {
        String apiUrl = "http://185.125.88.26:3000/api/users/";
        ResponseEntity<User[]> response = restTemplate.getForEntity(apiUrl, User[].class);


        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to fetch user data from the API");
        }
    }

    public User getUserFromDjangoApiByItin(String itin) {
        String apiUrl = "http://185.125.88.26:3000/api/user/" + itin;
        try {
            ResponseEntity<User> response = restTemplate.getForEntity(apiUrl, User.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (HttpClientErrorException.NotFound notFoundException) {
            return null;
        }
    }

    public ResponseEntity<? extends Object> getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // если не авторизован выкидываем ошибку
        if (!authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // находим в базе пользователя
        User user = userRepository.findByEmail(authentication.getPrincipal().toString()).orElseThrow();

        return ResponseEntity.ok(user);
    }


}
