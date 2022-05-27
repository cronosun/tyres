package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;

// TODO: Make configurable (disable validation)
// TODO: Maybe also check on first access (in the #get - Method)
// TODO: Also cache the validation result
public class DefaultValidator implements ValidatorBackend {

  private final BundleFactory bundleFactory;
  private final ResourcesBackend backend;

  public DefaultValidator(BundleFactory bundleFactory, ResourcesBackend backend) {
    this.bundleFactory = bundleFactory;
    this.backend = backend;
  }

  @Override
  public void validateManually(Resources resources, Class<?> bundleClass, Locale locale) {
    var bundle = resources.get(bundleClass);
    backend.validateAllResourcesFromBundle(
      () -> bundleFactory.declaredResourcesForValidation(bundle),
      locale
    );
  }

  @Override
  public void validateOnAccess(Resources resources2, Class<?> bundleClass, Locale locale) {
    validateManually(resources2, bundleClass, locale);
    // TODO
  }
}
