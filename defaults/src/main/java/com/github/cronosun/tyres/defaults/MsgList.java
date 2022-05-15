package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Msg;
import com.github.cronosun.tyres.core.MsgSource;
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

  private final MsgListConfiguration configuration;
  private final List<? extends Msg> messages;
  private static final MsgList EMPTY = new MsgList(MsgListConfiguration.INSTANCE, List.of());

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

  private MsgList(MsgListConfiguration configuration, List<Msg> messages) {
    this.configuration = configuration;
    this.messages = messages;
  }

  public List<? extends Msg> messages() {
    return messages;
  }

  public MsgListConfiguration configuration() {
    return configuration;
  }

  @Override
  public String message(
    MsgSource source,
    MsgSource.NotFoundStrategy notFoundStrategy,
    Locale locale
  ) {
    var messages = this.messages;
    if (messages.isEmpty()) {
      return source.message(configuration.empty(), notFoundStrategy, locale);
    } else {
      final String prefix = source.message(configuration.prefix(), notFoundStrategy, locale);
      final String delimiter = source.message(configuration.delimiter(), notFoundStrategy, locale);
      final String suffix = source.message(configuration.suffix(), notFoundStrategy, locale);
      return messages
        .stream()
        .map(message -> source.message(message, notFoundStrategy, locale))
        .collect(Collectors.joining(delimiter, prefix, suffix));
    }
  }

  @Nullable
  @Override
  public String maybeMessage(MsgSource source, Locale locale) {
    var messages = this.messages;
    if (messages.isEmpty()) {
      return source.maybeMessage(configuration.empty(), locale);
    } else {
      final String prefix = source.maybeMessage(configuration.prefix(), locale);
      final String delimiter = source.maybeMessage(configuration.delimiter(), locale);
      final String suffix = source.maybeMessage(configuration.suffix(), locale);
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
          var maybeMessage = source.maybeMessage(message, locale);
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
