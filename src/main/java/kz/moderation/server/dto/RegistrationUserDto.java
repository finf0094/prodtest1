package kz.moderation.server.dto;


import lombok.Data;

@Data
public class RegistrationUserDto {
    private Long iin;
    private String email;
    private String password;
}
