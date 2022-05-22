package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

final class ConciseDebugString {

  private final StringBuilder builder = new StringBuilder();
  private static final char SEPARATOR = ',';
  private static final String CHILD_SEPARATOR = "::";
  private static final char LIST_START = '[';
  private static final char LIST_END = ']';
  private static final String NULL = "#null#";

  public static ConciseDebugString create() {
    return new ConciseDebugString();
  }

  public String finish() {
    return builder.toString();
  }

  public ConciseDebugString start() {
    return append("{");
  }

  public ConciseDebugString end() {
    return append("}");
  }

  public ConciseDebugString append(String string) {
    builder.append(string);
    return this;
  }

  public ConciseDebugString append(Object item) {
    if (item instanceof WithConciseDebugString) {
      var withConciseDebugString = (WithConciseDebugString) item;
      return append(withConciseDebugString.conciseDebugString());
    } else if (item instanceof Collection) {
      var collection = (Collection<?>) item;
      append(collection.stream());
    } else if (item instanceof Object[]) {
      var objects = (Object[]) item;
      append(Arrays.stream(objects));
    } else {
      if (item != null) {
        var asString = item.toString();
        builder.append(asString);
      } else {
        builder.append(NULL);
      }
    }
    return this;
  }

  public ConciseDebugString append(Stream<?> items) {
    builder.append(LIST_START);
    final boolean[] first = { true };
    items.forEach(item -> {
      if (!first[0]) {
        builder.append(SEPARATOR);
      }
      first[0] = false;
      append(item);
    });
    builder.append(LIST_END);
    return this;
  }

  public ConciseDebugString child() {
    builder.append(CHILD_SEPARATOR);
    return this;
  }

  public ConciseDebugString separator() {
    builder.append(SEPARATOR);
    return this;
  }
}
