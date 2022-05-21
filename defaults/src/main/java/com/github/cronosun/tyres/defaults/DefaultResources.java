package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.*;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public final class DefaultResources
  implements Resources, BinResources, StrResources, MsgResources {

  private final MsgNotFoundStrategy notFoundStrategy;
  private final FallbackGenerator fallbackGenerator;
  private final StringBackend stringBackend;
  private final BinBackend binBackend;

  public DefaultResources(
    MsgNotFoundStrategy notFoundStrategy,
    @Nullable FallbackGenerator fallbackGenerator,
    @Nullable StringBackend stringBackend,
    @Nullable BinBackend binBackend
  ) {
    this.notFoundStrategy = Objects.requireNonNull(notFoundStrategy);
    this.fallbackGenerator =
      Objects.requireNonNullElse(fallbackGenerator, FallbackGenerator.defaultImplementation());
    this.stringBackend =
      Objects.requireNonNullElse(stringBackend, StringBackend.usingResourceBundle());
    this.binBackend = Objects.requireNonNullElse(binBackend, BinBackend.usingResources());
  }

  public static DefaultResources newDefault(MsgNotFoundStrategy notFoundStrategy) {
    return new DefaultResources(notFoundStrategy, null, null, null);
  }

  @Nullable
  private static Msg maybeMsg(Object object) {
    if (object instanceof Msg) {
      return (Msg) object;
    }
    return null;
  }

  @Override
  public String msg(MsgRes resource, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    var args = processArgsForMessage(resource.args(), locale, notFoundStrategy);
    var message = this.stringBackend.maybeMessage(resource.info(), args, locale);
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
  public @Nullable String maybeMsg(MsgRes resource, Locale locale) {
    var argsForMaybeMessage = processArgsForMaybeMessage(resource.args(), locale);
    final Object[] args;
    if (argsForMaybeMessage != null) {
      args = argsForMaybeMessage.args;
    } else {
      args = resource.args();
    }
    var message = this.stringBackend.maybeMessage(resource.info(), args, locale);
    if (argsForMaybeMessage != null && argsForMaybeMessage.notFound) {
      // if one of the arguments cannot be resolved, the entire message cannot be resolved
      return null;
    } else {
      return message;
    }
  }

  @Override
  public MsgNotFoundStrategy msgNotFoundStrategy() {
    return notFoundStrategy;
  }

  @Override
  public @Nullable String maybeStr(StrRes resource, Locale locale) {
    return this.stringBackend.maybeString(resource.info(), locale);
  }

  @Override
  public String str(StrRes resource, Locale locale) {
    var maybeString = this.stringBackend.maybeString(resource.info(), locale);
    if (maybeString != null) {
      return maybeString;
    } else {
      throw exceptionResourceNotFound(resource.info());
    }
  }

  @Override
  public @Nullable InputStream maybeBin(BinRes resource, Locale locale) {
    return binBackend.maybeBin(resource.info(), locale);
  }

  @Override
  public InputStream bin(BinRes resource, Locale locale) {
    var maybeInputStream = maybeBin(resource, locale);
    if (maybeInputStream != null) {
      return maybeInputStream;
    } else {
      throw exceptionResourceNotFound(resource.info());
    }
  }

  @Override
  public String fallbackFor(ResInfo resInfo, Object[] args) {
    return fallbackGenerator.generateFallbackMessageFor(resInfo, args);
  }

  private TyResException exceptionResourceNotFound(ResInfo resInfo) {
    var bundle = resInfo.bundle();
    var debugReference = resInfo.debugReference();
    var bundleDebugReference = bundle.baseName();
    return new TyResException(
      "Resource " + debugReference + " not found in bundle " + bundleDebugReference + "."
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

  @Override
  public MsgResources msg() {
    return this;
  }

  @Override
  public StrResources str() {
    return this;
  }

  @Override
  public BinResources bin() {
    return this;
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
      var maybeMsg = source.msg().maybeResolveMsg(msg, locale);
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
    private final MsgNotFoundStrategy notFoundStrategy;

    private ArgForMessage(
      Msg msg,
      Locale locale,
      Resources source,
      MsgNotFoundStrategy notFoundStrategy
    ) {
      this.msg = msg;
      this.locale = locale;
      this.source = source;
      this.notFoundStrategy = notFoundStrategy;
    }

    @Override
    public String toString() {
      return source.msg().resolveMsg(msg, notFoundStrategy, locale);
    }
  }
}
