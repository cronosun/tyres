package com.github.cronosun.tyres.core;

@ThreadSafe
public interface Res<TSelf> extends WithConciseDebugString {
  ResInfo info();

  Object[] args();

  TSelf withArgs(Object[] args);

  @Override
  default String conciseDebugString() {
    var builder = ConciseDebugString.create().start().append(info());
    if (args().length > 0) {
      builder.child().append(args());
    }
    return builder.end().finish();
  }
}
