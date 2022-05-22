package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.io.InputStream;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

final class DefaultBinResources implements BinResources {

  private final BinBackend backend;

  DefaultBinResources(BinBackend backend) {
    this.backend = backend;
  }

  @Override
  public @Nullable InputStream maybe(BinRes resource, Locale locale) {
    var resInfo = resource.info();
    assertCorrectResourceKind(resInfo);
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

  private void assertCorrectResourceKind(ResInfo resInfo) {
    var kind = resInfo.details().kind();
    var correctType = kind == ResInfoDetails.Kind.BINARY;
    if (!correctType) {
      throw new TyResException(
        "Invalid resource kind (must be a binary resource). It's " +
        kind +
        ". Resource '" +
        resInfo.conciseDebugString() +
        "'."
      );
    }
  }
}
