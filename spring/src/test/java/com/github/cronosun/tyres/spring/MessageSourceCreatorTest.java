package com.github.cronosun.tyres.spring;

import static org.junit.jupiter.api.Assertions.*;

import com.github.cronosun.tyres.core.MsgSource;
import com.github.cronosun.tyres.defaults.DefaultMsgSource;
import com.github.cronosun.tyres.defaults.FallbackGenerator;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class MessageSourceCreatorTest {

  @Test
  void demo() {
    var xy = new DefaultMessageSourceCreator();
    var backend = new SpringMsgSourceBackend(xy);
    var source = new DefaultMsgSource(
      MsgSource.NotFoundStrategy.THROW,
      FallbackGenerator.defaultImplementation(),
      backend
    );

    var msg = source.message(TestBundle.INSTANCE.sayHello(), Locale.ENGLISH);
    assertEquals("Hello, world!", msg);
  }
}
