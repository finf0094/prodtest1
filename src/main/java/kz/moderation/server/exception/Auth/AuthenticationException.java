package kz.moderation.server.exception.Auth;



public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}