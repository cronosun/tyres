package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Resources;

public class DelegatingResouces implements Resources {

  private final Resources resources;

  public DelegatingResouces(Resources resources) {
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
