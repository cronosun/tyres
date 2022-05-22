package com.github.cronosun.tyres.defaults.service_tests;

import com.github.cronosun.tyres.core.Resolvable;
import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public final class ValidationException
  extends RuntimeException
  implements Localizable<ValidationException> {

  private final Resolvable resolvable;

  @Nullable
  private final String localizedMessage;

  public ValidationException(Resolvable resolvable) {
    this(resolvable, null);
  }

  private ValidationException(Resolvable resolvable, @Nullable String localizedMessage) {
    this.resolvable = resolvable;
    this.localizedMessage = localizedMessage;
  }

  public Resolvable msg() {
    return resolvable;
  }

  @Override
  public ValidationException localize(Resources resources, Locale locale) {
    var localizedMessage = resources.resolver().get(this.resolvable, locale);
    return new ValidationException(resolvable, localizedMessage);
  }

  @Override
  public String getMessage() {
    return resolvable.conciseDebugString();
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
