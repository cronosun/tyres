package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

final class DefaultMsgResources implements MsgResources {

  private final StrBackend backend;
  private final MsgNotFoundStrategy notFoundStrategy;
  private final FallbackGenerator fallbackGenerator;

  DefaultMsgResources(
    StrBackend backend,
    MsgNotFoundStrategy notFoundStrategy,
    FallbackGenerator fallbackGenerator
  ) {
    this.backend = backend;
    this.notFoundStrategy = notFoundStrategy;
    this.fallbackGenerator = fallbackGenerator;
  }

  @Override
  public String get(MsgRes resource, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    var args = processArgsForMessage(resource.args(), locale, notFoundStrategy);
    var message = this.backend.maybeMessage(resource.info(), args, locale);
    if (message != null) {
      return message;
    } else {
      var resInfo = resource.info();
      switch (notFoundStrategy) {
        case FALLBACK:
          return fallbackFor(resInfo, args);
        case THROW:
          throw exceptionResourceNotFound(resInfo);
        default:
          throw new TyResException("Unknown not found strategy: " + notFoundStrategy);
      }
    }
  }

  @Override
  public @Nullable String maybe(MsgRes resource, Locale locale) {
    var argsForMaybeMessage = processArgsForMaybeMessage(resource.args(), locale);
    final Object[] args;
    if (argsForMaybeMessage != null) {
      args = argsForMaybeMessage.args;
    } else {
      args = resource.args();
    }
    var message = this.backend.maybeMessage(resource.info(), args, locale);
    if (argsForMaybeMessage != null && argsForMaybeMessage.notFound) {
      // if one of the arguments cannot be resolved, the entire message cannot be resolved
      return null;
    } else {
      return message;
    }
  }

  @Override
  public MsgNotFoundStrategy notFoundStrategy() {
    return notFoundStrategy;
  }

  @Override
  public String fallbackFor(ResInfo resInfo, Object[] args) {
    return fallbackGenerator.generateFallbackMessageFor(resInfo, args);
  }

  @Nullable
  private static Msg maybeMsg(Object object) {
    if (object instanceof Msg) {
      return (Msg) object;
    }
    return null;
  }

  private TyResException exceptionResourceNotFound(ResInfo resInfo) {
    var bundle = resInfo.bundle();
    var debugReference = resInfo.debugReference();
    var bundleDebugReference = bundle.baseName();
    return new TyResException(
      "Msg resource " + debugReference + " not found in bundle " + bundleDebugReference + "."
    );
  }

  private Object[] processArgsForMessage(
    Object[] args,
    Locale locale,
    MsgNotFoundStrategy notFoundStrategy
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
          newArgs[index] =
            new DefaultMsgResources.ArgForMessage(maybeResolvable, locale, this, notFoundStrategy);
        }
      }
      return Objects.requireNonNullElse(newArgs, args);
    }
  }

  @Nullable
  private DefaultMsgResources.ArgsForMaybeMessage processArgsForMaybeMessage(
    Object[] args,
    Locale locale
  ) {
    var numberOfArgs = args.length;
    if (numberOfArgs == 0) {
      return null;
    } else {
      DefaultMsgResources.ArgsForMaybeMessage argsForMaybeMessage = null;
      for (var index = 0; index < numberOfArgs; index++) {
        var existingArg = args[index];
        var maybeResolvable = maybeMsg(existingArg);
        if (maybeResolvable != null) {
          if (argsForMaybeMessage == null) {
            var newArgs = args.clone();
            argsForMaybeMessage = new DefaultMsgResources.ArgsForMaybeMessage(newArgs);
          }
          argsForMaybeMessage.args[index] =
            new DefaultMsgResources.ArgForMaybeMessage(
              argsForMaybeMessage,
              maybeResolvable,
              locale,
              this
            );
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
    private final MsgResources source;

    private ArgForMaybeMessage(
      ArgsForMaybeMessage argsForMaybeMessage,
      Msg msg,
      Locale locale,
      MsgResources source
    ) {
      this.argsForMaybeMessage = argsForMaybeMessage;
      this.msg = msg;
      this.locale = locale;
      this.source = source;
    }

    @Override
    public String toString() {
      var maybeMsg = source.maybeResolve(msg, locale);
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
    private final MsgResources source;
    private final MsgNotFoundStrategy notFoundStrategy;

    private ArgForMessage(
      Msg msg,
      Locale locale,
      MsgResources source,
      MsgNotFoundStrategy notFoundStrategy
    ) {
      this.msg = msg;
      this.locale = locale;
      this.source = source;
      this.notFoundStrategy = notFoundStrategy;
    }

    @Override
    public String toString() {
      return source.resolve(msg, notFoundStrategy, locale);
    }
  }
}
