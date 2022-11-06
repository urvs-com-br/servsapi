package br.com.urvs.servsapi.work;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class OrdersDIR {
  public static String dirList(File path) {
    var result = new StringBuilder();
    result.append("P: ");
    result.append(path.getAbsolutePath());
    result.append("\n");
    for (var inside : path.listFiles()) {
      result.append(inside.isDirectory() ? "D: " : "F: ");
      result.append(inside.getName());
      result.append("\n");
    }
    return result.toString();
  }

  public static String dirNew(File path) throws IOException {
    Files.createDirectories(path.toPath());
    return "Folder created: " + path.getAbsolutePath();
  }

  public static String dirCopy(File origin, File destiny) throws IOException {
    FileUtils.copyDirectory(origin, destiny);
    return "Folder copied: " + origin.getAbsolutePath() + " to: " + destiny
        .getAbsolutePath();
  }

  public static String dirMove(File origin, File destiny) throws IOException {
    FileUtils.moveDirectory(origin, destiny);
    return "Folder moved: " + origin.getAbsolutePath() + " to: " + destiny
        .getAbsolutePath();
  }

  public static String dirDel(File path) throws IOException {
    FileUtils.deleteDirectory(path);
    return "Folder deleted: " + path.getAbsolutePath();
  }

  public static String fileRead(File path, boolean base64, Integer rangeStart,
      Integer rangeLength) throws IOException {
    if (rangeStart != null) {
      try (var input = new FileInputStream(path);
          var output = new ByteArrayOutputStream();) {
        IOUtils.copyLarge(input, output, rangeStart, rangeLength);
        if (base64) {
          return Base64.getEncoder().encodeToString(output.toByteArray());
        } else {
          return new String(output.toByteArray());
        }
      }
    } else {
      if (base64) {
        return Base64.getEncoder().encodeToString(Files.readAllBytes(path.toPath()));
      } else {
        return Files.readString(path.toPath());
      }
    }
  }

  public static String fileWrite(File path, boolean base64, String data,
      Integer rangeStart) throws IOException {
    if (rangeStart != null) {
      try (var writer = new RandomAccessFile(path, "rw")) {
        writer.seek(rangeStart);
        if (base64) {
          writer.write(Base64.getDecoder().decode(data));
        } else {
          writer.writeUTF(data);
        }
      }
    } else {
      if (base64) {
        FileUtils.writeByteArrayToFile(path, Base64.getDecoder().decode(data), false);
      } else {
        FileUtils.writeStringToFile(path, data, StandardCharsets.UTF_8, false);
      }
    }
    return "File written: " + path.getAbsolutePath();
  }

  public static String fileAppend(File path, boolean base64, String data)
      throws IOException {
    if (base64) {
      FileUtils.writeByteArrayToFile(path, Base64.getDecoder().decode(data), true);
    } else {
      FileUtils.writeStringToFile(path, data, StandardCharsets.UTF_8, true);
    }
    return "File appended: " + path.getAbsolutePath();
  }

  public static String fileCopy(File origin, File destiny) throws IOException {
    FileUtils.copyFile(origin, destiny);
    return "File copied: " + origin.getAbsolutePath() + " to: " + destiny
        .getAbsolutePath();
  }

  public static String fileMove(File origin, File destiny) throws IOException {
    FileUtils.moveFile(origin, destiny);
    return "File moved: " + origin.getAbsolutePath() + " to: " + destiny
        .getAbsolutePath();
  }

  public static String fileDel(File path) throws IOException {
    FileUtils.delete(path);
    return "File deleted: " + path.getAbsolutePath();
  }
}
