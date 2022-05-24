package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.BaseName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SimpleBundleTest {

  @Test
  void voidMethodIsCorrect() {
    var res = SimpleBundle.INSTANCE.voidMethod();
    var info = res.info();
    var method = info.method();
    var bundleInfo = info.bundle();

    Assertions.assertEquals(0, res.args().length);
    Assertions.assertEquals(BaseName.fromClass(SimpleBundle.class), bundleInfo.baseName());
    Assertions.assertEquals("voidMethod", method.getName());
    Assertions.assertEquals(SimpleBundle.class, method.getDeclaringClass());
    Assertions.assertEquals(SimpleBundle.class, bundleInfo.bundleClass());
    Assertions.assertEquals(method.getName(), info.name());
  }

  @Test
  void methodWithArgumentIsCorrect() {
    var res = SimpleBundle.INSTANCE.methodWithArgument("Hello");
    var info = res.info();
    var method = info.method();
    var bundleInfo = info.bundle();

    Assertions.assertEquals(1, res.args().length);
    Assertions.assertEquals("Hello", res.args()[0]);
    Assertions.assertEquals(BaseName.fromClass(SimpleBundle.class), bundleInfo.baseName());
    Assertions.assertEquals("methodWithArgument", method.getName());
    Assertions.assertEquals(SimpleBundle.class, method.getDeclaringClass());
    Assertions.assertEquals(SimpleBundle.class, bundleInfo.bundleClass());
    Assertions.assertEquals(method.getName(), info.name());
  }

  @Test
  void methodCanBeRenamed1() {
    var res = SimpleBundle.INSTANCE.renamedMethod();
    var info = res.info();

    Assertions.assertEquals("renamedMethod", info.name());
  }

  @Test
  void methodCanBeRenamed2() {
    var res = SimpleBundle.INSTANCE.renamedMethodTwo();
    var info = res.info();

    Assertions.assertEquals("renamedMethod", info.name());
  }

  @Test
  void defaultValueIsNullIfThereIsNoAnnotation() {
    var res = SimpleBundle.INSTANCE.renamedMethodTwo();
    var info = res.info();

    Assertions.assertNull(info.defaultValue());
  }

  @Test
  void defaultValueCanBeProvided() {
    var res = SimpleBundle.INSTANCE.methodWithDefaultValue();
    var info = res.info();

    Assertions.assertEquals("the_default_value", info.defaultValue());
  }
}
