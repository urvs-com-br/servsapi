package br.com.urvs.servsapi;

import java.io.File;
import java.nio.file.Files;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import br.com.urvs.servsapi.data.Way;
import br.com.urvs.servsapi.hook.ServerAuth;
import br.com.urvs.servsapi.hook.ServerUtils;
import br.com.urvs.servsapi.hook.ServesDIR;
import br.com.urvs.servsapi.hook.ServesPUB;

public class Service {

  private final Way runny;
  private final QueuedThreadPool threadPool;
  private final Server server;
  private final HttpConfiguration httpConfig;
  private final HttpConnectionFactory httpFactory;
  private final ServerConnector connector;
  private final ServletContextHandler context;

  public Service(Way runny) throws Exception {
    this.runny = runny;
    this.threadPool = new QueuedThreadPool(this.runny.air.setup.threadsMax,
        this.runny.air.setup.threadsMin, this.runny.air.setup.threadsIdleTimeout);
    this.server = new Server(this.threadPool);
    this.httpConfig = new HttpConfiguration();
    this.httpConfig.setSendDateHeader(false);
    this.httpConfig.setSendServerVersion(false);
    this.httpFactory = new HttpConnectionFactory(this.httpConfig);
    this.connector = new ServerConnector(this.server, httpFactory);
    connector.setHost(this.runny.air.setup.serverHost);
    connector.setPort(this.runny.air.setup.serverPort);
    this.server.setConnectors(new Connector[] { this.connector });
    this.context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    this.context.setContextPath("");
    this.context.setAttribute("ServSapi.Way", this.runny);
    this.server.setHandler(this.context);
    this.init_serves();
  }

  private void init_serves() throws Exception {
    this.server_auth();
    if (this.runny.air.setup.servesPUB) {
      this.serves_pub();
    }
    if (this.runny.air.setup.servesDIR) {
      this.serves_dir();
    }
    this.server_utils();
  }

  private void server_auth() {
    this.runny.logInfo("Serving Auth...");
    ServerAuth.init(this.context);
  }

  private void serves_pub() throws Exception {
    this.runny.logInfo("Serving PUB...");
    var holder = new ServletHolder(new ServesPUB());
    var pubDir = new File("pub");
    if (!pubDir.exists()) {
      Files.createDirectories(pubDir.toPath());
    }
    holder.setInitParameter("basePath", pubDir.getAbsolutePath());
    this.context.addServlet(holder, "/pub/*");
  }

  private void serves_dir() {
    this.runny.logInfo("Serving DIR...");
    ServesDIR.init(this.context);
  }

  private void server_utils() {
    this.runny.logInfo("Serving Utils...");
    ServerUtils.init(this.context, this.runny.air.setup);
  }

  public void start() throws Exception {
    this.runny.logInfo("Starting Server...");
    this.runny.logInfo("Setup On-Air: " + this.runny.air.setup);
    this.runny.logInfo("Users On-Air: " + this.runny.air.users);
    this.server.start();
    this.server.join();
  }

}
