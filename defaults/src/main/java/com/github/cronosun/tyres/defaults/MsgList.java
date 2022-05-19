package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Msg;
import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.core.Resources;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

/**
 * A list of messages.
 */
public final class MsgList implements Msg {

  private static final MsgList EMPTY = new MsgList(MsgListConfiguration.INSTANCE, List.of());
  private final MsgListConfiguration configuration;
  private final List<? extends Msg> messages;

  private MsgList(MsgListConfiguration configuration, List<? extends Msg> messages) {
    this.configuration = configuration;
    this.messages = messages;
  }

  public static MsgList fromStream(
    MsgListConfiguration configuration,
    Stream<? extends Msg> messages
  ) {
    return new MsgList(configuration, messages.collect(Collectors.toUnmodifiableList()));
  }

  public static MsgList fromStream(Stream<? extends Msg> messages) {
    return fromStream(MsgListConfiguration.INSTANCE, messages);
  }

  public static MsgList fromList(
    MsgListConfiguration configuration,
    List<? extends Msg> messages
  ) {
    return new MsgList(configuration, Collections.unmodifiableList(messages));
  }

  public static MsgList fromList(List<? extends Msg> messages) {
    return new MsgList(MsgListConfiguration.INSTANCE, Collections.unmodifiableList(messages));
  }

  public static MsgList empty(MsgListConfiguration configuration) {
    return new MsgList(configuration, List.of());
  }

  public static MsgList empty() {
    return EMPTY;
  }

  public List<? extends Msg> messages() {
    return messages;
  }

  public MsgListConfiguration configuration() {
    return configuration;
  }

  @Override
  public String msg(Resources resources, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    var messages = this.messages;
    var numberOfMessages = messages.size();
    switch (numberOfMessages) {
      case 0:
        return resources.msg(configuration.empty(), notFoundStrategy, locale);
      case 1:
        var single = messages.get(0);
        return resources.msg(configuration.single(single), notFoundStrategy, locale);
      default:
        final String prefix = resources.msg(configuration.prefix(), notFoundStrategy, locale);
        final String delimiter = resources.msg(
          configuration.delimiter(),
          notFoundStrategy,
          locale
        );
        final String suffix = resources.msg(configuration.suffix(), notFoundStrategy, locale);
        return messages
          .stream()
          .map(message -> resources.resolveMsg(message, notFoundStrategy, locale))
          .collect(Collectors.joining(delimiter, prefix, suffix));
    }
  }

  @Nullable
  @Override
  public String maybeMsg(Resources resources, Locale locale) {
    var messages = this.messages;
    if (messages.isEmpty()) {
      return resources.maybeMsg(configuration.empty(), locale);
    } else {
      final String prefix = resources.maybeMsg(configuration.prefix(), locale);
      final String delimiter = resources.maybeMsg(configuration.delimiter(), locale);
      final String suffix = resources.maybeMsg(configuration.suffix(), locale);
      if (prefix == null || delimiter == null || suffix == null) {
        return null;
      }

      final boolean[] atLeastOneIsMissing = new boolean[] { false };
      var resultString = messages
        .stream()
        .map(message -> {
          if (atLeastOneIsMissing[0]) {
            return "";
          }
          var maybeMessage = resources.maybeResolveMsg(message, locale);
          if (maybeMessage == null) {
            atLeastOneIsMissing[0] = true;
            return "";
          }
          return maybeMessage;
        })
        .collect(Collectors.joining(delimiter, prefix, suffix));
      if (atLeastOneIsMissing[0]) {
        return null;
      } else {
        return resultString;
      }
    }
  }
}
