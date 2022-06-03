package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Text;

public interface BundleThatValidates {
  Text sayHello();

  Fmt sayHelloTo(String name);
}
