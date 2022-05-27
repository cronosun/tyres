package com.github.cronosun.tyres.implementation.basic_usage;

import com.github.cronosun.tyres.core.Bin;
import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.File;
import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Resolvable;
import com.github.cronosun.tyres.core.Text;
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
