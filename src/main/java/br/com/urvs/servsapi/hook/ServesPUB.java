package br.com.urvs.servsapi.hook;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import br.com.urvs.servsapi.work.OrdersPUB;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ServesPUB extends HttpServlet {

  private String basePath;

  @Override
  public void init() throws ServletException {
    this.basePath = getInitParameter("basePath");
    if (this.basePath == null) {
      throw new ServletException("ServesPub init param 'basePath' is required.");
    } else {
      var path = new File(this.basePath);
      if (!path.exists()) {
        throw new ServletException("ServesPub init param 'basePath' value '"
            + this.basePath + "' does actually not exist in file system.");
      } else if (!path.isDirectory()) {
        throw new ServletException("ServesPub init param 'basePath' value '"
            + this.basePath + "' is actually not a directory in file system.");
      } else if (!path.canRead()) {
        throw new ServletException("ServesPub init param 'basePath' value '"
            + this.basePath + "' is actually not readable in file system.");
      }
    }
  }

  @Override
  protected void doHead(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    var reqFile = req.getPathInfo();
    if (reqFile == null || reqFile.isEmpty()) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "You must provide a path");
      return;
    }
    var file = new File(basePath, URLDecoder.decode(reqFile, "UTF-8"));
    if (!file.exists()) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no file at: " + file);
      return;
    }
    OrdersPUB.send(req, resp, file, false);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    var reqFile = req.getPathInfo();
    if (reqFile == null || reqFile.isEmpty()) {
      resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "You must provide a path");
      return;
    }
    var file = new File(basePath, URLDecoder.decode(reqFile, "UTF-8"));
    if (!file.exists()) {
      resp.sendError(HttpServletResponse.SC_NOT_FOUND, "There is no file at: " + file);
      return;
    }
    OrdersPUB.send(req, resp, file, true);
  }

}
