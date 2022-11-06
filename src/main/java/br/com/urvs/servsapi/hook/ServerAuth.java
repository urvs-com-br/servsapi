package br.com.urvs.servsapi.hook;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import br.com.urvs.servsapi.swap.TryAuth;
import br.com.urvs.servsapi.work.Runner;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServerAuth {
  public static void init(ServletContextHandler context) {
    initEnter(context);
  }

  private static void initEnter(ServletContextHandler context) {
    context.addServlet(new ServletHolder(new HttpServlet() {
      @Override
      protected void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
        var way = Runner.getWay(req);
        var body = IOUtils.toString(req.getReader());
        var tryAuth = TryAuth.fromString(body);
        var logged = Runner.tryEnter(tryAuth, way, req);
        if (logged == null) {
          resp.sendError(HttpServletResponse.SC_FORBIDDEN,
              "The user and/or pass is incorrect.");
          return;
        }
        resp.setContentType("application/json");
        resp.getWriter().print(logged.toString());
      }
    }), "/enter");
  }
}
