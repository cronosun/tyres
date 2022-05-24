package com.github.cronosun.tyres.core;

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
  ResolvableListConfiguration INSTANCE = TyRes.create(ResolvableListConfiguration.class);

  /**
   * Message to use if the list is empty.
   */
  @Default("")
  StrRes empty();

  /**
   * Message to use if the list has one single item.
   */
  @Default("{0}")
  MsgRes single(Resolvable item);

  /**
   * Prefix for non-empty lists.
   */
  @Default("")
  StrRes prefix();

  /**
   * Delimiter for non-empty lists.
   */
  @Default(", ")
  StrRes delimiter();

  /**
   * Suffix for non-empty lists.
   */
  @Default("")
  StrRes suffix();
}
