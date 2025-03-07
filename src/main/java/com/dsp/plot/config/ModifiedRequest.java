package com.dsp.plot.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class ModifiedRequest extends HttpServletRequestWrapper {
    private final String newAuthHeader;

    public ModifiedRequest(HttpServletRequest request, String newAuthHeader) {
        super(request);
        this.newAuthHeader = newAuthHeader;
    }

    @Override
    public String getHeader(String name) {
        if ("Authorization".equalsIgnoreCase(name)) {
            return newAuthHeader; // ✅ Return the new token
        }
        return super.getHeader(name);
    }
}
