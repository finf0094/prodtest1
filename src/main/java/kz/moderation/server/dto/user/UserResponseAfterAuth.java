package kz.moderation.server.dto.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseAfterAuth {
    private String iin;
    private String email;
    private List<String> roles;
}

