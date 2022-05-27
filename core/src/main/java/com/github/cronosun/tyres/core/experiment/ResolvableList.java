package com.github.cronosun.tyres.core.experiment;

import com.github.cronosun.tyres.core.WithConciseDebugString;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public class ResolvableList implements Resolvable {

  private static final ResolvableList EMPTY = new ResolvableList(
    ResolvableListConfiguration.class,
    List.of()
  );

  private final Class<? extends ResolvableListConfiguration> configuration;
  private final List<? extends Resolvable> elements;

  private ResolvableList(
    Class<? extends ResolvableListConfiguration> configuration,
    List<? extends Resolvable> elements
  ) {
    this.configuration = configuration;
    this.elements = elements;
  }

  public static ResolvableList from(
    Class<? extends ResolvableListConfiguration> configuration,
    Stream<? extends Resolvable> elements
  ) {
    return new ResolvableList(configuration, elements.collect(Collectors.toUnmodifiableList()));
  }

  public static ResolvableList from(Stream<? extends Resolvable> elements) {
    return from(ResolvableListConfiguration.class, elements);
  }

  public static ResolvableList from(
    Class<? extends ResolvableListConfiguration> configuration,
    List<? extends Resolvable> resolvable
  ) {
    return new ResolvableList(configuration, Collections.unmodifiableList(resolvable));
  }

  public static ResolvableList from(List<? extends Resolvable> elements) {
    return new ResolvableList(
      ResolvableListConfiguration.class,
      Collections.unmodifiableList(elements)
    );
  }

  public static ResolvableList empty() {
    return EMPTY;
  }

  @Override
  public Text resolve(Resources2 resources) {
    var configuration = resources.get(this.configuration);
    return new ListText(resources, configuration);
  }

  @Override
  public String conciseDebugString() {
    return WithConciseDebugString.build(elements);
  }

  private final class ListText implements Text {

    private final Resources2 resources;
    private final ResolvableListConfiguration configuration;

    private ListText(Resources2 resources, ResolvableListConfiguration configuration) {
      this.resources = resources;
      this.configuration = configuration;
    }

    @Override
    public @Nullable String getText(
      @Nullable Locale locale,
      NotFoundConfig.WithNullAndDefault notFoundConfig
    ) {
      var messages = ResolvableList.this.elements;
      var numberOfMessages = messages.size();

      switch (numberOfMessages) {
        case 0:
          return configuration.empty().getText(locale, notFoundConfig);
        case 1:
          var single = messages.get(0);
          return configuration.single(single).getText(locale, notFoundConfig);
        default:
          var prefix = configuration.prefix().getText(locale, notFoundConfig);
          if (prefix == null) {
            return null;
          }
          var suffix = configuration.suffix().getText(locale, notFoundConfig);
          if (suffix == null) {
            return null;
          }
          var delimiter = configuration.delimiter().getText(locale, notFoundConfig);
          if (delimiter == null) {
            return null;
          }
          final boolean[] atLeastOneIsMissing = new boolean[] { false };
          var itemsStream =
            ResolvableList.this.elements.stream()
              .map(resolvable -> {
                if (atLeastOneIsMissing[0]) {
                  return "";
                }
                var maybeMessage = resolvable
                  .resolve(this.resources)
                  .getText(locale, notFoundConfig);
                if (maybeMessage == null) {
                  atLeastOneIsMissing[0] = true;
                  return "";
                } else {
                  return maybeMessage;
                }
              });
          var resultString = itemsStream.collect(Collectors.joining(delimiter, prefix, suffix));
          if (atLeastOneIsMissing[0]) {
            return null;
          } else {
            return resultString;
          }
      }
    }

    @Override
    public String conciseDebugString() {
      return ResolvableList.this.conciseDebugString();
    }
  }
}
