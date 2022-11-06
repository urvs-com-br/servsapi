package br.com.urvs.servsapi.work;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

public class Utils {
  public static String newRandomToken() {
    return java.util.UUID.randomUUID().toString();
  }

  public static String listFolders(File onDir) {
    var result = new StringBuilder();
    if (onDir.exists()) {
      for (var inside : onDir.listFiles()) {
        if (inside.isDirectory()) {
          result.append(inside.getName());
          result.append("\n");
        }
      }
    }
    return result.toString();
  }

  public static String listFilesWithExtension(File onDir, String extension) {
    var result = new StringBuilder();
    listFilesWithExtension(result, onDir.getAbsolutePath().length(), onDir, extension.toLowerCase());
    return result.toString();
  }

  private static void listFilesWithExtension(StringBuilder making, int rootSize, File onDir, String extension) {
    if (onDir.exists()) {
      for (var inside : onDir.listFiles()) {
        if (!inside.isDirectory()) {
          if (inside.getName().toLowerCase().endsWith(extension)) {
            making.append(inside.getAbsolutePath().substring(rootSize));
            making.append("\n");
          }
        }
      }
      for (var inside : onDir.listFiles()) {
        if (inside.isDirectory()) {
          listFilesWithExtension(making, rootSize, new File(onDir, inside.getName()), extension);
        }
      }
    }
  }

  public static File resolveFile(String path, String parentIfRelative) {
    var parent = Paths.get(parentIfRelative);
    var child = Paths.get(path);
    var result = parent.resolve(child);
    return result.toFile();
  }

  public static void close(Closeable resource) {
    if (resource != null) {
      try {
        resource.close();
      } catch (IOException ignore) {
      }
    }
  }

  private final static String[] TEXT_EXTENSIONS = new String[] { "txt", "htm", "html",
      "css", "log" };
  private final static String[] IMAGE_EXTENSIONS = new String[] { "jpg", "jpeg", "gif",
      "png", "ico", "bmp", "svg" };
  private final static String[] AUDIO_EXTENSIONS = new String[] { "mp3", "ogg", "wav",
      "midi", "mid" };
  private final static String[] VIDEO_EXTENSIONS = new String[] { "mp4", "ogv", "avi",
      "mpg", "webm", "flv", "mov" };

  public static String getMimeType(String fileName) {
    var dot = fileName.lastIndexOf(".");
    if (dot == -1) {
      return "application/octet-stream";
    }
    var extension = fileName.substring(dot + 1);
    if (extension.equals("js")) {
      return "text/javascript";
    }
    if (has(extension, Utils.TEXT_EXTENSIONS)) {
      return "text/" + extension;
    }
    if (has(extension, Utils.IMAGE_EXTENSIONS)) {
      return "image/" + extension;
    }

    if (has(extension, Utils.AUDIO_EXTENSIONS)) {
      return "audio/" + extension;
    }

    if (has(extension, Utils.VIDEO_EXTENSIONS)) {
      return "video/" + extension;
    }
    return "application/" + extension;
  }

  @SuppressWarnings("all")
  public static <T> Boolean has(T value, T... onArray) {
    if (onArray != null) {
      for (Object daMatriz : onArray) {
        if (Objects.equals(value, daMatriz)) {
          return true;
        }
      }
    }
    return false;
  }
}
