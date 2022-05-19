package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.ResInfo;
import com.github.cronosun.tyres.core.ResInfoDetails;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.defaults.StringBackend;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public final class SpringMsgSourceBackend implements StringBackend {

  private final MessageSourceCreator messageSourceCreator;

  public SpringMsgSourceBackend(MessageSourceCreator messageSourceCreator) {
    this.messageSourceCreator = messageSourceCreator;
  }

  @Override
  public @Nullable String maybeMessage(ResInfo resInfo, Object[] args, Locale locale) {
    // TODO: Cache
    assertCorrectResourceType(resInfo);
    var createdSource = messageSourceCreator.createMessageSource(resInfo, locale);
    var msgSource = createdSource.messageSource();

    var name = resInfo.details().asStringResource().name();
    try {
      return msgSource.getMessage(name, args, null, locale);
    } catch (IllegalArgumentException iae) {
      var bundleRef = resInfo.bundle().baseName().value();
      throw new TyResException(
        "Invalid format / cannot parse: '" +
        name +
        "' (locale " +
        locale +
        ") in bundle " +
        bundleRef +
        "'.",
        iae
      );
    }
  }

  @Override
  public @Nullable String maybeString(ResInfo resInfo, Locale locale) {
    // TODO: Implement me
    return null;
  }

  private void assertCorrectResourceType(ResInfo resInfo) {
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
