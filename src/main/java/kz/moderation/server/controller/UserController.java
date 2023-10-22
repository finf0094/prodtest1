package kz.moderation.server.controller;


import kz.moderation.server.dto.ResponseDto;
import kz.moderation.server.dto.UserInfo;
import kz.moderation.server.entity.Role;
import kz.moderation.server.entity.User;
import kz.moderation.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/user/{iin}")
    public UserInfo getUserData(@PathVariable String iin) {
        UserInfo userInfo = new UserInfo();

        // находим в базе пользователя
        User user = userService.findByItin(iin).get();

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        userInfo.setItin(user.getItin());
        userInfo.setEmail(user.getEmail());
        userInfo.setFirstName(user.getFirstname());
        userInfo.setRoles(roles);
        userInfo.setPhoneNumber(user.getPhone());
        userInfo.setLastName(user.getLastname());
        userInfo.setPosition(user.getPosition());

        return userInfo;
    }


    @GetMapping("/user-info")
    public ResponseEntity<?> getUserData() {
        // получаем из фильтра данные ползователя
         Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//         // если не авторизован выкидываем ошибку
         if (!authentication.isAuthenticated()) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }

        UserInfo userInfo = new UserInfo();

        // находим в базе пользователя
        User user = userService.findByEmail(authentication.getName()).get();

        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        userInfo.setItin(user.getItin());
        userInfo.setEmail(user.getEmail());
        userInfo.setFirstName(user.getFirstname());
        userInfo.setRoles(roles);
        userInfo.setPhoneNumber(user.getPhone());
        userInfo.setLastName(user.getLastname());
        userInfo.setPosition(user.getPosition());

        return ResponseEntity.ok(userInfo);
    }
}
