package com.github.cronosun.tyres.implementation.implementation;

import com.github.cronosun.tyres.core.*;
import com.github.cronosun.tyres.implementation.backends.MsgStrBackend;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
final class DefaultStrings implements Resources.Strings {

  private final Resources resources;
  private final MsgStrBackend backend;

  public DefaultStrings(Resources resources, MsgStrBackend msgStrBackend) {
    this.resources = resources;
    this.backend = msgStrBackend;
  }

  @Override
  public @Nullable String maybe(StrRes resource, Locale locale) {
    var resInfo = resource.info();
    return this.backend.maybeString(resInfo, locale);
  }

  @Override
  public String get(StrRes resource, Locale locale) {
    return get(resource, resources.common().notFoundStrategy(), locale);
  }

  @Override
  public String get(StrRes resource, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    var maybeString = maybe(resource, locale);
    if (maybeString != null) {
      return maybeString;
    } else {
      switch (notFoundStrategy) {
        case THROW:
          throw exceptionResourceNotFound(resource.info());
        case FALLBACK:
          return resources.common().fallbackFor(resource.info(), resource.args());
        default:
          throw new IllegalArgumentException("Unknown not-found strategy: " + notFoundStrategy);
      }
    }
  }

  private TyResException exceptionResourceNotFound(ResInfo resInfo) {
    var bundle = resInfo.bundle();
    var conciseDebugString = resInfo.conciseDebugString();
    var bundleDebugReference = bundle.baseName();
    return new TyResException(
      "String resource " +
      conciseDebugString +
      " not found in bundle " +
      bundleDebugReference +
      "."
    );
  }
}
