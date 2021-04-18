package com.rocinante.datastore.types;

import com.neovisionaries.i18n.CountryCode;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;

public class CountryCodeJavaTypeDescriptor extends AbstractTypeDescriptor<CountryCode> {
  public static final CountryCodeJavaTypeDescriptor INSTANCE = new CountryCodeJavaTypeDescriptor();

  public CountryCodeJavaTypeDescriptor() {
    super(CountryCode.class, ImmutableMutabilityPlan.INSTANCE);
  }

  @Override
  public CountryCode fromString(String s) {
    return CountryCode.getByCode(s);
  }

  @Override
  public <X> X unwrap(CountryCode countryCode, Class<X> type, WrapperOptions wrapperOptions) {
    if (countryCode == null) {
      return null;
    } else {
      return (X) countryCode.getAlpha3();
    }
  }

  @Override
  public <X> CountryCode wrap(X value, WrapperOptions wrapperOptions) {
    if (value == null) {
      return null;
    } else {
      return CountryCode.getByAlpha3Code((String) value);
    }
  }
}
