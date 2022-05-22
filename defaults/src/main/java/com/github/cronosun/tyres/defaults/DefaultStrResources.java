package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
final class DefaultStrResources implements StrResources {

  private final Resources resources;
  private final StrBackend backend;

  public DefaultStrResources(Resources resources, StrBackend strBackend) {
    this.resources = resources;
    this.backend = strBackend;
  }

  @Override
  public @Nullable String maybe(StrRes resource, Locale locale) {
    var resInfo = resource.info();
    assertCorrectResourceKind(resInfo);
    return this.backend.maybeString(resInfo, locale);
  }

  @Override
  public String get(StrRes resource, Locale locale) {
    return get(resource, resources.notFoundStrategy(), locale);
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
          return resources.fallbackFor(resource.info(), resource.args());
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

  private void assertCorrectResourceKind(ResInfo resInfo) {
    var kind = resInfo.details().kind();
    var correctType = kind == ResInfoDetails.Kind.STRING;
    if (!correctType) {
      throw new TyResException(
        "Invalid resource kind (must be a string resource). It's " +
        kind +
        ". Resource '" +
        resInfo.conciseDebugString() +
        "'."
      );
    }
  }
}
