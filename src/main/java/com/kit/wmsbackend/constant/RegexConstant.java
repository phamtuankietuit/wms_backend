package com.kit.wmsbackend.constant;

public final class RegexConstant {
    public static final String PASSWORD_REGEX = "/^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\W_]).{8,}$/";
    public static final String PHONE_REGEX = "^(?:\\+84|0084|0)[235789]\\d{8}$";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
}
