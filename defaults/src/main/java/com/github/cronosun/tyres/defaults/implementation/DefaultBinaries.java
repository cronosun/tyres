package com.github.cronosun.tyres.defaults.implementation;

import com.github.cronosun.tyres.core.*;
import com.github.cronosun.tyres.defaults.backends.BinBackend;
import java.io.InputStream;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
final class DefaultBinaries implements Resources.Binaries {

  private final BinBackend backend;

  DefaultBinaries(BinBackend backend) {
    this.backend = backend;
  }

  @Override
  public @Nullable InputStream maybe(BinRes resource, Locale locale) {
    var resInfo = resource.info();
    return backend.maybeBin(resource.info(), locale);
  }

  @Override
  public InputStream get(BinRes resource, Locale locale) {
    var maybeInputStream = maybe(resource, locale);
    if (maybeInputStream != null) {
      return maybeInputStream;
    } else {
      throw exceptionResourceNotFound(resource.info());
    }
  }

  private TyResException exceptionResourceNotFound(ResInfo resInfo) {
    var bundle = resInfo.bundle();
    var conciseDebugString = resInfo.conciseDebugString();
    var bundleDebugReference = bundle.baseName();
    return new TyResException(
      "Binary resource " +
      conciseDebugString +
      " not found in bundle " +
      bundleDebugReference +
      "."
    );
  }
}
