package com.rocinante.shops.utils;

import java.io.File;

public class ResourceUtils {
  public static File getFileAtPath(String path) {
    return new File(ResourceUtils.class.getClassLoader().getResource(path).getFile());
  }
}
