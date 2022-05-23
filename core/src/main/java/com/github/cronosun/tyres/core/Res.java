package com.github.cronosun.tyres.core;

import java.util.List;

@ThreadSafe
public interface Res<TSelf> extends WithConciseDebugString {
  ResInfo info();

  Object[] args();

  TSelf withArgs(Object[] args);

  @Override
  default String conciseDebugString() {
    if (args().length > 0) {
      return WithConciseDebugString.build(List.of(info(), args()));
    } else {
      return WithConciseDebugString.build(List.of(info()));
    }
  }
}
