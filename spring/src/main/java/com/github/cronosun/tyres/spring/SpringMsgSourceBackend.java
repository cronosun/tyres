package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.Res;
import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.defaults.MsgSourceBackend;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jetbrains.annotations.Nullable;

public final class SpringMsgSourceBackend implements MsgSourceBackend {

  private static final Logger LOGGER = Logger.getLogger(SpringMsgSourceBackend.class.getName());
  private final MessageSourceCreator messageSourceCreator;

  public SpringMsgSourceBackend(MessageSourceCreator messageSourceCreator) {
    this.messageSourceCreator = messageSourceCreator;
  }

  @Nullable
  @Override
  public String maybeMessage(Res<?> resource, Object[] args, Locale locale, boolean throwOnError) {
    // TODO: Cache
    var resInfo = resource.info();
    var createdSource = messageSourceCreator.createMessageSource(resInfo, locale);
    var msgSource = createdSource.messageSource();

    var name = resInfo.name();
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
}
