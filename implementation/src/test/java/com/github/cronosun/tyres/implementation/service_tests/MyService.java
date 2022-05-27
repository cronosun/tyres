package com.github.cronosun.tyres.implementation.service_tests;

import com.github.cronosun.tyres.core.experiment.Resolvable;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class MyService {

  public void doSomethingWithAmount(UUID id, @Nullable Integer amount) {
    if (amount == null) {
      throw new ValidationException(
        Resolvable.constant(MyServiceBundle.class, MyServiceBundle::amountIsMissing)
      );
    }
    if (amount < 10) {
      throw new ValidationException(
        Resolvable.constant(MyServiceBundle.class, bundle -> bundle.amountIsTooSmall(amount))
      );
    }
    if (amount > 550) {
      throw new ValidationException(
        Resolvable.constant(MyServiceBundle.class, bundle -> bundle.amountIsTooLarge(amount))
      );
    }
    // ok, validation seems to be ok.
    // <...> now can do something here
  }
}
