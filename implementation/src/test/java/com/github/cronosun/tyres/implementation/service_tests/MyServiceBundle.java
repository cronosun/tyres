package com.github.cronosun.tyres.implementation.service_tests;

import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Text;

public interface MyServiceBundle {
  Text amountIsMissing();

  Fmt amountIsTooSmall(int givenAmount);

  Fmt amountIsTooLarge(int givenAmount);

  Fmt somethingWithTestObject(TestObject testObject);
}
