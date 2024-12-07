package org.bbqqvv.backendecommerce.config.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {
	private SecurityUtils() {}

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
	public static Optional<String> getCurrentUserLogin() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    if (authentication != null && authentication.getName() != null) {
	        return Optional.of(authentication.getName());
	    }
	    return Optional.empty();
	}
}
