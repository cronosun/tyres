package com.github.cronosun.tyres.implementation.implementation;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.implementation.backends.BinBackend;
import com.github.cronosun.tyres.implementation.backends.FallbackGenerator;
import com.github.cronosun.tyres.implementation.backends.MsgStrBackend;
import com.github.cronosun.tyres.implementation.validation.Validator;

@ThreadSafe
final class DefaultResources implements Resources {

  private final Binaries bin;
  private final Strings str;
  private final Messages msg;
  private final Resolver resolver;
  private final Common common;

  public DefaultResources(
    MsgNotFoundStrategy notFoundStrategy,
    FallbackGenerator fallbackGenerator,
    MsgStrBackend msgStrBackend,
    BinBackend binBackend,
    Validator validator
  ) {
    var presentResolver = new DefaultResolver(this);
    this.common = new DefaultCommon(validator, fallbackGenerator, notFoundStrategy);
    this.str = new DefaultStrings(this, msgStrBackend);
    this.msg = new DefaultMsgResources(msgStrBackend, this);
    this.bin = new DefaultBinaries(binBackend);
    this.resolver = presentResolver;
  }

  @Override
  public Messages msg() {
    return msg;
  }

  @Override
  public Strings str() {
    return str;
  }

  @Override
  public Binaries bin() {
    return bin;
  }

  @Override
  public Resolver resolver() {
    return resolver;
  }

  @Override
  public Common common() {
    return this.common;
  }
}
