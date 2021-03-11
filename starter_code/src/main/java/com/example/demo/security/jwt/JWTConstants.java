package com.example.demo.security.jwt;

public class JWTConstants {
    public static final String PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    public static final String SECRET = "SomeSecret#123";
    public static final long EXPIRATION_TIME = (10 * 24 * 60 * 60 * 1000); // (10 days)
}
