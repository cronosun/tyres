package com.github.cronosun.tyres.core;

import java.util.stream.Stream;

public interface WithConciseDebugString {
  @ThreadSafe
  static Object text(String text) {
    return ConciseDebugStringBuilder.text(text);
  }

  @ThreadSafe
  static Object association(Object key, Object value) {
    return ConciseDebugStringBuilder.association(key, value);
  }

  @ThreadSafe
  static String build(Iterable<?> items) {
    return ConciseDebugStringBuilder.build(items);
  }

  @ThreadSafe
  static String build(Stream<?> items) {
    return ConciseDebugStringBuilder.build(items);
  }

  /**
   * This is similar to {@link #toString()} but does not contain as much information. Contains only the
   * information that's necessary for a developer to identify the object.
   */
  @ThreadSafe
  String conciseDebugString();
}
