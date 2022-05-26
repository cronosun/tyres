package com.github.cronosun.tyres.core.experiment;

import com.github.cronosun.tyres.core.*;

/**
 * Configuration for {@link ResolvableList}.
 * <p>
 * A message list is formatted like this:
 *
 * <ul>
 *     <li>Empty list: {@link #empty()}</li>
 *     <li>Non-empty list: {@link #prefix()} ITEM1 {@link #delimiter()} ITEM2 {@link #suffix()}</li>
 * </ul>
 */
public interface ResolvableListConfiguration {
  /**
   * Message to use if the list is empty.
   */
  @Default("")
  Text empty();

  /**
   * Message to use if the list has one single item.
   */
  @Default("{0}")
  Fmt single(Resolvable item);

  /**
   * Prefix for non-empty lists.
   */
  @Default("")
  Text prefix();

  /**
   * Delimiter for non-empty lists.
   */
  @Default(", ")
  Text delimiter();

  /**
   * Suffix for non-empty lists.
   */
  @Default("")
  Text suffix();
}
