package net.shlab.hogefugapiyo.framework.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import net.shlab.hogefugapiyo.equipmentlending.infrastructure.security.UserPrincipal;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class SecurityMockMvcTestSupport {

    private SecurityMockMvcTestSupport() {
    }

    public static RequestPostProcessor userPrincipal(String userId, UserRole roleCode) {
        UserPrincipal userPrincipal = new UserPrincipal(userId, roleCode);
        return authentication(UsernamePasswordAuthenticationToken.authenticated(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        ));
    }
}
