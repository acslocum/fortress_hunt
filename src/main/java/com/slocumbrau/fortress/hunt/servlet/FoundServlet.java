package com.slocumbrau.fortress.hunt.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slocumbrau.fortress.hunt.domain.CookieTracker;

@WebServlet(
        name = "FoundASheet",
        urlPatterns = {"/fortress/*"}
    )
public class FoundServlet extends HttpServlet{

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
        String result = createMessage(cookieTracker);
        resp.addCookie(cookieTracker.getCookie());
        ServletOutputStream out = resp.getOutputStream();
        out.write(result.getBytes());
        out.flush();
        out.close();
    }

    protected String createMessage(CookieTracker cookieTracker) {
        StringBuffer result = new StringBuffer();
        int count = cookieTracker.count();
        result.append("<html><body><h2>");
        result.append("You have found " + count + " sheets so far!");
        result.append("</h2>");
        for(String year : cookieTracker.years()) {
            result.append("<p>"+year+"</p>");
        }
        result.append("</body></html>");
        return result.toString();
    }


}
