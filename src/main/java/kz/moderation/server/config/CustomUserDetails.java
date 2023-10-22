package kz.moderation.server.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    private String iin;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String iin, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.iin = iin;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public String getIin() {
        return iin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        // Реализуйте проверку срока действия аккаунта (например, true, если аккаунт действителен)
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Реализуйте проверку, заблокирован ли аккаунт (например, true, если аккаунт не заблокирован)
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Реализуйте проверку срока действия учетных данных (например, true, если учетные данные действительны)
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Реализуйте проверку, включен ли аккаунт (например, true, если аккаунт включен)
        return true;
    }
}
