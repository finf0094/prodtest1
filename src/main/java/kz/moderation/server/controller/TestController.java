package kz.moderation.server.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping(name = "api/v1/test")
public class TestController {

    @GetMapping("/unsecured")
    public String unsecured() {
        return "unsecured";
    }


    @GetMapping("/secured")
    public String secured() {
        return "secured";
    }


    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/info")
    public String info(Principal principal) {
        return "info";
    }
}
