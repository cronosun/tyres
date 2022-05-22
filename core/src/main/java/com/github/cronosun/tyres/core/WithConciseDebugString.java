package com.github.cronosun.tyres.core;

public interface WithConciseDebugString {
  /**
   * This is similar to {@link #toString()} but does not contain as much information. Contains only the
   * information that's necessary for a developer to identify the object.
   */
  String conciseDebugString();
}
