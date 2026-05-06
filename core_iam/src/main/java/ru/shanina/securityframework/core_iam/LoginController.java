package ru.shanina.securityframework.core_iam;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public LoginController(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @PublicEndpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        String token = tokenProvider.generateToken(auth);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    public static class LoginRequest {
        private String username;
        private String password;

        // getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginResponse {
        private String token;

        public LoginResponse(String token) { this.token = token; }
        public String getToken() { return token; }
    }
}
