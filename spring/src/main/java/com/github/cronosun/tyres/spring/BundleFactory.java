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
    // TODO: Man sollte auch das feld "INSTANCE" anschauen, man wird das auch
    // in spring so noch brauchen: Man will ja die Message auch irgendwo referenzieren
    // können wo es kein Spring gibt. Oder alternativ was auch gehen würde:
    // Wir machen noch ein `Resolvable`? Eines mit einer "Function<Bundle,Resolveable>"?
    // ... wird dann aber schwierig die bundle-instanz zu kriegen.

    // Was auch denkbar ist: Dann brauchen wir gar nichts mehr im bereich Spring:
    // vielleich eine weitere TyRes implementation für spring wo alles aufzeichnet
    // was generiert wurde? Dann hätten wir das für das validieren auch drinn...
    // ... dann brauchts diese Bundle factory gar nicht mehr.
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
