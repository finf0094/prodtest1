package kz.moderation.server.dto;

import kz.moderation.server.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Long iin;
    private String firstname;
    private String lastname;
    private String phone;
    private List<Role> roles;
    private String email;
    private String position;
}
