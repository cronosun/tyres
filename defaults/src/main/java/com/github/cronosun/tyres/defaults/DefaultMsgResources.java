package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
final class DefaultMsgResources implements MsgResources {

  private final StrBackend backend;
  private final Resources resources;

  DefaultMsgResources(StrBackend backend, Resources resources) {
    this.backend = backend;
    this.resources = resources;
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
          return resources.fallbackFor(resInfo, args);
        case THROW:
          throw exceptionResourceNotFound(resInfo);
        default:
          throw new TyResException("Unknown not found strategy: " + notFoundStrategy);
      }
    }
  }

  @Override
  public String get(MsgRes resource, Locale locale) {
    return get(resource, resources.notFoundStrategy(), locale);
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

  @Nullable
  private static Resolvable maybeMsg(Object object) {
    if (object instanceof Resolvable) {
      return (Resolvable) object;
    }
    return null;
  }

  private TyResException exceptionResourceNotFound(ResInfo resInfo) {
    var bundle = resInfo.bundle();
    var conciseDebugString = resInfo.conciseDebugString();
    var bundleConciseDebugString = bundle.conciseDebugString();
    return new TyResException(
      "Msg resource " +
      conciseDebugString +
      " not found in bundle " +
      bundleConciseDebugString +
      "."
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
            new DefaultMsgResources.ArgForMessage(
              maybeResolvable,
              locale,
              resources,
              notFoundStrategy
            );
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
              resources
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
    private final Resolvable resolvable;
    private final Locale locale;
    private final Resources source;

    private ArgForMaybeMessage(
      ArgsForMaybeMessage argsForMaybeMessage,
      Resolvable resolvable,
      Locale locale,
      Resources source
    ) {
      this.argsForMaybeMessage = argsForMaybeMessage;
      this.resolvable = resolvable;
      this.locale = locale;
      this.source = source;
    }

    @Override
    public String toString() {
      var maybeMsg = source.resolver().maybe(resolvable, locale);
      if (maybeMsg != null) {
        return maybeMsg;
      } else {
        this.argsForMaybeMessage.notFound = true;
        return "";
      }
    }
  }

  private static final class ArgForMessage {

    private final Resolvable resolvable;
    private final Locale locale;
    private final Resources source;
    private final MsgNotFoundStrategy notFoundStrategy;

    private ArgForMessage(
      Resolvable resolvable,
      Locale locale,
      Resources source,
      MsgNotFoundStrategy notFoundStrategy
    ) {
      this.resolvable = resolvable;
      this.locale = locale;
      this.source = source;
      this.notFoundStrategy = notFoundStrategy;
    }

    @Override
    public String toString() {
      return source.resolver().get(resolvable, notFoundStrategy, locale);
    }
  }
}
