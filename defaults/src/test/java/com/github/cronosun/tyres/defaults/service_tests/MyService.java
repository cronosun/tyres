package com.github.cronosun.tyres.defaults.service_tests;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.defaults.Implementation;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public class MyService {

  private final CurrentLocaleService currentLocale = new CurrentLocaleService();
  private final Resources resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

  public void doSomethingWithAmount(UUID id, @Nullable Integer amount) {
    if (amount == null) {
      throw new ValidationException(MyServiceBundle.INSTANCE.amountIsMissing());
    }
    if (amount < 10) {
      throw new ValidationException(MyServiceBundle.INSTANCE.amountIsTooSmall(amount));
    }
    if (amount > 550) {
      throw new ValidationException(MyServiceBundle.INSTANCE.amountIsTooLarge(amount));
    }
    // ok, validation seems to be ok.
    // <...> now can do something here
  }

  /**
   * Alternatively you could also translate exceptions in the service (or catch them centrally before returning the
   * HTTP response to the client).
   */
  public void doSomethingWithAmountVariantTwo(UUID id, @Nullable Integer amount) {
    if (amount == null) {
      throw new ValidationException(MyServiceBundle.INSTANCE.amountIsMissing())
        .localize(resources, currentLocale.getLocale());
    }
    if (amount < 10) {
      throw new ValidationException(MyServiceBundle.INSTANCE.amountIsTooSmall(amount))
        .localize(resources, currentLocale.getLocale());
    }
    if (amount > 550) {
      throw new ValidationException(MyServiceBundle.INSTANCE.amountIsTooLarge(amount))
        .localize(resources, currentLocale.getLocale());
    }
    // ok, validation seems to be ok.
    // <...> now can do something here
  }
}
