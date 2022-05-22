package com.github.cronosun.tyres.spring.bundle_factory;

import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.spring.BundleFactory;
import com.github.cronosun.tyres.spring.FactoryForBundle;

public interface BundleWithFactory {
  MsgRes noSuchFileError(String filename);

  @FactoryForBundle(BundleWithFactory.class)
  final class Factory extends BundleFactory {}
}
