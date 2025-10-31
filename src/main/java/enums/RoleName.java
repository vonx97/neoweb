package enums;

import org.springframework.security.core.GrantedAuthority;

public enum RoleName implements GrantedAuthority {
    ADMIN,
    USER;

    @Override
    public String getAuthority() {
        return name(); // Spring Security için gerekli
    }
}
