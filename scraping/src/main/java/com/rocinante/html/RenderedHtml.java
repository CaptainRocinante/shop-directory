package com.rocinante.shopdirectory.html;

import java.util.Map;
import lombok.Data;

@Data
public class RenderedHtml {
  private final String html;
  private final Map<String, int[]> imageSrcDimensionMap;
}
