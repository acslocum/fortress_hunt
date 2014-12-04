package com.slocumbrau.fortress.hunt.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slocumbrau.fortress.hunt.domain.CookieTracker;

public class WelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        StringBuffer result = new StringBuffer();
        result.append("<html><body>");
        result.append("<h2>Welcome to the Fortress 20th Anniversary Sheet Hunt!</h2>");
        result.append("<p>Search high and low for previous years sign-in sheets! When you find one, scan it with a QR reader.</p>");
        result.append("<p>When "+CookieTracker.GUEST_COUNT+" people find all "+ CookieTracker.SHEET_COUNT + " of them, a <b>secret</b> fortress room will be opened!</p>");
        result.append("</body></html>");

        ServletOutputStream out = resp.getOutputStream();
        out.write(result.toString().getBytes());
        out.flush();
        out.close();
    }
}
