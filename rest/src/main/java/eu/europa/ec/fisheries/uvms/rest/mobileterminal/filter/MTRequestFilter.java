/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.rest.mobileterminal.filter;

import eu.europa.ec.fisheries.uvms.rest.mobileterminal.MTRestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@WebFilter("/*")
public class MTRequestFilter implements Filter {

    private final static Logger LOG = LoggerFactory.getLogger(MTRequestFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        LOG.info("Request filter starting up!");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader(MTRestConstants.ACCESS_CONTROL_ALLOW_ORIGIN, MTRestConstants.ACCESS_CONTROL_ALLOW_METHODS_ALL);
        response.setHeader(MTRestConstants.ACCESS_CONTROL_ALLOW_METHODS, MTRestConstants.ACCESS_CONTROL_ALLOWED_METHODS);
        response.setHeader(MTRestConstants.ACCESS_CONTROL_ALLOW_HEADERS, MTRestConstants.ACCESS_CONTROL_ALLOW_HEADERS_ALL);
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        LOG.info("Request filter shutting down!");
    }
}
