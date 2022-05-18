package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.*;
import com.github.cronosun.tyres.defaults.StringBackend;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.Nullable;

public final class SpringMsgSourceBackend implements StringBackend {

  private static final Logger LOGGER = Logger.getLogger(SpringMsgSourceBackend.class.getName());
  private final MessageSourceCreator messageSourceCreator;

  public SpringMsgSourceBackend(MessageSourceCreator messageSourceCreator) {
    this.messageSourceCreator = messageSourceCreator;
  }

  @Override
  public @Nullable String maybeMessage(
    ResInfo resInfo,
    Object[] args,
    Locale locale,
    boolean throwOnError
  ) {
    // TODO: Cache
    if (!isCorrectResourceType(resInfo, throwOnError)) {
      return null;
    }
    var createdSource = messageSourceCreator.createMessageSource(resInfo, locale);
    var msgSource = createdSource.messageSource();

    var name = resInfo.details().asStringResouce().name();
    try {
      return msgSource.getMessage(name, args, null, locale);
    } catch (IllegalArgumentException iae) {
      var bundleRef = resInfo.bundle().baseName().value();
      if (throwOnError) {
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
      } else {
        LOGGER.log(Level.INFO, "Invalid format", iae);
        return null;
      }
    }
  }

  @Override
  public @Nullable String maybeString(ResInfo resInfo, Locale locale, boolean throwOnError) {
    // TODO: Implement me
    return null;
  }

  private boolean isCorrectResourceType(ResInfo resInfo, boolean throwOnError) {
    var kind = resInfo.details().kind();
    var correctType = kind == ResInfoDetails.Kind.STRING;
    if (throwOnError && !correctType) {
      throw new TyResException(
        "Invalid resource kind (must be a string resource). It's " +
        kind +
        ". Resource '" +
        resInfo.debugReference() +
        "'."
      );
    }
    return correctType;
  }
}
