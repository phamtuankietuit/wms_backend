package com.kit.wmsbackend.constant;

public final class MessageConstant {
    public static final class Server {
        public static final String INTERNAL_SERVER_ERROR = "Internal Server Error.";
    }
    public static final class Authentication {
        public static final String INVALID_CREDENTIALS = "Invalid username or password.";
        public static final String UNAUTHORIZED = "Unauthorized.";
        public static final String INVALID_OR_EXPIRED_TOKEN = "Invalid or expired token.";
    }
    public static final class Authorization {
        public static final String DENIED = "You do not have permission to access this resource.";
    }
    public static final class NotFound  {
        public static final String RESOURCE_NOT_FOUND = "Resource not found.";
        public static final String USER_NOT_FOUND = "User not found.";
    }
}
