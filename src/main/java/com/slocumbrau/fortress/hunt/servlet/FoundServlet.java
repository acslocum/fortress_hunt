package com.slocumbrau.fortress.hunt.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slocumbrau.fortress.hunt.domain.CookieTracker;

@WebServlet(
        name = "FoundASheet",
        urlPatterns = {"/fortress/*"}
    )
public class FoundServlet extends HttpServlet{
    public static final String FORTRESS_WINNER_COOKIE = "fortress_hunt_winner_cookie";
    static int successfulPeopleRequired = CookieTracker.GUEST_COUNT;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String year = null;

        String pathInfo = req.getPathInfo();
        if(pathInfo != null) {
            String[] pathParts = pathInfo.split("/");
            if(pathParts.length > 1)
                year = pathParts[1];
        }

        CookieTracker cookieTracker = new CookieTracker(req.getCookies());
        if(year != null) {
            cookieTracker.addYear(year);
        }
        resp.addCookie(cookieTracker.getCookie());
        String result = null;
        if(cookieTracker.success()) {
            if(successfulPeopleRequired > 1){
                result = showSuccessMessage();
            } else if(successfulPeopleRequired < 0){
                result = showKeepGoingMessage();
            }
        } else {
            result = createMessage(cookieTracker);
        }
        if(cookieTracker.success() && !previousWinner(req)) {
            successfulPeopleRequired--;
            setWinnerCookie(resp);
        }
        if(successfulPeopleRequired == 0) {
            successfulPeopleRequired--;
            result = showUnlockMessage();
        }
        result = wrap(result);
        ServletOutputStream out = resp.getOutputStream();
        out.write(result.getBytes());
        out.flush();
        out.close();
    }

    private void setWinnerCookie(HttpServletResponse resp) {
        resp.addCookie(new Cookie(FORTRESS_WINNER_COOKIE,"true"));
    }

    private boolean previousWinner(HttpServletRequest req) {
        for(Cookie cookie : req.getCookies()) {
            if(cookie != null && FORTRESS_WINNER_COOKIE.equals(cookie.getName())) {
                return true;
            }
        }
        return false;
    }

    private String wrap(String result) {
        return "<html><body>"+result+"</body></html>";
    }

    private String showKeepGoingMessage() {
        return "The secret room is already opened, but you should keep doing this for funsies.";
    }

    protected String createMessage(CookieTracker cookieTracker) {
        StringBuffer result = new StringBuffer();
        int count = cookieTracker.count();
        if(count == 1) {
            result.append(showInfoMessage());
        }
        result.append("<h2>You have found " + count + " sheets so far!</h2>");
        for(String year : cookieTracker.years()) {
            result.append("<p>"+year+"</p>");
        }
        result.append("<h2>You still need to find sheets from:</h2>");
        for(String year : cookieTracker.remainingYears()) {
            result.append("<p>"+year+"</p>");
        }
        return result.toString();
    }
    
    private String showUnlockMessage() {
        StringBuffer winMessage = new StringBuffer();
        winMessage.append("<h1>IT'S ALL CONNECTED</h1>");
        winMessage.append("<p>Find someone with the Fortress logo on their chest and show this to them.</p>");
        winMessage.append("<img src='http://imgur.com/7drHiqr.gif'/>");
        return winMessage.toString();
    }

    private String showSuccessMessage() {
        StringBuffer successMessage = new StringBuffer();
        successMessage.append("<h2>You have scanned all the sign-in sheets!</h2>");
        successMessage.append("<p>Now help " + successfulPeopleRequired + " more people do the same to open the secret room!</p>");
        return successMessage.toString();
    }

    public static String showInfoMessage() {
        StringBuffer infoMessage = new StringBuffer();
        infoMessage.append("These sign in sheets are scattered throughout the fort. When you find one, scan the QR code with a QR reader (which you just did).");
        infoMessage.append("When "+CookieTracker.GUEST_COUNT+" people find all "+ CookieTracker.SHEET_COUNT + " of them, a secret fortress room will be opened.");
        return infoMessage.toString();
    }


}
