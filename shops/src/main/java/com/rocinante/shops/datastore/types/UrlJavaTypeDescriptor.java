package com.rocinante.shops.datastore.types;

import java.net.MalformedURLException;
import java.net.URL;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class UrlJavaTypeDescriptor extends AbstractTypeDescriptor<URL> {
  public static final UrlJavaTypeDescriptor INSTANCE = new UrlJavaTypeDescriptor();

  public UrlJavaTypeDescriptor() {
    super(URL.class, ImmutableMutabilityPlan.INSTANCE);
  }

  @Override
  public URL fromString(String s) {
    try {
      return new URL(s);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public <X> X unwrap(URL url, Class<X> aClass, WrapperOptions wrapperOptions) {
    if (url == null) {
      return null;
    } else {
      return (X) url.toString();
    }
  }

  @Override
  public <X> URL wrap(X value, WrapperOptions wrapperOptions) {
    if (value == null) {
      return null;
    } else {
      try {
        return new URL((String) value);
      } catch (MalformedURLException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
