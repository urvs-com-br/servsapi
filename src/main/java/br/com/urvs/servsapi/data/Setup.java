package br.com.urvs.servsapi.data;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Setup {
  public Boolean serverVerbose;
  public Boolean serverArchive;
  public String serverName;
  public String serverLang;
  public String serverHost;
  public Integer serverPort;
  public String serverFolder;
  public Boolean servesPUB;
  public Boolean servesDIR;

  public Map<String, String> params;
  public Map<String, String> redirects;

  public Integer threadsMin;
  public Integer threadsMax;
  public Integer threadsIdleTimeout;
  public Long cleanInterval;
  public Long tokenValidity;
  public Integer storeMinIdle;
  public Integer storeMaxIdle;
  public Integer storeMaxTotal;

  public void fixDefaults() {
    if (this.serverVerbose == null) {
      this.serverVerbose = false;
    }
    if (this.serverArchive == null) {
      this.serverArchive = false;
    }
    if (this.serverName == null || this.serverName.isEmpty()) {
      this.serverName = "ServSapi";
    }
    if (this.serverLang == null || this.serverLang.isEmpty()) {
      this.serverLang = "en";
    }
    if (this.serverHost == null || this.serverHost.isEmpty()) {
      this.serverHost = "localhost";
    }
    if (this.serverPort == null) {
      this.serverPort = 5490;
    }
    if (this.serverFolder == null) {
      this.serverFolder = "";
    }
    this.serverFolder = new File(this.serverFolder).getAbsolutePath();
    if (this.servesPUB == null) {
      this.servesPUB = false;
    }
    if (this.servesDIR == null) {
      this.servesDIR = false;
    }

    if (this.params == null) {
      this.params = new HashMap<>();
    }
    if (this.redirects == null) {
      this.redirects = new HashMap<>();
    }

    if (this.threadsMin == null) {
      this.threadsMin = 10;
    }
    if (this.threadsMax == null) {
      this.threadsMax = 100;
    }
    if (this.threadsIdleTimeout == null) {
      this.threadsIdleTimeout = 120;
    }
    if (this.cleanInterval == null) {
      this.cleanInterval = 12 * 60 * 60 * 1000L;
    }
    if (this.tokenValidity == null) {
      this.tokenValidity = 24 * 60 * 60 * 1000L;
    }
    if (this.storeMinIdle == null) {
      this.storeMinIdle = 10;
    }
    if (this.storeMaxIdle == null) {
      this.storeMaxIdle = 30;
    }
    if (this.storeMaxTotal == null) {
      this.storeMaxTotal = 100;
    }
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }

  public static Setup fromString(String source) {
    return new Gson().fromJson(source, Setup.class);
  }
}
