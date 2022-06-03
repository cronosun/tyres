package com.github.cronosun.tyres.implementation.service_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.core.Resolvable;
import com.github.cronosun.tyres.implementation.TestUtil;
import com.github.cronosun.tyres.implementation.res_info_from_text.RestInfoFromTextBundle;
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

  @Test
  void testEqualityAndInequalityForTextAndFmt() {
    // correct equality and inequality is important for testing services.

    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundleOne = resources.get(MyServiceBundle.class);
    var bundleTwo = resources.get(MyServiceBundle.class);

    // equality
    assertEquals(bundleOne.amountIsMissing(), bundleTwo.amountIsMissing());
    assertEquals(bundleOne.amountIsTooLarge(15), bundleTwo.amountIsTooLarge(15));
    assertEquals(bundleOne.amountIsTooSmall(4), bundleTwo.amountIsTooSmall(4));
    assertEquals(
      bundleOne.somethingWithTestObject(new TestObject(556, "hello")),
      bundleTwo.somethingWithTestObject(new TestObject(556, "hello"))
    );

    // inequality 1
    assertNotEquals(bundleOne.amountIsTooLarge(3), bundleOne.amountIsTooLarge(4));
    assertNotEquals(bundleOne.amountIsTooSmall(3), bundleOne.amountIsTooSmall(4));
    assertNotEquals(
      bundleOne.somethingWithTestObject(new TestObject(3, "hello")),
      bundleTwo.somethingWithTestObject(new TestObject(556, "hello"))
    );

    // inequality 2
    assertNotEquals(bundleOne.amountIsTooLarge(3), bundleOne.amountIsMissing());
    assertNotEquals(bundleOne.amountIsTooLarge(3), bundleOne.amountIsTooSmall(9));
    assertNotEquals(
      bundleOne.amountIsTooLarge(3),
      bundleOne.somethingWithTestObject(new TestObject(3, "hello"))
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
