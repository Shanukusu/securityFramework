package ru.shanina.securityframework.core_iam;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JwtTokenProvider.class)
@TestPropertySource(properties = {"securitas.jwt.secret=testSecretKeyForHS512AlgorithmMustBeAtLeast512BitsLong1234567890"})
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Test
    public void testGenerateAndValidateToken() {
        UserDetails user = User.withUsername("test").password("pass").roles("USER").build();
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        String token = tokenProvider.generateToken(auth);
        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("test", tokenProvider.getUsernameFromToken(token));
    }

    @Test
    public void testInvalidToken() {
        assertFalse(tokenProvider.validateToken("invalid"));
    }
}
