package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.TyRes;
import com.github.cronosun.tyres.core.TyResImplementation;
import com.github.cronosun.tyres.implementation.backends.MsgStrBackend;
import com.github.cronosun.tyres.implementation.implementation.ResourcesConstructor;
import java.util.Locale;
import java.util.Set;
import org.springframework.context.annotation.Bean;

public abstract class AbstractTyResConfiguration {

  protected Set<Locale> localesForValidation() {
    return Set.of();
  }

  protected MsgNotFoundStrategy msgNotFoundStrategy() {
    return MsgNotFoundStrategy.FALLBACK;
  }

  @Bean
  protected TyResImplementation tyResImplementation() {
    return TyRes.implementation();
  }

  @Bean
  protected BundleCreator bundleCreator(
    TyResImplementation tyResImplementation,
    Resources resources
  ) {
    return new DefaultBundleCreator(tyResImplementation, resources, localesForValidation());
  }

  @Bean
  protected Resources resources() {
    var strBackend = strBackend();
    return new ResourcesConstructor(msgNotFoundStrategy()).msgStrBackend(strBackend).construct();
  }

  protected final MsgStrBackend strBackend() {
    return new SpringMsgStrBackend(
      MessageSourceProvider.cached(MessageSourceFactory.resourceBundle())
    );
  }
}
