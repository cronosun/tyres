package com.github.cronosun.tyres.defaults.service_tests;

import com.github.cronosun.tyres.core.Msg;
import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public final class ValidationException
  extends RuntimeException
  implements Localizable<ValidationException> {

  private final Msg msg;

  @Nullable
  private final String localizedMessage;

  public ValidationException(Msg msg) {
    this(msg, null);
  }

  private ValidationException(Msg msg, @Nullable String localizedMessage) {
    this.msg = msg;
    this.localizedMessage = localizedMessage;
  }

  public Msg msg() {
    return msg;
  }

  @Override
  public ValidationException localize(Resources resources, Locale locale) {
    var localizedMessage = resources.msg().resolve(this.msg, locale);
    return new ValidationException(msg, localizedMessage);
  }

  @Override
  public String getMessage() {
    return msg.conciseDebugString();
  }

  @Override
  public String getLocalizedMessage() {
    if (localizedMessage == null) {
      return getMessage();
    } else {
      return this.localizedMessage;
    }
  }
}
