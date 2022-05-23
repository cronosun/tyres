package com.github.cronosun.tyres.core;

import java.util.stream.Stream;

public interface WithConciseDebugString {
  static Object text(String text) {
    return ConciseDebugStringBuilder.text(text);
  }

  static Object association(Object key, Object value) {
    return ConciseDebugStringBuilder.association(key, value);
  }

  static String build(Iterable<?> items) {
    return ConciseDebugStringBuilder.build(items);
  }

  static String build(Stream<?> items) {
    return ConciseDebugStringBuilder.build(items);
  }

  /**
   * This is similar to {@link #toString()} but does not contain as much information. Contains only the
   * information that's necessary for a developer to identify the object.
   */
  String conciseDebugString();
}
