package com.rocinante.shopdirectory.selectors;

import io.vavr.control.Either;
import org.jsoup.nodes.Element;

public interface ElementSelector {
  Either<ElementNotSelected, ElementProperties> select(Element element);
}