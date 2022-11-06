package br.com.urvs.servsapi.data;

import java.io.PrintWriter;

import org.apache.commons.io.FilenameUtils;

public class Way {
  public final Air air;
  public final AuthedMap authedMap;

  private final PrintWriter archive;

  public Way(Air air) throws Exception {
    this.air = air;
    this.authedMap = new AuthedMap();
    this.archive = air.setup.serverArchive ? new PrintWriter(air.setup.serverFolder + "/"
        + air.setup.serverName + ".log") : null;
  }

  public void logInfo(String message) {
    message = logMake("INFO", message, null);
    if (this.air.setup.serverVerbose) {
      System.out.print(message);
      System.out.flush();
    }
    if (this.archive != null) {
      this.archive.print(message);
      this.archive.flush();
    }
  }

  public void logStep(Object value) {
    var message = logMake("STEP", String.valueOf(value), this);
    if (this.air.setup.serverVerbose) {
      System.out.print(message);
      System.out.flush();
    }
    if (this.archive != null) {
      this.archive.print(message);
      this.archive.flush();
    }
  }

  public void logWarn(String message) {
    message = logMake("WARN", message, this);
    if (this.air.setup.serverVerbose) {
      System.out.print(message);
      System.out.flush();
    }
    if (this.archive != null) {
      this.archive.print(message);
      this.archive.flush();
    }
  }

  public void logErro(Throwable error) {
    var message = logMake("ERRO", error.getMessage(), error);
    if (this.air.setup.serverVerbose) {
      System.out.print(message);
      System.out.flush();
    }
    if (this.archive != null) {
      this.archive.print(message);
      this.archive.flush();
    }
  }

  private String logMake(String kind, String message, Object traceOf) {
    var result = new StringBuilder();
    result.append("[");
    result.append(kind);
    result.append("] ");
    result.append(message);
    if (traceOf != null) {
      if (traceOf instanceof Throwable error) {
        result.append(this.getOrigin(error));
      } else {
        result.append(this.getOrigin());
      }
    }
    result.append("\n");
    return result.toString();
  }

  private String getOrigin() {
    StringBuilder builder = new StringBuilder();
    builder.append(" {");
    StackTraceElement elements[] = Thread.currentThread().getStackTrace();
    for (int i = elements.length - 1; i >= 0; i--) {
      StackTraceElement element = elements[i];
      if (element.getClassName().startsWith("br.com.urvs.servsapi") &&
          !element.getClassName().equals("br.com.urvs.servsapi.data.Way")) {
        builder.append(" |> ");
        builder.append(FilenameUtils.getBaseName(element.getFileName()));
        builder.append("[");
        builder.append(element.getLineNumber());
        builder.append("](");
        builder.append(element.getMethodName());
        builder.append(")");
      }
    }
    builder.append(" }");
    return builder.toString();
  }

  private String getOrigin(Throwable ofError) {
    StringBuilder builder = new StringBuilder();
    builder.append(" {");
    StackTraceElement elements[] = ofError.getStackTrace();
    for (int i = elements.length - 1; i >= 0; i--) {
      StackTraceElement element = elements[i];
      if (element.getClassName().startsWith("br.com.urvs.servsapi") &&
          !element.getClassName().equals("br.com.urvs.servsapi.data.Way")) {
        builder.append(" |> ");
        builder.append(FilenameUtils.getBaseName(element.getFileName()));
        builder.append("[");
        builder.append(element.getLineNumber());
        builder.append("](");
        builder.append(element.getMethodName());
        builder.append(")");
      }
    }
    builder.append(" }");
    return builder.toString();
  }
}
