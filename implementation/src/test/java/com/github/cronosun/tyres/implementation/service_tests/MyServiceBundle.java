package com.github.cronosun.tyres.implementation.service_tests;

import com.github.cronosun.tyres.core.experiment.Fmt;
import com.github.cronosun.tyres.core.experiment.Text;

public interface MyServiceBundle {
  Text amountIsMissing();

  Fmt amountIsTooSmall(int givenAmount);

  Fmt amountIsTooLarge(int givenAmount);
}
