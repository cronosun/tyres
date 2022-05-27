package com.github.cronosun.tyres.implementation;

import com.github.cronosun.tyres.core.*;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

final class DefaultBundleFactory implements BundleFactory {

  private final Once<Resources> resources;
  private final ResourcesBackend backend;
  private final EffectiveNameGenerator effectiveNameGenerator;

  public DefaultBundleFactory(
    Once<Resources> resources,
    ResourcesBackend backend,
    EffectiveNameGenerator effectiveNameGenerator
  ) {
    this.resources = resources;
    this.backend = backend;
    this.effectiveNameGenerator = effectiveNameGenerator;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T createBundle(Class<T> bundleClass) {
    var originalBundleInfo = BundleInfo.reflect(bundleClass);
    var effectiveBaseName = effectiveNameGenerator.effectiveBaseName(
      bundleClass,
      originalBundleInfo.baseName()
    );
    var bundleInfo = originalBundleInfo.withEffectiveBaseName(effectiveBaseName);

    var resourcesMap = ResourcesMap.from(this, bundleInfo);
    var invocationHandler = new InvocationHandler(resourcesMap);
    return (T) Proxy.newProxyInstance(
      bundleClass.getClassLoader(),
      new Class[] { bundleClass },
      invocationHandler
    );
  }

  private Resources resources() {
    return this.resources.get();
  }

  @Override
  public Stream<ResInfo> declaredResourcesForValidation(Object bundle) {
    var handler = Proxy.getInvocationHandler(bundle);
    if (handler instanceof InvocationHandler) {
      var cast = (InvocationHandler) handler;
      return cast.resourcesMap.map
        .values()
        .stream()
        .map(ResourcesMap.WithArgumentsAndResInfo::resInfo);
    } else {
      throw new TyResException(
        "Unable to get resouces from '" +
        bundle +
        "'. The bundle must have been created by this factory (it's not). Read the documentation!"
      );
    }
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
    private final Map<String, WithArgumentsAndResInfo> map;

    private ResourcesMap(BundleInfo bundleInfo, Map<String, WithArgumentsAndResInfo> map) {
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

    public static ResourcesMap from(DefaultBundleFactory factory, BundleInfo bundleInfo) {
      var numberOfMethods = bundleInfo.numberOfMethods();
      var map = bundleInfo
        .resources(factory.effectiveNameGenerator)
        .map(resInfo -> {
          var key = resInfo.method().getName();
          var value = (WithArgumentsAndResInfo) ResourcesMap.fromMethodInfoToImplementation(
            factory,
            resInfo
          );
          return new MapEntry<>(key, value);
        })
        .collect(Collectors.toUnmodifiableMap(item -> item.key, item -> item.value));
      if (map.size() != numberOfMethods) {
        var methodNames = bundleInfo
          .resources(factory.effectiveNameGenerator)
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

    private static WithArgumentsAndResInfo<?> fromMethodInfoToImplementation(
      DefaultBundleFactory factory,
      ResInfo resInfo
    ) {
      if (resInfo instanceof ResInfo.TextResInfo) {
        var text = (ResInfo.TextResInfo) resInfo;
        switch (text.type()) {
          case TEXT:
            return new TextImpl(factory, text);
          case FMT:
            return new FmtImpl(factory, text);
          default:
            throw new TyResException("Unknown text type: " + text.type());
        }
      } else if (resInfo instanceof ResInfo.BinResInfo) {
        var bin = (ResInfo.BinResInfo) resInfo;
        return new BinImpl(factory, bin);
      } else {
        throw new TyResException("Unknown resource type: " + resInfo);
      }
    }

    private static final class TextImpl implements Text, WithArgumentsAndResInfo<TextImpl> {

      private final DefaultBundleFactory factory;
      private final ResInfo.TextResInfo info;

      private TextImpl(DefaultBundleFactory factory, ResInfo.TextResInfo info) {
        this.factory = factory;
        this.info = info;
      }

      @Override
      public TextImpl withArguments(Object[] args) {
        if (args.length == 0) {
          return this;
        }
        throw new TyResException("Texts (not formatted) cannot have arguments");
      }

      @Override
      public ResInfo resInfo() {
        return info;
      }

      @Override
      public @Nullable String getText(
        @Nullable Locale locale,
        NotFoundConfig.WithNullAndDefault notFoundConfig
      ) {
        return factory.backend.getText(factory.resources(), info, locale, notFoundConfig);
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextImpl text = (TextImpl) o;
        return factory.equals(text.factory) && info.equals(text.info);
      }

      @Override
      public int hashCode() {
        return Objects.hash(factory, info);
      }

      @Override
      public String toString() {
        return "TextImpl{" + "factory=" + factory + ", info=" + info + '}';
      }

      @Override
      public String conciseDebugString() {
        return info.conciseDebugString();
      }
    }

    private static final class FmtImpl implements Fmt, WithArgumentsAndResInfo<Fmt> {

      private static final Object[] NO_ARGS = new Object[] {};
      private final DefaultBundleFactory factory;
      private final ResInfo.TextResInfo info;

      private FmtImpl(DefaultBundleFactory factory, ResInfo.TextResInfo info) {
        this.factory = factory;
        this.info = info;
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
      public ResInfo resInfo() {
        return info;
      }

      @Override
      public @Nullable String getText(
        @Nullable Locale locale,
        NotFoundConfig.WithNullAndDefault notFoundConfig
      ) {
        return factory.backend.getFmt(factory.resources(), info, NO_ARGS, locale, notFoundConfig);
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FmtImpl fmt = (FmtImpl) o;
        return factory.equals(fmt.factory) && info.equals(fmt.info);
      }

      @Override
      public int hashCode() {
        return Objects.hash(factory, info);
      }

      @Override
      public String toString() {
        return "FmtImpl{" + "factory=" + factory + ", info=" + info + '}';
      }

      @Override
      public String conciseDebugString() {
        return info.conciseDebugString();
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
        return noArgs.factory.backend.getFmt(
          noArgs.factory.resources(),
          noArgs.info,
          args,
          locale,
          notFoundConfig
        );
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

      @Override
      public String conciseDebugString() {
        return WithConciseDebugString.build(List.of(noArgs.info, args));
      }
    }

    private static final class BinImpl implements Bin, WithArgumentsAndResInfo<BinImpl> {

      private final DefaultBundleFactory factory;
      private final ResInfo.BinResInfo info;

      private BinImpl(DefaultBundleFactory factory, ResInfo.BinResInfo info) {
        this.factory = factory;
        this.info = info;
      }

      @Override
      public BinImpl withArguments(Object[] args) {
        if (args.length == 0) {
          return this;
        }
        throw new TyResException("Binary cannot have arguments");
      }

      @Override
      public ResInfo resInfo() {
        return info;
      }

      @Override
      public @Nullable InputStream getInputStream(@Nullable Locale locale, boolean required) {
        return factory.backend.getInputStream(factory.resources(), info, locale, required);
      }

      @Override
      public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinImpl bin = (BinImpl) o;
        return factory.equals(bin.factory) && info.equals(bin.info);
      }

      @Override
      public int hashCode() {
        return Objects.hash(factory, info);
      }

      @Override
      public String toString() {
        return "BinImpl{" + "factory=" + factory + ", info=" + info + '}';
      }

      @Override
      public String conciseDebugString() {
        return info.conciseDebugString();
      }
    }

    interface WithArgumentsAndResInfo<TSelf> {
      TSelf withArguments(Object[] args);
      ResInfo resInfo();
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
