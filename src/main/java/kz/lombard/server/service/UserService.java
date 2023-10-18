package kz.lombard.server.service;


import kz.lombard.server.dto.RegistrationUserDto;
import kz.lombard.server.entity.User;
import kz.lombard.server.exception.AppError;
import kz.lombard.server.repository.RoleRepository;
import kz.lombard.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
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
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("Пользователь '%s' не найден!", email)
        ));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList())
        );
    }

    public User createNewUser(RegistrationUserDto registrationUserDto) {
        User user = new User();
        user.setItin(registrationUserDto.getItin());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setPhone(registrationUserDto.getPhone());
        user.setFirstname(registrationUserDto.getFirstname());
        user.setLastname(registrationUserDto.getLastname());
        user.setEmail(registrationUserDto.getEmail());
        user.setRoles(List.of(roleRepository.findByName("ROLE_USER").get()));

        userRepository.save(user);
        return user;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public Optional<User> findByItin(Long itin) {
        return userRepository.findByItin(itin);
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

    public ResponseEntity<?> getUserFromDjangoApiByItin(Long itin) {
        String apiUrl = "http://185.125.88.26:3000/api/user/" + itin;
        try {
            ResponseEntity<User> response = restTemplate.getForEntity(apiUrl, User.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println("s");
                return ResponseEntity.ok(response.getBody());
            } else {
                return null;
            }
        } catch (HttpClientErrorException.NotFound notFoundException) {
            // Обработка ошибки 404 (Not Found)
            return ResponseEntity.notFound().build();
        } catch (HttpClientErrorException httpException) {
            // Обработка других ошибок HTTP
            return ResponseEntity.status(httpException.getStatusCode()).body(httpException.getResponseBodyAsString());
        } catch (Exception e) {
            // Обработка других исключений
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

}
