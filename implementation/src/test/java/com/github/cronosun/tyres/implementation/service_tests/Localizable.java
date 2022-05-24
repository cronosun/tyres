package com.github.cronosun.tyres.implementation.service_tests;

import com.github.cronosun.tyres.core.Resources;
import java.util.Locale;

public interface Localizable<TSelf> {
  TSelf localize(Resources resources, Locale locale);
}