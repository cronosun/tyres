package com.github.cronosun.tyres.core;

import com.github.cronosun.tyres.core.Resources;

public class DelegatingResources implements Resources {

  private final Resources resources;

  public DelegatingResources(Resources resources) {
    this.resources = resources;
  }

  @Override
  public final Messages msg() {
    return resources.msg();
  }

  @Override
  public final Strings str() {
    return resources.str();
  }

  @Override
  public final Binaries bin() {
    return resources.bin();
  }

  @Override
  public final Resolver resolver() {
    return resources.resolver();
  }

  @Override
  public final Common common() {
    return resources.common();
  }
}
