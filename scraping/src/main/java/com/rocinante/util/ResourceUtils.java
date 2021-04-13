package com.rocinante.util;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import org.apache.commons.io.FileUtils;

public class ResourceUtils {
  public static String readFileContents(String resourcesFilePath) {
    final String data;
    final File file =
        new File(
            Objects.requireNonNull(
                    ResourceUtils.class.getClassLoader().getResource(resourcesFilePath))
                .getFile());
    try {
      data = FileUtils.readFileToString(file, "UTF-8");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return data;
  }

  public static File getFileAtPath(String path) {
    return new File(Objects.requireNonNull(ResourceUtils.class.getClassLoader().getResource(path))
        .getFile());
  }
}
