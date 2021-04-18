package com.rocinante.datastore.types;

import com.neovisionaries.i18n.CurrencyCode;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class CurrencyCodeType extends AbstractSingleColumnStandardBasicType<CurrencyCode> {

  public CurrencyCodeType() {
    super(VarcharTypeDescriptor.INSTANCE, CurrencyCodeJavaTypeDescriptor.INSTANCE);
  }

  @Override
  public String getName() {
    return "CurrencyCode";
  }
}
