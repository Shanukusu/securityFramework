package ru.shanina.securityframework.sample_app;

import ru.shanina.securityframework.sample_app.User;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final Map<Long, User> store = new ConcurrentHashMap<>();

    public UserService() {
        store.put(1L, new User(1L, "Alice"));
        store.put(2L, new User(2L, "Bob"));
    }

    public User findById(Long id) {
        return store.get(id);
    }
}
