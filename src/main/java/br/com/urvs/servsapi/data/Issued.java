package br.com.urvs.servsapi.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Issued {

  private final Long createdAt;
  private final List<String> resultLines;
  private final ReadWriteLock linesLock;
  private volatile Integer resultCoded;
  private volatile Boolean isDone;
  private volatile Long finishedAt;

  public Issued() {
    this.createdAt = System.nanoTime();
    this.resultLines = new ArrayList<>();
    this.linesLock = new ReentrantReadWriteLock();
    this.resultCoded = null;
    this.isDone = false;
    this.finishedAt = null;
  }

  public Long getCreatedAt() {
    return this.createdAt;
  }

  public String getLines() {
    try {
      this.linesLock.readLock().lock();
      return String.join("\n", this.resultLines);
    } finally {
      this.linesLock.readLock().unlock();
    }
  }

  public String getLinesFrom(int index) {
    try {
      this.linesLock.readLock().lock();
      var result = new StringBuilder();
      for (int i = index; i < this.resultLines.size(); i++) {
        result.append(this.resultLines.get(i));
        result.append("\n");
      }
      return result.toString();
    } finally {
      this.linesLock.readLock().unlock();
    }
  }

  public void addLine(String out) {
    try {
      this.linesLock.writeLock().lock();
      this.resultLines.add(out);
    } finally {
      this.linesLock.writeLock().unlock();
    }
  }

  public Integer getResultCoded() {
    return this.resultCoded;
  }

  public void setResultCoded(Integer coded) {
    this.resultCoded = coded;
  }

  public boolean isDone() {
    return this.isDone;
  }

  public void setDone() {
    this.isDone = true;
    this.finishedAt = System.nanoTime();
  }

  public Long getFinishedAt() {
    return this.finishedAt;
  }
}
