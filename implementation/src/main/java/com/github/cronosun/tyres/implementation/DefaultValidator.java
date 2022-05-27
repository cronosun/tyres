package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;

final class DefaultValidator implements ValidatorBackend {

  private final Once<Resources> resources;
  private final BundleFactory bundleFactory;
  private final ResourcesBackend backend;

  public DefaultValidator(
    Once<Resources> resources,
    BundleFactory bundleFactory,
    ResourcesBackend backend
  ) {
    this.resources = resources;
    this.bundleFactory = bundleFactory;
    this.backend = backend;
  }

  @Override
  public void validate(When when, Class<?> bundleClass, Locale locale) {
    var bundle = resources.get().get(bundleClass);
    backend.validateAllResourcesFromBundle(
      () -> bundleFactory.declaredResourcesForValidation(bundle),
      locale
    );
  }
}
