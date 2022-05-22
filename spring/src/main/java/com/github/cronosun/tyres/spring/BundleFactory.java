package com.github.cronosun.tyres.spring;

import com.github.cronosun.tyres.core.TyResException;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BundleFactory implements FactoryBean<Object> {

  @Autowired
  private BundleCreator creator;

  @Nullable
  private volatile Class<?> bundleClass;

  public BundleFactory() {}

  public final Class<?> bundleClass() {
    var localBundleClass = this.bundleClass;
    if (localBundleClass == null) {
      var thisClass = getClass();
      var annotation = thisClass.getAnnotation(FactoryForBundle.class);
      if (annotation == null) {
        throw new TyResException(
          "Missing " +
          FactoryForBundle.class.getSimpleName() +
          " annotation for '" +
          thisClass.getName() +
          "'."
        );
      }
      localBundleClass = annotation.value();
      this.bundleClass = localBundleClass;
    }
    return localBundleClass;
  }

  @Override
  public final Object getObject() {
    return creator.createBundle(bundleClass());
  }

  @Override
  public final Class<?> getObjectType() {
    return bundleClass();
  }

  @Override
  public final boolean isSingleton() {
    return true;
  }
}
