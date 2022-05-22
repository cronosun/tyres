package com.github.cronosun.tyres.spring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.defaults.BinBackend;
import com.github.cronosun.tyres.defaults.DefaultResources;
import com.github.cronosun.tyres.defaults.FallbackGenerator;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class MessageSourceCreatorTest {

  @Test
  void demo() {
    var xy = new DefaultMessageSourceCreator();
    var backend = new SpringMsgSourceBackend(xy);
    var source = new DefaultResources(
      MsgNotFoundStrategy.THROW,
      FallbackGenerator.defaultImplementation(),
      backend,
      BinBackend.usingResources(),
      null,
      null
    );

    var msg = source.msg().get(TestBundle.INSTANCE.sayHello(), Locale.ENGLISH);
    assertEquals("Hello, world!", msg);
  }
}
