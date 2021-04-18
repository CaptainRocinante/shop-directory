package com.rocinante.scraping.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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

  public static List<String> readAllLinesFromFile(String path) {
    try {
      try (InputStream inputStream = ResourceUtils.class.getResourceAsStream(path);
          BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        return reader.lines().collect(Collectors.toList());
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
