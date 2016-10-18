/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2016, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.set.overview.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.set.aphrodite.domain.Stream;
import org.jboss.set.aphrodite.domain.StreamComponent;
import org.jboss.set.overview.Constants;
import org.jboss.set.overview.ejb.Aider;

/**
 * @author wangc
 *
 */
@WebServlet(name = "StreamComponentViewServlet", loadOnStartup = 1, urlPatterns = { "/streamview/overview" })
public class StreamComponentViewServlet extends HttpServlet {

    private static final long serialVersionUID = -7121305620456598691L;
    private static Logger logger = Logger.getLogger(StreamComponentViewServlet.class.getCanonicalName());

    @EJB
    private Aider aiderService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String streamName = request.getParameter("streamName");
        List<Stream> streams = Aider.getAllStreams();
        if (streams == null) {
            response.addHeader("Refresh", "5");
            request.getRequestDispatcher("/error-wait.html").forward(request, response);
        } else {
            Optional<Stream> stream = aiderService.getCurrentStream(streamName);
            if (stream.isPresent()) {
                List<StreamComponent> filteredstreams = stream.get().getAllComponents().stream()
                        .filter(e -> e.getName().trim().equalsIgnoreCase(Constants.APPLICATION_SERVER)
                                || e.getName().trim().equalsIgnoreCase(Constants.APPLICATION_SERVER_CORE))
                        .collect(Collectors.toList());
                request.setAttribute("streamName", streamName);
                request.setAttribute("components", filteredstreams);
                request.getRequestDispatcher("/component.jsp").forward(request, response);
            } else {
                logger.log(Level.WARNING, "stream is an invalid");
                request.getRequestDispatcher("/error.html").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        // do nothing
    }
}
