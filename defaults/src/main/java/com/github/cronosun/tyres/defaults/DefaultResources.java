package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public final class DefaultResources implements Resources {

  private final NotFoundStrategy notFoundStrategy;
  private final FallbackGenerator fallbackGenerator;
  private final StringBackend backend;

  public static DefaultResources newDefaultImplementation(NotFoundStrategy notFoundStrategy) {
    return new DefaultResources(
      notFoundStrategy,
      FallbackGenerator.defaultImplementation(),
      StringBackend.usingResourceBundle()
    );
  }

  public DefaultResources(
    NotFoundStrategy notFoundStrategy,
    FallbackGenerator fallbackGenerator,
    StringBackend backend
  ) {
    this.notFoundStrategy = notFoundStrategy;
    this.fallbackGenerator = fallbackGenerator;
    this.backend = backend;
  }

  @Override
  public String message(MsgRes resource, NotFoundStrategy notFoundStrategy, Locale locale) {
    var args = processArgsForMessage(resource.args(), locale, notFoundStrategy);
    final boolean throwOnError;
    switch (notFoundStrategy) {
      case FALLBACK:
        throwOnError = false;
        break;
      case THROW:
        throwOnError = true;
        break;
      default:
        throw new TyResException("Unknown not found strategy: " + notFoundStrategy);
    }
    var message = this.backend.maybeMessage(resource, args, locale, throwOnError);
    if (message != null) {
      return message;
    } else {
      var resInfo = resource.info();
      switch (notFoundStrategy) {
        case FALLBACK:
          return fallbackFor(resInfo, args);
        case THROW:
          var bundle = resInfo.bundle();
          var debugReference = resInfo.debugReference();
          var bundleDebugReference = bundle.baseName();
          throw new TyResException(
            "Resource " + debugReference + " not found in bundle " + bundleDebugReference + "."
          );
        default:
          throw new TyResException("Unknown not found strategy: " + notFoundStrategy);
      }
    }
  }

  @Override
  @Nullable
  public String maybeMessage(MsgRes resource, Locale locale) {
    var argsForMaybeMessage = processArgsForMaybeMessage(resource.args(), locale);
    final Object[] args;
    if (argsForMaybeMessage != null) {
      args = argsForMaybeMessage.args;
    } else {
      args = resource.args();
    }
    var message = this.backend.maybeMessage(resource, args, locale, false);
    if (argsForMaybeMessage != null && argsForMaybeMessage.notFound) {
      // if one of the arguments cannot be resolved, the entire message cannot be resolved
      return null;
    } else {
      return message;
    }
  }

  @Override
  public NotFoundStrategy notFoundStrategy() {
    return notFoundStrategy;
  }

  @Override
  public String fallbackFor(ResInfo resInfo, Object[] args) {
    return fallbackGenerator.generateFallbackMessageFor(resInfo, args);
  }

  private Object[] processArgsForMessage(
    Object[] args,
    Locale locale,
    NotFoundStrategy notFoundStrategy
  ) {
    var numberOfArgs = args.length;
    if (numberOfArgs == 0) {
      return args;
    } else {
      Object[] newArgs = null;
      for (var index = 0; index < numberOfArgs; index++) {
        var existingArg = args[index];
        var maybeResolvable = maybeMsg(existingArg);
        if (maybeResolvable != null) {
          if (newArgs == null) {
            newArgs = args.clone();
          }
          newArgs[index] = new ArgForMessage(maybeResolvable, locale, this, notFoundStrategy);
        }
      }
      return Objects.requireNonNullElse(newArgs, args);
    }
  }

  @Nullable
  private ArgsForMaybeMessage processArgsForMaybeMessage(Object[] args, Locale locale) {
    var numberOfArgs = args.length;
    if (numberOfArgs == 0) {
      return null;
    } else {
      ArgsForMaybeMessage argsForMaybeMessage = null;
      for (var index = 0; index < numberOfArgs; index++) {
        var existingArg = args[index];
        var maybeResolvable = maybeMsg(existingArg);
        if (maybeResolvable != null) {
          if (argsForMaybeMessage == null) {
            var newArgs = args.clone();
            argsForMaybeMessage = new ArgsForMaybeMessage(newArgs);
          }
          argsForMaybeMessage.args[index] =
            new ArgForMaybeMessage(argsForMaybeMessage, maybeResolvable, locale, this);
        }
      }
      return argsForMaybeMessage;
    }
  }

  private static final class ArgsForMaybeMessage {

    private final Object[] args;
    private boolean notFound;

    private ArgsForMaybeMessage(Object[] args) {
      this.args = args;
    }
  }

  private static final class ArgForMaybeMessage {

    private final ArgsForMaybeMessage argsForMaybeMessage;
    private final Msg msg;
    private final Locale locale;
    private final Resources source;

    private ArgForMaybeMessage(
      ArgsForMaybeMessage argsForMaybeMessage,
      Msg msg,
      Locale locale,
      Resources source
    ) {
      this.argsForMaybeMessage = argsForMaybeMessage;
      this.msg = msg;
      this.locale = locale;
      this.source = source;
    }

    @Override
    public String toString() {
      var maybeMsg = source.maybeMessage(msg, locale);
      if (maybeMsg != null) {
        return maybeMsg;
      } else {
        this.argsForMaybeMessage.notFound = true;
        return "";
      }
    }
  }

  private static final class ArgForMessage {

    private final Msg msg;
    private final Locale locale;
    private final Resources source;
    private final NotFoundStrategy notFoundStrategy;

    private ArgForMessage(
      Msg msg,
      Locale locale,
      Resources source,
      NotFoundStrategy notFoundStrategy
    ) {
      this.msg = msg;
      this.locale = locale;
      this.source = source;
      this.notFoundStrategy = notFoundStrategy;
    }

    @Override
    public String toString() {
      return source.message(msg, notFoundStrategy, locale);
    }
  }

  @Nullable
  private static Msg maybeMsg(Object object) {
    if (object instanceof Msg) {
      return (Msg) object;
    }
    return null;
  }
}
