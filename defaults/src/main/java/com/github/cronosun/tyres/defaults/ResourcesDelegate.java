package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.Locale;
import java.util.Set;

public class ResourcesDelegate implements Resources {

  private final Resources resources;

  public ResourcesDelegate(Resources resources) {
    this.resources = resources;
  }

  @Override
  public final MsgResources msg() {
    return resources.msg();
  }

  @Override
  public final StrResources str() {
    return resources.str();
  }

  @Override
  public final BinResources bin() {
    return resources.bin();
  }

  @Override
  public Resolver resolver() {
    return resources.resolver();
  }

  @Override
  public final void validate(Object bundle, Set<Locale> locales) {
    resources.validate(bundle, locales);
  }

  @Override
  public MsgNotFoundStrategy notFoundStrategy() {
    return resources.notFoundStrategy();
  }

  @Override
  public String fallbackFor(ResInfo resInfo, Object[] args) {
    return resources.fallbackFor(resInfo, args);
  }
}
