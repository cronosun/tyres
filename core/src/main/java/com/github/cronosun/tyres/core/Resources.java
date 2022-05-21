package com.github.cronosun.tyres.core;

@ThreadSafe
public interface Resources {
  MsgResources msg();
  StrResources str();
  BinResources bin();
}
