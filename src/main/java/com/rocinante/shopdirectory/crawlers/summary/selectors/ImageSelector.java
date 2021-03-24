package com.rocinante.shopdirectory.crawlers.summary.selectors;

import com.rocinante.shopdirectory.selectors.ElementNotSelected;
import com.rocinante.shopdirectory.selectors.ElementProperties;
import com.rocinante.shopdirectory.selectors.ElementSelector;
import io.vavr.control.Either;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.jsoup.nodes.Element;

public class ImageSelector implements ElementSelector {
  public static final String IMAGE_SRC_URL = "image_url";

  @Override
  public Either<ElementNotSelected, ElementProperties> select(Element element) {
    if (element.tagName().equals("img")) {
      Map<String, Object> properties = new HashMap<>();
      properties.put(IMAGE_SRC_URL, element.attr("abs:src"));
      return Either.right(new ElementProperties(element, properties));
    } else {
      return Either.left(ElementNotSelected.getInstance());
    }
  }

  @Override
  public int hashCode() {
    return Objects.hash(ImageSelector.class.getName());
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof ImageSelector;
  }
}
