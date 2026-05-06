package ru.shanina.securityframework.sample_app;

import ru.shanina.securityframework.core_audit.Audit;
import ru.shanina.securityframework.core_iam.PublicEndpoint;
import ru.shanina.securityframework.core_iam.Authorize;
import ru.shanina.securityframework.core_iam.Role;
import lombok.RequiredArgsConstructor;
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
    @Authorize(Role.USER)
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }

    @Audit(event = "ADMIN_ACTION", level = "WARN")
    @Authorize(Role.ADMIN)
    @GetMapping("/admin")
    public String adminOnly() {
        return "Admin access granted";
    }
}
