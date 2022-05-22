package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.core.TyRes;
import com.github.cronosun.tyres.core.TyResImplementation;
import com.github.cronosun.tyres.defaults.DefaultResources;
import com.github.cronosun.tyres.defaults.backends.MsgStrBackend;
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
    return new DefaultResources(msgNotFoundStrategy(), null, strBackend, null, null, null);
  }

  protected final MsgStrBackend strBackend() {
    return new SpringMsgStrBackend(
      MessageSourceProvider.cached(MessageSourceFactory.resourceBundle())
    );
  }
}
