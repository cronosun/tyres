package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import java.util.Locale;
import java.util.Set;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestTyResConfiguration extends AbstractTyResConfiguration {

  @Override
  protected Set<Locale> localesForValidation() {
    return Set.of(Locale.ENGLISH, Locale.GERMAN);
  }

  @Override
  protected MsgNotFoundStrategy msgNotFoundStrategy() {
    return MsgNotFoundStrategy.THROW;
  }
}
