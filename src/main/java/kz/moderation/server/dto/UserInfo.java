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
    private Long itin;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private List<Role> roles;
    private String email;
}
