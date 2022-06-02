package com.github.cronosun.tyres.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.implementation.MessageFormatter;
import com.github.cronosun.tyres.implementation.ResourcesBuilder;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class MessageSourceCreatorTest {

  @Test
  void demo() {
    var textBackend = new SpringTextBackend(
      MessageSourceProvider.cached(MessageSourceFactory.resourceBundle()),
      MessageFormatter.newCachedMessageFormatter()
    );
    var resourcesBuilder = new ResourcesBuilder();
    resourcesBuilder.textBackend().setValue(textBackend);
    var resources = resourcesBuilder.build();

    resources.validate(TestBundle.class, Locale.ENGLISH);

    var msg = resources.get(TestBundle.class).sayHello().get(Locale.ENGLISH);
    assertEquals("Hello, world!", msg);
  }
}
