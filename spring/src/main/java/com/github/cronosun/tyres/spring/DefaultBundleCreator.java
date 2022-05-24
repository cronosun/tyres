package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.core.TyResImplementation;
import java.util.Locale;
import java.util.Set;

final class DefaultBundleCreator implements BundleCreator {

  private final TyResImplementation tyResImplementation;
  private final Resources resources;
  private final Set<Locale> localesForValidation;

  public DefaultBundleCreator(
    TyResImplementation tyResImplementation,
    Resources resources,
    Set<Locale> localesForValidation
  ) {
    this.tyResImplementation = tyResImplementation;
    this.resources = resources;
    this.localesForValidation = localesForValidation;
  }

  @Override
  public <T> T createBundle(Class<T> bundleClass) {
    var bundle = tyResImplementation.createInstance(bundleClass);
    var errors = resources.common().validate(bundle, this.localesForValidation);
    if (errors!=null) {
      throw new TyResException(errors);
    }
    return bundle;
  }
}
