package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.core.experiment.*;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

final class DefaultBundleFactory implements BundleFactory {

  private final ResourcesBackend backend;
  private final EffectiveNameGenerator effectiveNameGenerator;

  public DefaultBundleFactory(
    ResourcesBackend backend,
    EffectiveNameGenerator effectiveNameGenerator
  ) {
    this.backend = backend;
    this.effectiveNameGenerator = effectiveNameGenerator;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T createBundle(Resources2 resources, Class<T> bundleClass) {
    var originalBundleInfo = BundleInfo.reflect(bundleClass);
    var effectiveBaseName = effectiveNameGenerator.effectiveBaseName(
      bundleClass,
      originalBundleInfo.baseName()
    );
    var bundleInfo = originalBundleInfo.withEffectiveBaseName(effectiveBaseName);

    var resourcesMap = ResourcesMap.from(resources, bundleInfo, backend, effectiveNameGenerator);
    var invocationHandler = new InvocationHandler(resourcesMap);
    return (T) Proxy.newProxyInstance(
      bundleClass.getClassLoader(),
      new Class[] { bundleClass },
      invocationHandler
    );
  }

  private static final class InvocationHandler implements java.lang.reflect.InvocationHandler {

    private final ResourcesMap resourcesMap;

    private InvocationHandler(ResourcesMap resourcesMap) {
      this.resourcesMap = resourcesMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
      return resourcesMap.get(method, args);
    }
  }

  @SuppressWarnings("rawtypes")
  private static final class ResourcesMap {

    private final BundleInfo bundleInfo;
    private final Map<String, WithArguments> map;

    private ResourcesMap(BundleInfo bundleInfo, Map<String, WithArguments> map) {
      this.bundleInfo = bundleInfo;
      this.map = map;
    }

    public Object get(Method method, Object[] args) {
      var name = method.getName();
      var entry = map.get(name);
      if (entry == null) {
        throw new TyResException(
          "Entry '" +
          name +
          "' not found in bundle " +
          bundleInfo.conciseDebugString() +
          "' (this should not happen, could be a TyRes implementation error)."
        );
      }
      if (args != null && args.length != 0) {
        return entry.withArguments(args);
      } else {
        return entry;
      }
    }

    public static ResourcesMap from(
      Resources2 resources,
      BundleInfo bundleInfo,
      ResourcesBackend backend,
      ResInfo.EffectiveNameGenerator effectiveNameGenerator
    ) {
      var numberOfMethods = bundleInfo.numberOfMethods();
      var map = bundleInfo
        .resources(effectiveNameGenerator)
        .map(resInfo -> {
          var key = resInfo.method().getName();
          var value = (WithArguments) ResourcesMap.fromMethodInfoToImplementation(
            resources,
            resInfo,
            backend
          );
          return new MapEntry<>(key, value);
        })
        .collect(Collectors.toUnmodifiableMap(item -> item.key, item -> item.value));
      if (map.size() != numberOfMethods) {
        var methodNames = bundleInfo
          .resources(effectiveNameGenerator)
          .map(item -> item.method().getName())
          .collect(Collectors.joining(", "));
        throw new TyResException(
          "Bundle '" +
          bundleInfo.conciseDebugString() +
          "' contains at least two methods with the same name. That's not supported. Methods: [" +
          methodNames +
          "]."
        );
      }
      return new ResourcesMap(bundleInfo, map);
    }

    private static WithArguments<?> fromMethodInfoToImplementation(
      Resources2 resources,
      ResInfo resInfo,
      ResourcesBackend backend
    ) {
      if (resInfo instanceof ResInfo.Text) {
        var text = (ResInfo.Text) resInfo;
        switch (text.type()) {
          case TEXT:
            return new TextImpl(resources, text, backend);
          case FMT:
            return new FmtImpl(resources, text, backend);
          default:
            throw new TyResException("Unknown text type: " + text.type());
        }
      } else if (resInfo instanceof ResInfo.Bin) {
        var bin = (ResInfo.Bin) resInfo;
        return new BinImpl(resources, bin, backend);
      } else {
        throw new TyResException("Unknown resource type: " + resInfo);
      }
    }

    private static final class TextImpl implements Text, WithArguments<TextImpl> {

      private final Resources2 resources;
      private final ResInfo.Text info;
      private final ResourcesBackend backend;

      private TextImpl(Resources2 resources, ResInfo.Text info, ResourcesBackend backend) {
        this.resources = resources;
        this.info = info;
        this.backend = backend;
      }

      @Override
      public TextImpl withArguments(Object[] args) {
        if (args.length == 0) {
          return this;
        }
        throw new TyResException("Texts (not formatted) cannot have arguments");
      }

      @Override
      public @Nullable String getText(
        @Nullable Locale locale,
        NotFoundConfig.WithNullAndDefault notFoundConfig
      ) {
        return backend.getText(resources, info, locale, notFoundConfig);
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextImpl text = (TextImpl) o;
        return (
          resources.equals(text.resources) &&
          info.equals(text.info) &&
          backend.equals(text.backend)
        );
      }

      @Override
      public int hashCode() {
        return Objects.hash(resources, info, backend);
      }

      @Override
      public String toString() {
        return (
          "TextImpl{" + "resources=" + resources + ", info=" + info + ", backend=" + backend + '}'
        );
      }
    }

    private static final class FmtImpl implements Fmt, WithArguments<Fmt> {

      private static final Object[] NO_ARGS = new Object[] {};
      private final Resources2 resources;
      private final ResInfo.Text info;
      private final ResourcesBackend backend;

      private FmtImpl(Resources2 resources, ResInfo.Text info, ResourcesBackend backend) {
        this.resources = resources;
        this.info = info;
        this.backend = backend;
      }

      @Override
      public Fmt withArguments(Object[] args) {
        if (args.length == 0) {
          return this;
        } else {
          return new FmtWithArgsImpl(this, args);
        }
      }

      @Override
      public @Nullable String getText(
        @Nullable Locale locale,
        NotFoundConfig.WithNullAndDefault notFoundConfig
      ) {
        return backend.getFmt(resources, info, NO_ARGS, locale, notFoundConfig);
      }

      @Override
      public String toString() {
        return (
          "FmtImpl{" + "resources=" + resources + ", info=" + info + ", backend=" + backend + '}'
        );
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FmtImpl fmt = (FmtImpl) o;
        return (
          resources.equals(fmt.resources) && info.equals(fmt.info) && backend.equals(fmt.backend)
        );
      }

      @Override
      public int hashCode() {
        return Objects.hash(resources, info, backend);
      }
    }

    private static final class FmtWithArgsImpl implements Fmt {

      private final FmtImpl noArgs;
      private final Object[] args;

      private FmtWithArgsImpl(FmtImpl noArgs, Object[] args) {
        this.noArgs = noArgs;
        this.args = args;
      }

      @Override
      public @Nullable String getText(
        @Nullable Locale locale,
        NotFoundConfig.WithNullAndDefault notFoundConfig
      ) {
        return noArgs.backend.getFmt(noArgs.resources, noArgs.info, args, locale, notFoundConfig);
      }

      @Override
      public String toString() {
        return "FmtWithArgsImpl{" + "noArgs=" + noArgs + ", args=" + Arrays.toString(args) + '}';
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FmtWithArgsImpl that = (FmtWithArgsImpl) o;
        return noArgs.equals(that.noArgs) && Arrays.equals(args, that.args);
      }

      @Override
      public int hashCode() {
        int result = Objects.hash(noArgs);
        result = 31 * result + Arrays.hashCode(args);
        return result;
      }
    }

    private static final class BinImpl implements Bin, WithArguments<BinImpl> {

      private final Resources2 resources;
      private final ResInfo.Bin info;
      private final ResourcesBackend backend;

      private BinImpl(Resources2 resources, ResInfo.Bin info, ResourcesBackend backend) {
        this.resources = resources;
        this.info = info;
        this.backend = backend;
      }

      @Override
      public BinImpl withArguments(Object[] args) {
        if (args.length == 0) {
          return this;
        }
        throw new TyResException("Binary cannot have arguments");
      }

      @Override
      public @Nullable InputStream getInputStream(@Nullable Locale locale, boolean required) {
        return backend.getInputStream(resources, info, locale, required);
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinImpl bin = (BinImpl) o;
        return (
          resources.equals(bin.resources) && info.equals(bin.info) && backend.equals(bin.backend)
        );
      }

      @Override
      public int hashCode() {
        return Objects.hash(resources, info, backend);
      }

      @Override
      public String toString() {
        return (
          "BinImpl{" + "resources=" + resources + ", info=" + info + ", backend=" + backend + '}'
        );
      }
    }

    interface WithArguments<TSelf> {
      TSelf withArguments(Object[] args);
    }

    private static final class MapEntry<K, V> {

      private final K key;
      private final V value;

      private MapEntry(K key, V value) {
        this.key = key;
        this.value = value;
      }
    }
  }
}
