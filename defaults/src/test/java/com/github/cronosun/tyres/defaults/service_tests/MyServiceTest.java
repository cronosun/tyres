package com.github.cronosun.tyres.defaults.service_tests;

import com.github.cronosun.tyres.core.Resolvable;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class MyServiceTest {

  private final MyService myService = new MyService();

  @Test
  void testAmountIsRequired() {
    validateThrowsValidationExceptionWithMessage(
      () -> myService.doSomethingWithAmount(UUID.randomUUID(), null),
      MyServiceBundle.INSTANCE.amountIsMissing()
    );
  }

  @Test
  void testAmountMustNotBeTooSmall() {
    validateThrowsValidationExceptionWithMessage(
      () -> myService.doSomethingWithAmount(UUID.randomUUID(), 4),
      MyServiceBundle.INSTANCE.amountIsTooSmall(4)
    );
  }

  @Test
  void testAmountMustNotBeTooLarge() {
    validateThrowsValidationExceptionWithMessage(
      () -> myService.doSomethingWithAmount(UUID.randomUUID(), 98887),
      MyServiceBundle.INSTANCE.amountIsTooLarge(98887)
    );
  }

  @Test
  void testAmountMustNotBeTooLargeVariantTwo() {
    validateThrowsValidationExceptionWithMessage(
      () -> myService.doSomethingWithAmountVariantTwo(UUID.randomUUID(), 98887),
      MyServiceBundle.INSTANCE.amountIsTooLarge(98887)
    );
  }

  private void validateThrowsValidationExceptionWithMessage(
    Runnable runnable,
    Resolvable message
  ) {
    try {
      runnable.run();
      throw new AssertionError("Expected an exception");
    } catch (ValidationException validationException) {
      if (!validationException.msg().equals(message)) {
        throw new AssertionError(
          "Invalid message, expected '" +
          message.conciseDebugString() +
          "' but got '" +
          validationException.msg().conciseDebugString() +
          "'."
        );
      }
    }
  }
}
