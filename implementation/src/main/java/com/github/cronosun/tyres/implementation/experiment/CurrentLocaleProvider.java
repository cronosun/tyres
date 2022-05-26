package com.github.cronosun.tyres.implementation.experiment;

import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public interface CurrentLocaleProvider {
  @Nullable
  Locale currentLocale();
}
