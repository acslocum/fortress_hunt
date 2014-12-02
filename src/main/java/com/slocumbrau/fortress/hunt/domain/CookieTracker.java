package com.slocumbrau.fortress.hunt.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;

public class CookieTracker {

    public static final String FORTRESS_COOKIE = "fortress_hunt_cookie";
    public static final int SHEET_COUNT = 12;
    public static final int GUEST_COUNT = 12;
    private Cookie cookie;

    public CookieTracker(Cookie cookie) {
        this.cookie = cookie;
    }
    
    public CookieTracker(Cookie[] cookies) {
        this(findCookie(cookies));
    }

    protected static Cookie findCookie(Cookie[] cookies) {
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie !=null && FORTRESS_COOKIE.equals(cookie.getName())) return cookie;
            }
        }
        return new Cookie(FORTRESS_COOKIE,"");
    }

    public int count() {
        if(cookie == null) return 0;
        return SHEET_COUNT - remainingYears().size();
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
    
    private ArrayList<String> validYears() {
        return new ArrayList<String>(Arrays.asList("2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014"));
    }

    public List<String> remainingYears() {
        ArrayList<String> allYears = validYears();
        for(String year : years()) {
            allYears.remove(year);
        }
        return allYears;
    }

    public boolean success() {
        return remainingYears().size()==0;
    }
}
