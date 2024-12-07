package org.bbqqvv.backendecommerce.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateUtils {
	// Khai báo logger
    private static final Logger logger = LoggerFactory.getLogger(ValidateUtils.class);

    public static void validateUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            logger.error("Username is empty or null. Please provide a valid username.");
            throw new IllegalArgumentException("Username is required and cannot be empty.");
        }
    }
}
