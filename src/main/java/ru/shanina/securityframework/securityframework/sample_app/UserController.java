package ru.shanina.securityframework.securityframework.sample_app;

import ru.shanina.securityframework.securityframework.sample_app.User;
import ru.shanina.securityframework.securityframework.sample_app.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PublicEndpoint
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @Audit(event = "GET_USER", level = "INFO")
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
}
