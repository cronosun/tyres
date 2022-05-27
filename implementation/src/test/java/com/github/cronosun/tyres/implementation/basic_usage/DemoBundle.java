package com.github.cronosun.tyres.implementation.basic_usage;

import com.github.cronosun.tyres.core.*;
import java.math.BigDecimal;

public interface DemoBundle {
  Text unformattedText();

  @Default("See the @Default annotation.")
  Text unformattedTextWithDefault();

  Fmt formattedTextNoArgument();
  Fmt somethingWithArgument(String name);
  Fmt somethingWithTwoArguments(String firstName, String lastName);

  Fmt argumentsAreResolved(Resolvable resolvableOne, Resolvable resolvableTwo);

  @Default("Formatted with default says ''{0}''!")
  Fmt formattedWithDefault(String name);

  Fmt price(BigDecimal amount);

  @File("filename.txt")
  Bin somethingBinary();
}
