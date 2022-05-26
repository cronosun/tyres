package com.github.cronosun.tyres.implementation.static_translation;

import com.github.cronosun.tyres.core.experiment.Resolvable;

public final class ValidationException extends RuntimeException {

  private static final Resolvable AMOUNT_TOO_LARGE_MSG = Resolvable.constant(
    StaticTranslationBundle.class,
    StaticTranslationBundle::validationGivenAmountIsTooLarge
  );
  private final Resolvable message;

  public ValidationException(Resolvable message) {
    this.message = message;
  }

  public static ValidationException newAmountTooLargeValidationException() {
    return new ValidationException(AMOUNT_TOO_LARGE_MSG);
  }

  public Resolvable message() {
    return message;
  }
}
