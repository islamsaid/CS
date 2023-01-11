/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.asset.contactstrategy.controllers;

import com.asset.contactstrategy.defines.Defines;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Mostafa Fouda
 */
public class CSFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        // Get request URI
        String contextPath = req.getContextPath();
        String requestURI = req.getRequestURI().substring(contextPath.length());
        // If this is a resource request, process request normally
        if (requestURI.startsWith("/javax.faces.resource/")) {
            chain.doFilter(request, response);
            return;
        }

        String url = req.getRequestURL().toString();

        if (!url.contains("/faces/pages/Login.xhtml")) {
            if (req.getSession().getAttribute(Defines.USER_SESSION_MODULE) == null) {
                String facesRequestHeader = req.getHeader("Faces-Request");
                boolean isAjaxRequest = facesRequestHeader != null && facesRequestHeader.equals("partial/ajax");

                HttpServletResponse res = (HttpServletResponse) response;
                String redirectURL = res.encodeRedirectURL(req.getContextPath() + "/faces/pages/Login.xhtml?faces-redirect=true");

                /*Ajax Request*/
                if (isAjaxRequest) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><partial-response><redirect url=\"");
                    sb.append(redirectURL).append("\"></redirect></partial-response>");
                    res.setHeader("Cache-Control", "no-cache");
                    res.setCharacterEncoding("UTF-8");
                    res.setContentType("text/xml");
                    PrintWriter pw = response.getWriter();
                    pw.println(sb.toString());
                    pw.flush();
                } else/*Submit Request*/ {
                    // User is not logged in, so redirect to index.
                    res.sendRedirect(redirectURL);
                }
                return;
            }
        } else {
            if (req.getSession().getAttribute(Defines.USER_SESSION_MODULE) != null) {
                HttpServletResponse res = (HttpServletResponse) response;
//                if (req.getSession().getAttribute(Defines.USER_SESSION_MODULE) instanceof ShortUserModel) {
                    res.sendRedirect(req.getContextPath() + "/faces/pages/Home.xhtml?faces-redirect=true");
//                } else {
//                    res.sendRedirect(req.getContextPath() + "/pages/ChangePassword.xhtml?faces-redirect=true");
//                }
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
