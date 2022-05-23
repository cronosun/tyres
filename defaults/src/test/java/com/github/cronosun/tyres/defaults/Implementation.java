package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.defaults.implementation.ResourcesConstructor;

public final class Implementation {

  private Implementation() {}

  public static Resources newImplementation(MsgNotFoundStrategy notFoundStrategy) {
    return new ResourcesConstructor(notFoundStrategy).construct();
  }
}
