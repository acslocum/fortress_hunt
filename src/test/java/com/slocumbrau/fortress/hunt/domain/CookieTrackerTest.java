package com.slocumbrau.fortress.hunt.domain;

import static org.junit.Assert.*;

import java.util.List;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Test;

public class CookieTrackerTest {
    Cookie cookie;
    
    @Before
    public void setUp() {
        cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,null);
    }

    @Test
    public void shouldIdentifyOneYearScannedFromCookie() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE, "2004");
        assertEquals(1,new CookieTracker(cookie).count());
    }

    @Test
    public void shouldIdentifyTwoYearsScannedFromCookie() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE, "2004|2005");
        assertEquals(2,new CookieTracker(cookie).count());
    }

    @Test
    public void shouldIdentifyTwoYearsScannedFromCookieIfOneFakeCreepsIn() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE, "2004|2005|2020");
        assertEquals(2,new CookieTracker(cookie).count());
    }

    @Test
    public void shouldIdentifyNoYearScannedFromMissingCookie() {
        assertEquals(0,new CookieTracker(cookie).count());
    }
    
    @Test
    public void shouldConvertNullStringToNoYearsFound() {
        String[] years = CookieTracker.parseValue(null);
        assertEquals(0,years.length);
    }

    @Test
    public void shouldConvertEmptyStringToNoYearsFound() {
        String[] years = CookieTracker.parseValue("");
        assertEquals(0,years.length);
    }

    @Test
    public void shouldConvertOneYearStringToOneYearFound() {
        String[] years = CookieTracker.parseValue("2004");
        assertEquals(1,years.length);
        assertEquals("2004", years[0]);
    }

    @Test
    public void shouldConvertTwoYearStringToTwoYearsFound() {
        String[] years = CookieTracker.parseValue("2004|2005");
        assertEquals(2,years.length);
        assertEquals("2004", years[0]);
        assertEquals("2005", years[1]);
    }
    
    @Test
    public void shouldAddAYearToAnEmptyCookie() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,"");
        cookie = new CookieTracker(cookie).addYear("2014");
        assertEquals("2014",cookie.getValue());
    }

    @Test
    public void shouldAddAYearToANonEmptyCookie() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,"2013");
        cookie = new CookieTracker(cookie).addYear("2014");
        assertEquals("2013|2014",cookie.getValue());
    }

    @Test
    public void shouldNotAddAnExistingYearToACookie() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,"2013");
        cookie = new CookieTracker(cookie).addYear("2013");
        assertEquals("2013",cookie.getValue());
    }
    
    @Test
    public void shouldFindCorrectCookieFromArray() {
        Cookie[] cookies = new Cookie[2];
        cookies[0] = new Cookie("wrong", "not it");
        cookies[1] = cookie;
        assertEquals(cookie,CookieTracker.findCookie(cookies));
    }

    @Test
    public void shouldCreateCookieIfNotFound() {
        Cookie[] cookies = new Cookie[2];
        cookies[0] = new Cookie("wrong", "not it");
        cookies[1] = null;
        Cookie foundCookie = CookieTracker.findCookie(cookies);
        assertEquals(CookieTracker.FORTRESS_COOKIE,foundCookie.getName());
        assertEquals("",foundCookie.getValue());
    }
    
    @Test
    public void shouldCreateCookieIfNoCookies() {
        Cookie foundCookie = CookieTracker.findCookie(null);
        assertEquals(CookieTracker.FORTRESS_COOKIE,foundCookie.getName());
        assertEquals("",foundCookie.getValue());
    }
    
    @Test
    public void shouldEnumerateYearsFound() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,"2003|2004|2005");
        String[] years = new CookieTracker(cookie).years();
        assertEquals(3,years.length);
        assertEquals("2003",years[0]);
        assertEquals("2004",years[1]);
        assertEquals("2005",years[2]);
    }

    @Test
    public void shouldSortYearsFound() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,"2013|2004|1999");
        String[] years = new CookieTracker(cookie).years();
        assertEquals(3,years.length);
        assertEquals("1999",years[0]);
        assertEquals("2004",years[1]);
        assertEquals("2013",years[2]);
    }
    
    @Test
    public void shouldShowAllRemainingYearsForNewUser() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,null);
        assertEquals(12,new CookieTracker(cookie).remainingYears().size());
    }

    @Test
    public void shouldShowNoRemainingYearsForCompleteUser() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,"2003|2004|2005|2006|2007|2008|2009|2010|2011|2012|2013|2014");
        assertEquals(0,new CookieTracker(cookie).remainingYears().size());
    }

    @Test
    public void shouldShowOneRemainingYearsForNearlyCompleteUser() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,"2003|2004|2005|2006|2007|2008|2009|2010|2011|2012|2013");
        List<String> remainingYears = new CookieTracker(cookie).remainingYears();
        assertEquals(1,remainingYears.size());
        assertTrue(remainingYears.contains("2014"));
    }
    
    @Test
    public void shouldShowOneRemainingYearsUserWhoTriedToCheat() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,"2003|2004|2005|2006|2007|2008|2009|2010|2011|2012|2013|2020");
        List<String> remainingYears = new CookieTracker(cookie).remainingYears();
        assertEquals(1,remainingYears.size());
        assertTrue(remainingYears.contains("2014"));
    }
    
    @Test
    public void shouldReportSuccessWhenAllSheetsFound() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,"2003|2004|2005|2006|2007|2008|2009|2010|2011|2012|2013|2014");
        assertTrue(new CookieTracker(cookie).success());

    }

    @Test
    public void shouldNotReportSuccessWhenFakeSheetsFound() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,"2003|2004|2005|2006|2007|2008|2009|2010|2011|2012|2013|2020");
        assertFalse(new CookieTracker(cookie).success());

    }

    @Test
    public void shouldReportFailureWhenMissingAYear() {
        Cookie cookie = new Cookie(CookieTracker.FORTRESS_COOKIE,"2003|2004|2005|2006|2007|2008|2010|2011|2012|2013|2014");
        assertFalse(new CookieTracker(cookie).success());
    }

}
