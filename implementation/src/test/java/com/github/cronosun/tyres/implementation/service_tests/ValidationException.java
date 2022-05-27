package com.github.cronosun.tyres.implementation.service_tests;

import com.github.cronosun.tyres.core.experiment.Resolvable;

public final class ValidationException extends RuntimeException {

  private final Resolvable message;

  public ValidationException(Resolvable message) {
    super(message.conciseDebugString());
    this.message = message;
  }

  public Resolvable message() {
    return message;
  }
}
