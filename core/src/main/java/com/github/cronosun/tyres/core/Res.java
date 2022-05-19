package com.github.cronosun.tyres.core;

@ThreadSafe
public interface Res<TSelf> {
  ResInfo info();

  Object[] args();

  TSelf withArgs(Object[] args);
}
