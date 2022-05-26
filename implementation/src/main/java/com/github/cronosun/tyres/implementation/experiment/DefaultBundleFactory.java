package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.TyResException;
import com.github.cronosun.tyres.core.experiment.*;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

final class DefaultBundleFactory implements BundleFactory {

  private final ResourcesBackend backend;

  public DefaultBundleFactory(ResourcesBackend backend) {
    this.backend = backend;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T createBundle(Class<T> bundleClass) {
    var bundleInfo = BundleInfo.reflect(bundleClass);
    var resourcesMap = ResourcesMap.from(bundleInfo, backend);
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
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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

    public static ResourcesMap from(BundleInfo bundleInfo, ResourcesBackend backend) {
      var numberOfMethods = bundleInfo.numberOfMethods();
      var map = bundleInfo
        .unvalidatedMethods()
        .map(methodInfo -> {
          var key = methodInfo.method().getName();
          var value = (WithArguments) ResourcesMap.fromMethodInfoToImplementation(
            bundleInfo,
            methodInfo,
            backend
          );
          return new MapEntry<>(key, value);
        })
        .collect(Collectors.toUnmodifiableMap(item -> item.key, item -> item.value));
      if (map.size() != numberOfMethods) {
        var methodNames = bundleInfo
          .unvalidatedMethods()
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
      BundleInfo bundleInfo,
      MethodInfo methodInfo,
      ResourcesBackend backend
    ) {
      methodInfo.assertValid();
      var returnType = methodInfo.returnType();
      switch (returnType) {
        case TEXT:
          return new TextImpl(bundleInfo, methodInfo, backend);
        case FMT:
          return new FmtImpl(bundleInfo, methodInfo, backend);
        case BIN:
          return new BinImpl(bundleInfo, methodInfo, backend);
        default:
          throw new TyResException("Unknown resource type: " + returnType);
      }
    }
  }

  private static final class TextImpl implements Text, WithArguments<TextImpl> {

    private final BundleInfo bundleInfo;
    private final MethodInfo methodInfo;
    private final ResourcesBackend backend;

    private TextImpl(BundleInfo bundleInfo, MethodInfo methodInfo, ResourcesBackend backend) {
      this.bundleInfo = bundleInfo;
      this.methodInfo = methodInfo;
      this.backend = backend;
    }

    @Override
    public String get(@Nullable Locale locale, NotFoundConfig notFoundConfig) {
      return backend.getText(bundleInfo, methodInfo, locale, notFoundConfig);
    }

    @Override
    public @Nullable String maybe(@Nullable Locale locale) {
      return backend.maybeText(bundleInfo, methodInfo, locale);
    }

    @Override
    public TextImpl withArguments(Object[] args) {
      if (args.length == 0) {
        return this;
      }
      throw new TyResException("Texts (not formatted) cannot have arguments");
    }
  }

  private static final class FmtImpl implements Fmt, WithArguments<Fmt> {

    private static final Object[] NO_ARGS = new Object[] {};
    private final BundleInfo bundleInfo;
    private final MethodInfo methodInfo;
    private final ResourcesBackend backend;

    private FmtImpl(BundleInfo bundleInfo, MethodInfo methodInfo, ResourcesBackend backend) {
      this.bundleInfo = bundleInfo;
      this.methodInfo = methodInfo;
      this.backend = backend;
    }

    @Override
    public String get(@Nullable Locale locale, NotFoundConfig notFoundConfig) {
      return backend.getFmt(bundleInfo, methodInfo, NO_ARGS, locale, notFoundConfig);
    }

    @Override
    public @Nullable String maybe(@Nullable Locale locale) {
      return backend.maybeFmt(bundleInfo, methodInfo, NO_ARGS, locale);
    }

    @Override
    public Fmt withArguments(Object[] args) {
      if (args.length == 0) {
        return this;
      } else {
        return new FmtWithArgsImpl(this, args, backend);
      }
    }
  }

  private static final class FmtWithArgsImpl implements Fmt {

    private final FmtImpl noArgs;
    private final Object[] args;

    private FmtWithArgsImpl(FmtImpl noArgs, Object[] args, ResourcesBackend backend) {
      this.noArgs = noArgs;
      this.args = args;
    }

    @Override
    public String get(@Nullable Locale locale, NotFoundConfig notFoundConfig) {
      return noArgs.backend.getFmt(
        noArgs.bundleInfo,
        noArgs.methodInfo,
        args,
        locale,
        notFoundConfig
      );
    }

    @Override
    public @Nullable String maybe(@Nullable Locale locale) {
      return noArgs.backend.maybeFmt(noArgs.bundleInfo, noArgs.methodInfo, args, locale);
    }
  }

  private static final class BinImpl implements Bin, WithArguments<BinImpl> {

    private final BundleInfo bundleInfo;
    private final MethodInfo methodInfo;
    private final ResourcesBackend backend;

    private BinImpl(BundleInfo bundleInfo, MethodInfo methodInfo, ResourcesBackend backend) {
      this.bundleInfo = bundleInfo;
      this.methodInfo = methodInfo;
      this.backend = backend;
    }

    @Override
    public InputStream get(@Nullable Locale locale) {
      return backend.getBin(bundleInfo, methodInfo, locale);
    }

    @Override
    public @Nullable InputStream maybe(@Nullable Locale locale) {
      return backend.maybeBin(bundleInfo, methodInfo, locale);
    }

    @Override
    public BinImpl withArguments(Object[] args) {
      if (args.length == 0) {
        return this;
      }
      throw new TyResException("Binary cannot have arguments");
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
