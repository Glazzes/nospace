package com.nospace.security;

public enum CookieType {
    AUTHORIZATION_TOKEN("Authorization", 900),
    REFRESH_TOKEN("Refresh",604800);

    private final String cookieName;
    private final Integer expirationTimeInSeconds;
    CookieType(String cookieName, Integer expirationTimeInSeconds){
        this.cookieName = cookieName;
        this.expirationTimeInSeconds = expirationTimeInSeconds;
    }

    public String getCookieName(){
        return cookieName;
    }

    public Integer getExpirationTimeInSeconds(){
        return expirationTimeInSeconds;
    }
}
