package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.Resources;

final class Implementation {

  private Implementation() {}

  public static Resources newImplementation(MsgNotFoundStrategy notFoundStrategy) {
    return DefaultResources.newDefault(notFoundStrategy);
  }
}
