package br.com.urvs.servsapi.work;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OrdersPUB {
  private static final int DEFAULT_BUFFER_SIZE = 10240; // ..bytes = 10KB.
  private static final long DEFAULT_EXPIRE_TIME = 86400000L; // ..ms = 1 day.
  private static final String MULTIPART_BOUNDARY = "MULTIPART_BYTERANGES";

  public static void send(HttpServletRequest req, HttpServletResponse resp,
      File file, boolean content) throws IOException {
    var fileName = file.getName();
    long length = file.length();
    long lastModified = file.lastModified();
    var eTag = fileName + "_" + length + "_" + lastModified;
    long expires = System.currentTimeMillis() + DEFAULT_EXPIRE_TIME;

    var ifNoneMatch = req.getHeader("If-None-Match");
    if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
      resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      resp.setHeader("ETag", eTag);
      resp.setDateHeader("Expires", expires);
      return;
    }
    var ifModifiedSince = req.getDateHeader("If-Modified-Since");
    if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince
        + 1000 > lastModified) {
      resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      resp.setHeader("ETag", eTag);
      resp.setDateHeader("Expires", expires);
      return;
    }
    var ifMatch = req.getHeader("If-Match");
    if (ifMatch != null && !matches(ifMatch, eTag)) {
      resp.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
      return;
    }
    var ifUnmodifiedSince = req.getDateHeader("If-Unmodified-Since");
    if (ifUnmodifiedSince != -1 && ifUnmodifiedSince + 1000 <= lastModified) {
      resp.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
      return;
    }

    var full = new Range(0, length - 1, length);
    var ranges = new ArrayList<Range>();
    var range = req.getHeader("Range");
    if (range != null) {
      if (!range.matches("^bytes=\\d*-\\d*(,\\d*-\\d*)*$")) {
        resp.setHeader("Content-Range", "bytes */" + length);
        resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
        return;
      }

      var ifRange = req.getHeader("If-Range");
      if (ifRange != null && !ifRange.equals(eTag)) {
        try {
          var ifRangeTime = req.getDateHeader("If-Range");
          if (ifRangeTime != -1 && ifRangeTime + 1000 < lastModified) {
            ranges.add(full);
          }
        } catch (IllegalArgumentException ignore) {
          ranges.add(full);
        }
      }

      if (ranges.isEmpty()) {
        for (String part : range.substring(6).split(",")) {
          var start = sublong(part, 0, part.indexOf("-"));
          var end = sublong(part, part.indexOf("-") + 1, part.length());
          if (start == -1) {
            start = length - end;
            end = length - 1;
          } else if (end == -1 || end > length - 1) {
            end = length - 1;
          }
          if (start > end) {
            resp.setHeader("Content-Range", "bytes */" + length); // Required in 416.
            resp.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return;
          }
          ranges.add(new Range(start, end, length));
        }
      }
    }

    var contentType = URLConnection.guessContentTypeFromName(file.getName());
    var acceptsGzip = false;
    var disposition = "inline";
    if (contentType == null) {
      contentType = "application/octet-stream";
    }
    if (contentType.startsWith("text")) {
      var acceptEncoding = req.getHeader("Accept-Encoding");
      acceptsGzip = acceptEncoding != null && accepts(acceptEncoding, "gzip");
      contentType += ";charset=UTF-8";
    } else if (!contentType.startsWith("image")) {
      var accept = req.getHeader("Accept");
      disposition = accept != null && accepts(accept, contentType) ? "inline"
          : "attachment";
    }

    resp.reset();
    resp.setBufferSize(DEFAULT_BUFFER_SIZE);
    resp.setHeader("Content-Disposition", disposition + ";filename=\"" + fileName
        + "\"");
    resp.setHeader("Accept-Ranges", "bytes");
    resp.setHeader("ETag", eTag);
    resp.setDateHeader("Last-Modified", lastModified);
    resp.setDateHeader("Expires", expires);

    RandomAccessFile input = null;
    OutputStream output = null;
    try {
      input = new RandomAccessFile(file, "r");
      output = resp.getOutputStream();
      if (ranges.isEmpty() || ranges.get(0) == full) {
        var r = full;
        resp.setContentType(contentType);
        if (content) {
          if (acceptsGzip) {
            resp.setHeader("Content-Encoding", "gzip");
            output = new GZIPOutputStream(output, DEFAULT_BUFFER_SIZE);
          } else {
            resp.setHeader("Content-Length", String.valueOf(r.length));
          }
          copy(input, output, r.start, r.length);
        }
      } else if (ranges.size() == 1) {
        var r = ranges.get(0);
        resp.setContentType(contentType);
        resp.setHeader("Content-Range", "bytes " + r.start + "-" + r.end + "/"
            + r.total);
        resp.setHeader("Content-Length", String.valueOf(r.length));
        resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT); // 206.
        if (content) {
          copy(input, output, r.start, r.length);
        }
      } else {
        resp.setContentType("multipart/byteranges; boundary=" + MULTIPART_BOUNDARY);
        resp.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        if (content) {
          var sos = (ServletOutputStream) output;
          for (var r : ranges) {
            sos.println();
            sos.println("--" + MULTIPART_BOUNDARY);
            sos.println("Content-Type: " + contentType);
            sos.println("Content-Range: bytes " + r.start + "-" + r.end + "/" + r.total);
            copy(input, output, r.start, r.length);
          }
          sos.println();
          sos.println("--" + MULTIPART_BOUNDARY + "--");
        }
      }
    } finally {
      Utils.close(output);
      Utils.close(input);
    }
  }

  private static boolean accepts(String acceptHeader, String toAccept) {
    var acceptValues = acceptHeader.split("\\s*(,|;)\\s*");
    Arrays.sort(acceptValues);
    return Arrays.binarySearch(acceptValues, toAccept) > -1 || Arrays.binarySearch(
        acceptValues, toAccept.replaceAll("/.*$", "/*")) > -1 || Arrays.binarySearch(
            acceptValues, "*/*") > -1;
  }

  private static boolean matches(String matchHeader, String toMatch) {
    var matchValues = matchHeader.split("\\s*,\\s*");
    Arrays.sort(matchValues);
    return Arrays.binarySearch(matchValues, toMatch) > -1 || Arrays.binarySearch(
        matchValues, "*") > -1;
  }

  private static long sublong(String value, int beginIndex, int endIndex) {
    var substring = value.substring(beginIndex, endIndex);
    return (substring.length() > 0) ? Long.parseLong(substring) : -1;
  }

  private static void copy(RandomAccessFile input, OutputStream output, long start,
      long length) throws IOException {
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int read;
    if (input.length() == length) {
      while ((read = input.read(buffer)) > 0) {
        output.write(buffer, 0, read);
      }
    } else {
      input.seek(start);
      long toRead = length;
      while ((read = input.read(buffer)) > 0) {
        if ((toRead -= read) > 0) {
          output.write(buffer, 0, read);
        } else {
          output.write(buffer, 0, (int) toRead + read);
          break;
        }
      }
    }
  }

  private static class Range {
    long start;
    long end;
    long length;
    long total;

    public Range(long start, long end, long total) {
      this.start = start;
      this.end = end;
      this.length = end - start + 1;
      this.total = total;
    }
  }
}
