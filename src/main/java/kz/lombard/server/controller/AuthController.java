package kz.lombard.server.controller;

import kz.lombard.server.dto.*;
import kz.lombard.server.entity.User;
import kz.lombard.server.exception.AppError;
import kz.lombard.server.service.UserService;
import kz.lombard.server.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authRequest.getEmail(), authRequest.getPassword()
            ));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.UNAUTHORIZED.value(), "Неправильный логин или пароль"), HttpStatus.UNAUTHORIZED);
        }

        UserDetails userDetails = userService.loadUserByUsername(authRequest.getEmail());
        String token = jwtTokenUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(token, userDetails.getUsername()));
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        // Проверка Джанго
        if (userService.getUserFromDjangoApiByItin(registrationUserDto.getItin()) == null) {
             return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "BAD REQUEST"), HttpStatus.BAD_REQUEST);
        }

        // Проверка пароля
        if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пароли не совпадают!"), HttpStatus.BAD_REQUEST);
        }

        if (userService.findByEmail(registrationUserDto.getEmail()).isPresent()) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Пользователь с такой почтой уже существует!"), HttpStatus.BAD_REQUEST);
        }

        User user = userService.createNewUser(registrationUserDto);
        return ResponseEntity.ok(new UserInfo(
                user.getItin(),
                user.getFirstname(),
                user.getLastname(),
                user.getPhone(),
                user.getRoles()
                        .stream()
                        .map(role -> role)
                        .collect(Collectors.toList()),
                user.getEmail()
        ));
     }

     @GetMapping("/user-info")
     public ResponseEntity<?> getUserData() {
         // получаем из фильтра данные ползователя
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

         // если не авторизован выкидываем ошибку
         if (!authentication.isAuthenticated()) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }

         UserInfo userInfo = new UserInfo();

         // находим в базе пользователя
         User user = userService.findByEmail(authentication.getPrincipal().toString()).orElseThrow();

         userInfo.setItin(user.getItin());
         userInfo.setEmail(user.getEmail());
         userInfo.setFirstName(user.getFirstname());
         userInfo.setRoles(user.getRoles());
         userInfo.setPhoneNumber(user.getPhone());

         return ResponseEntity.ok(userInfo);
     }

}
