package com.rocinante.datastore.types;

import java.net.URL;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class UrlType extends AbstractSingleColumnStandardBasicType<URL> {
  public UrlType() {
    super(VarcharTypeDescriptor.INSTANCE, UrlJavaTypeDescriptor.INSTANCE);
  }

  @Override
  public String getName() {
    return "URL";
  }
}
