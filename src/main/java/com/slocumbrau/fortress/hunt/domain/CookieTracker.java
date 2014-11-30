package com.slocumbrau.fortress.hunt.domain;

import java.util.Arrays;

import javax.servlet.http.Cookie;

public class CookieTracker {

    public static final String FORTRESS_COOKIE = "fortress_hunt_cookie";
    private Cookie cookie;

    public CookieTracker(Cookie cookie) {
        this.cookie = cookie;
    }
    
    public CookieTracker(Cookie[] cookies) {
        this(findCookie(cookies));
    }

    protected static Cookie findCookie(Cookie[] cookies) {
        for(Cookie cookie : cookies) {
            if(cookie !=null && FORTRESS_COOKIE.equals(cookie.getName())) return cookie;
        }
        return new Cookie(FORTRESS_COOKIE,"");
    }

    public int count() {
        if(cookie == null) return 0;
        return parseValue(cookie.getValue()).length;
    }
    
    protected static String[] parseValue(String value) {
        if(value == null || value.isEmpty()) return new String[0];
        return value.split("\\|");
    }
    
    public Cookie addYear(String newYear) {
        if(cookie.getValue() == null || cookie.getValue().isEmpty()) {
            cookie.setValue(newYear);
        } else if(!contains(newYear)){
            cookie.setValue(cookie.getValue()+ "|" + newYear);
        }
        return cookie;
    }

    protected boolean contains(String newYear) {
        String[] foundYears = parseValue(cookie.getValue());
        for(String year : foundYears) {
            if(year.equals(newYear)) return true;
        }
        return false;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public String[] years() {
        String[] years = parseValue(cookie.getValue());
        Arrays.sort(years);
        return years;
    }
}
