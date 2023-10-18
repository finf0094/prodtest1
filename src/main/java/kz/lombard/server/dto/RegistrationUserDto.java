package kz.lombard.server.dto;


import lombok.Data;

@Data
public class RegistrationUserDto {
    private Long itin;
    private String firstname;
    private String lastname;
    private String email;

    private String password;
    private String confirmPassword;

    private String phone;
}
