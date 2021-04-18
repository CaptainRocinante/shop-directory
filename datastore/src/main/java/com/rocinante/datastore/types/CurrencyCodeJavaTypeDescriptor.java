package com.rocinante.datastore.types;

import com.neovisionaries.i18n.CurrencyCode;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class CurrencyCodeJavaTypeDescriptor extends AbstractTypeDescriptor<CurrencyCode> {
  public static final CurrencyCodeJavaTypeDescriptor INSTANCE =
      new CurrencyCodeJavaTypeDescriptor();

  public CurrencyCodeJavaTypeDescriptor() {
    super(CurrencyCode.class, ImmutableMutabilityPlan.INSTANCE);
  }

  @Override
  public CurrencyCode fromString(String s) {
    return CurrencyCode.getByCode(s);
  }

  @Override
  public <X> X unwrap(CurrencyCode currencyCode, Class<X> aClass, WrapperOptions wrapperOptions) {
    if (currencyCode == null) {
      return null;
    } else {
      return (X) currencyCode.getCurrency().getCurrencyCode();
    }
  }

  @Override
  public <X> CurrencyCode wrap(X value, WrapperOptions wrapperOptions) {
    if (value == null) {
      return null;
    } else {
      return CurrencyCode.getByCode((String) value);
    }
  }
}
