package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

final class DefaultStrResources implements StrResources {

  private final StrBackend backend;

  public DefaultStrResources(StrBackend strBackend) {
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
    var maybeString = maybe(resource, locale);
    if (maybeString != null) {
      return maybeString;
    } else {
      throw exceptionResourceNotFound(resource.info());
    }
  }

  private TyResException exceptionResourceNotFound(ResInfo resInfo) {
    var bundle = resInfo.bundle();
    var debugReference = resInfo.debugReference();
    var bundleDebugReference = bundle.baseName();
    return new TyResException(
      "String resource " + debugReference + " not found in bundle " + bundleDebugReference + "."
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
        resInfo.debugReference() +
        "'."
      );
    }
  }
}
