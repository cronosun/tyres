package com.github.cronosun.tyres.implementation.service_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.Resolvable;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class MyServiceTest {

  private final MyService myService = new MyService();

  @Test
  void testAmountIsRequired() {
    validateThrowsValidationExceptionWithMessage(
      () -> myService.doSomethingWithAmount(UUID.randomUUID(), null),
      Resolvable.constant(MyServiceBundle.class, MyServiceBundle::amountIsMissing)
    );
  }

  @Test
  void testAmountMustNotBeTooSmall() {
    validateThrowsValidationExceptionWithMessage(
      () -> myService.doSomethingWithAmount(UUID.randomUUID(), 4),
      Resolvable.constant(MyServiceBundle.class, bundle -> bundle.amountIsTooSmall(4))
    );
  }

  @Test
  void testAmountMustNotBeTooLarge() {
    validateThrowsValidationExceptionWithMessage(
      () -> myService.doSomethingWithAmount(UUID.randomUUID(), 98887),
      Resolvable.constant(MyServiceBundle.class, bundle -> bundle.amountIsTooLarge(98887))
    );
  }

  private void validateThrowsValidationExceptionWithMessage(
    Runnable runnable,
    Resolvable message
  ) {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);

    try {
      runnable.run();
      throw new AssertionError("Expected an exception");
    } catch (ValidationException validationException) {
      // only text resources can be used for equals / hashcode, so we have to resolve it.
      var givenMessage = validationException.message();
      var resolvedGivenMessage = resources.resolve(givenMessage);
      var resolvedExpectedMessage = resources.resolve(message);
      assertEquals(resolvedExpectedMessage, resolvedGivenMessage);
    }
  }
}
