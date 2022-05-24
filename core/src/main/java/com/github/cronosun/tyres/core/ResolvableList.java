package com.github.cronosun.tyres.core;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

/**
 * A list of 0-n {@link Resolvable}s. This list in turn itself implements {@link Resolvable}.
 */
@ThreadSafe
public final class ResolvableList implements Resolvable {

  private static final ResolvableList EMPTY = new ResolvableList(
    ResolvableListConfiguration.INSTANCE,
    List.of()
  );
  private final ResolvableListConfiguration configuration;
  private final List<? extends Resolvable> elements;

  private ResolvableList(
    ResolvableListConfiguration configuration,
    List<? extends Resolvable> elements
  ) {
    this.configuration = configuration;
    this.elements = elements;
  }

  public static ResolvableList from(
    ResolvableListConfiguration configuration,
    Stream<? extends Resolvable> elements
  ) {
    return new ResolvableList(configuration, elements.collect(Collectors.toUnmodifiableList()));
  }

  public static ResolvableList from(Stream<? extends Resolvable> elements) {
    return from(ResolvableListConfiguration.INSTANCE, elements);
  }

  public static ResolvableList from(
    ResolvableListConfiguration configuration,
    List<? extends Resolvable> resolvable
  ) {
    return new ResolvableList(configuration, Collections.unmodifiableList(resolvable));
  }

  public static ResolvableList from(List<? extends Resolvable> elements) {
    return new ResolvableList(
      ResolvableListConfiguration.INSTANCE,
      Collections.unmodifiableList(elements)
    );
  }

  public static ResolvableList empty(ResolvableListConfiguration configuration) {
    return new ResolvableList(configuration, List.of());
  }

  public static ResolvableList empty() {
    return EMPTY;
  }

  public List<? extends Resolvable> elements() {
    return elements;
  }

  public ResolvableListConfiguration configuration() {
    return configuration;
  }

  @Override
  public String conciseDebugString() {
    return WithConciseDebugString.build(this.elements);
  }

  @Override
  public String get(Resources resources, MsgNotFoundStrategy notFoundStrategy, Locale locale) {
    var messages = this.elements;
    var numberOfMessages = messages.size();
    switch (numberOfMessages) {
      case 0:
        return resources.str().get(configuration.empty(), notFoundStrategy, locale);
      case 1:
        var single = messages.get(0);
        return resources.msg().get(configuration.single(single), notFoundStrategy, locale);
      default:
        final String prefix = resources
          .str()
          .get(configuration.prefix(), notFoundStrategy, locale);
        final String delimiter = resources
          .str()
          .get(configuration.delimiter(), notFoundStrategy, locale);
        final String suffix = resources
          .str()
          .get(configuration.suffix(), notFoundStrategy, locale);
        return messages
          .stream()
          .map(message -> resources.resolver().get(message, notFoundStrategy, locale))
          .collect(Collectors.joining(delimiter, prefix, suffix));
    }
  }

  @Nullable
  @Override
  public String maybe(Resources resources, Locale locale) {
    var messages = this.elements;
    if (messages.isEmpty()) {
      return resources.str().maybe(configuration.empty(), locale);
    } else {
      final String prefix = resources.str().maybe(configuration.prefix(), locale);
      final String delimiter = resources.str().maybe(configuration.delimiter(), locale);
      final String suffix = resources.str().maybe(configuration.suffix(), locale);
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
          var maybeMessage = resources.resolver().maybe(message, locale);
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
