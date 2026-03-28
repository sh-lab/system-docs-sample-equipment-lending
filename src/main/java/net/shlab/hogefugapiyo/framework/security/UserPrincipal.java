package net.shlab.hogefugapiyo.framework.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import net.shlab.hogefugapiyo.equipmentlending.model.value.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 認証済み利用者情報を表す UserDetails。
 */
public record UserPrincipal(String userId, UserRole roleCode) implements UserDetails, Serializable {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleCode.code()));
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return userId;
    }
}
