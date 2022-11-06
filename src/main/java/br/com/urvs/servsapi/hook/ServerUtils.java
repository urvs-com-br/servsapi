package br.com.urvs.servsapi.hook;

import java.io.IOException;
import java.net.URLDecoder;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import br.com.urvs.servsapi.data.Setup;
import br.com.urvs.servsapi.swap.AskIssued;
import br.com.urvs.servsapi.work.OrdersUtils;
import br.com.urvs.servsapi.work.Runner;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServerUtils {
  public static void init(ServletContextHandler context, Setup setup) {
    initPing(context);
    initLang(context);
    initLogged(context);
    initParams(context);
    initRedirects(context, setup);
    initIssued(context);
  }

  private static void initPing(ServletContextHandler context) {
    context.addServlet(new ServletHolder(new HttpServlet() {
      @Override
      protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.getWriter().print("pong");
      }
    }), "/ping");
  }

  private static void initLang(ServletContextHandler context) {
    context.addServlet(new ServletHolder(new HttpServlet() {
      @Override
      protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
        var way = Runner.getWay(req);
        resp.setContentType("text/plain");
        resp.getWriter().print(way.air.setup.serverLang);
      }
    }), "/lang");
  }

  private static void initLogged(ServletContextHandler context) {
    context.addServlet(new ServletHolder(new HttpServlet() {
      @Override
      protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
        var way = Runner.getWay(req);
        var authed = Runner.getAuthed(way, req);
        resp.setContentType("text/plain");
        if (authed != null) {
          resp.getWriter().print(authed.getUserName());
        } else {
          resp.getWriter().print("<!-- No user is logged. -->");
        }
      }
    }), "/logged");
  }

  private static void initParams(ServletContextHandler context) {
    context.addServlet(new ServletHolder(new HttpServlet() {
      @Override
      protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
        var name = req.getPathInfo().substring(1);
        if (name.isEmpty()) {
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "You must provide a parameter name");
          return;
        }
        name = URLDecoder.decode(name, "UTF-8");
        var way = Runner.getWay(req);
        var authed = Runner.getAuthed(way, req);
        resp.setContentType("text/plain");
        resp.getWriter().print(OrdersUtils.askParams(way, authed, name));
      }
    }), "/param/*");
  }

  private static void initRedirects(ServletContextHandler context, Setup setup) {
    for (var entry : setup.redirects.entrySet()) {
      context.addServlet(new ServletHolder(new HttpServlet() {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
          resp.sendRedirect(entry.getValue());
        }
      }), entry.getKey());
    }
  }

  private static void initIssued(ServletContextHandler context) {
    context.addServlet(new ServletHolder(new HttpServlet() {
      @Override
      protected void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
        var way = Runner.getWay(req);
        var authed = Runner.getAuthed(way, req);
        if (authed == null) {
          resp.sendError(HttpServletResponse.SC_FORBIDDEN, "You must be logged");
          return;
        }
        var body = IOUtils.toString(req.getReader());
        var question = AskIssued.fromString(body);
        if (question.token == null || question.token.isEmpty()) {
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
              "You must provide the issued token");
          return;
        }
        var issued = authed.getIssued(question.token);
        if (issued == null) {
          resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
              "Couldn't found a issued with the token");
          return;
        }
        try {
          var results = OrdersUtils.askIssued(issued, question);
          resp.setContentType("text/plain");
          resp.getWriter().print(results);
        } catch (Exception e) {
          throw new ServletException(e);
        }
      }
    }), "/issued");
  }
}
