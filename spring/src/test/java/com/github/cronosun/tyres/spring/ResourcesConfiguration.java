package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.Resources;
import com.github.cronosun.tyres.implementation.MessageFormatter;
import com.github.cronosun.tyres.implementation.ResourcesBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourcesConfiguration {

  @Bean
  Resources resources() {
    var textBackend = new SpringTextBackend(
      MessageSourceProvider.cached(MessageSourceFactory.resourceBundle()),
      MessageFormatter.newCachedMessageFormatter()
    );
    var resourcesBuilder = new ResourcesBuilder();
    resourcesBuilder.textBackend().setValue(textBackend);
    return resourcesBuilder.build();
  }
}
