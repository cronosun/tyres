package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Msg;
import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.MsgSource;
import com.github.cronosun.tyres.core.TyResException;
import java.util.Locale;
import org.jetbrains.annotations.Nullable;

public final class DefaultMsgSource implements MsgSource {

  private final NotFoundStrategy notFoundStrategy;
  private final ResourceBundleProvider resourceBundleProvider;
  private final MessageFormatter messageFormatter;
  private final FallbackGenerator fallbackGenerator;

  public static DefaultMsgSource newWithDefaults(NotFoundStrategy notFoundStrategy) {
    return new DefaultMsgSource(
      notFoundStrategy,
      DefaultResourceBundleProvider.instance(),
      DefaultMessageFormatter.instance(),
      DefaultFallbackGenerator.instance()
    );
  }

  public DefaultMsgSource(
    NotFoundStrategy notFoundStrategy,
    ResourceBundleProvider resourceBundleProvider,
    MessageFormatter messageFormatter,
    FallbackGenerator fallbackGenerator
  ) {
    this.notFoundStrategy = notFoundStrategy;
    this.resourceBundleProvider = resourceBundleProvider;
    this.messageFormatter = messageFormatter;
    this.fallbackGenerator = fallbackGenerator;
  }

  @Override
  public String message(MsgRes resource, NotFoundStrategy notFoundStrategy, Locale locale) {
    var resInfo = resource.info();
    var bundle = resourceBundleProvider.getBundleFor(resInfo);
    var pattern = bundle.getString(resInfo, locale);
    var originalArgs = resource.args();
    var processedArgs = processArgsForMessage(locale, notFoundStrategy, originalArgs);
    switch (notFoundStrategy) {
      case THROW:
        if (pattern != null) {
          return messageFormatter.format(pattern, locale, processedArgs, this, notFoundStrategy);
        } else {
          var debugReference = resInfo.debugReference();
          var bundleDebugReference = bundle.debugReference();
          throw new TyResException(
            "Resource " + debugReference + " not found in bundle " + bundleDebugReference + "."
          );
        }
      case FALLBACK:
        if (pattern != null) {
          var msg = messageFormatter.maybeFormat(pattern, locale, processedArgs, this);
          if (msg == null) {
            return fallbackFor(resource);
          } else {
            return msg;
          }
        } else {
          return fallbackFor(resource);
        }
      default:
        throw new TyResException(
          "Unknown not found strategy: " + notFoundStrategy + ". This is an implementation error."
        );
    }
  }

  @Nullable
  @Override
  public String maybeMessage(MsgRes resource, Locale locale) {
    var resInfo = resource.info();
    var bundle = resourceBundleProvider.getBundleFor(resInfo);
    var pattern = bundle.getString(resInfo, locale);
    if (pattern != null) {
      var originalArgs = resource.args();
      final Object[] processedArgs;
      final NotFoundMark notFoundMark;
      if (originalArgs.length > 0) {
        notFoundMark = new NotFoundMark();
        processedArgs = processArgsForMaybeMessage(locale, originalArgs, notFoundMark);
      } else {
        notFoundMark = null;
        processedArgs = originalArgs;
      }
      var message = messageFormatter.maybeFormat(pattern, locale, processedArgs, this);
      if (notFoundMark != null && notFoundMark.notFound) {
        // if one argument cannot be resolved, the entire message cannot be resolved.
        return null;
      } else {
        return message;
      }
    } else {
      return null;
    }
  }

  @Override
  public NotFoundStrategy notFoundStrategy() {
    return notFoundStrategy;
  }

  @Override
  public String fallbackFor(MsgRes resource) {
    return fallbackGenerator.generateFallbackMessageFor(resource.info(), resource.args());
  }

  private Object[] processArgsForMaybeMessage(
    Locale locale,
    Object[] args,
    Runnable notFoundMarker
  ) {
    var numberOfArgs = args.length;
    if (numberOfArgs == 0) {
      return args;
    }
    Object[] newArgs = null;
    for (var index = 0; index < numberOfArgs; index++) {
      var existingArg = args[index];
      if (existingArg instanceof Msg) {
        if (newArgs == null) {
          newArgs = args.clone();
        }
        newArgs[index] =
          new ArgumentForMaybeMessage(notFoundMarker, this, (Msg) existingArg, locale);
      }
    }
    if (newArgs != null) {
      return newArgs;
    } else {
      return args;
    }
  }

  private Object[] processArgsForMessage(
    Locale locale,
    NotFoundStrategy notFoundStrategy,
    Object[] args
  ) {
    var numberOfArgs = args.length;
    if (numberOfArgs == 0) {
      return args;
    }
    Object[] newArgs = null;
    for (var index = 0; index < numberOfArgs; index++) {
      var existingArg = args[index];
      if (existingArg instanceof Msg) {
        if (newArgs == null) {
          newArgs = args.clone();
        }
        newArgs[index] = new ArgumentForMessage(this, (Msg) existingArg, locale, notFoundStrategy);
      }
    }
    if (newArgs != null) {
      return newArgs;
    } else {
      return args;
    }
  }

  private static final class ArgumentForMaybeMessage {

    private final Runnable notFoundMarker;
    private final MsgSource source;
    private final Msg msg;
    private final Locale locale;

    private ArgumentForMaybeMessage(
      Runnable notFoundMarker,
      MsgSource source,
      Msg msg,
      Locale locale
    ) {
      this.notFoundMarker = notFoundMarker;
      this.source = source;
      this.msg = msg;
      this.locale = locale;
    }

    @Override
    public String toString() {
      var translatedMessage = source.maybeMessage(msg, locale);
      if (translatedMessage != null) {
        return translatedMessage;
      } else {
        notFoundMarker.run();
        return "";
      }
    }
  }

  private static final class ArgumentForMessage {

    private final MsgSource source;
    private final Msg msg;
    private final Locale locale;
    private final NotFoundStrategy notFoundStrategy;

    private ArgumentForMessage(
      MsgSource source,
      Msg msg,
      Locale locale,
      NotFoundStrategy notFoundStrategy
    ) {
      this.source = source;
      this.msg = msg;
      this.locale = locale;
      this.notFoundStrategy = notFoundStrategy;
    }

    @Override
    public String toString() {
      return source.message(msg, notFoundStrategy, locale);
    }
  }

  private static final class NotFoundMark implements Runnable {

    private boolean notFound;

    @Override
    public void run() {
      notFound = true;
    }
  }
}
