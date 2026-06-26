package com.systemdmove.session;

import com.systemdmove.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Substitui o SessionManager do spec desktop: obtem o usuario autenticado
 * a partir do SecurityContext (preenchido pelo JwtAuthFilter).
 */
public final class SecurityUtil {

    private SecurityUtil() {
    }

    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Long userId)) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Usuario nao autenticado");
        }
        return userId;
    }
}
