package com.rocinante.shops.datastore.types;

import com.neovisionaries.i18n.CountryCode;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class CountryCodeType extends AbstractSingleColumnStandardBasicType<CountryCode> {
  public CountryCodeType() {
    super(VarcharTypeDescriptor.INSTANCE, CountryCodeJavaTypeDescriptor.INSTANCE);
  }

  @Override
  public String getName() {
    return "CountryCode";
  }
}
