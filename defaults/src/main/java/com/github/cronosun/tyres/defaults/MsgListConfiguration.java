package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.TyRes;

/**
 * Configuration for {@link MsgList}.
 *
 * A message list is formatted like this:
 *
 * <ul>
 *     <li>Empty list: {@link #empty()}</li>
 *     <li>Non-empty list: {@link #prefix()} ITEM1 {@link #delimiter()} ITEM2 {@link #suffix()}</li>
 * </ul>
 *
 */
public interface MsgListConfiguration {
  MsgListConfiguration INSTANCE = TyRes.create(MsgListConfiguration.class);

  /**
   * Message to use if the list is empty.
   */
  MsgRes empty();

  /**
   * Prefix for non-empty lists.
   */
  MsgRes prefix();

  /**
   * Delimiter for non-empty lists.
   */
  MsgRes delimiter();

  /**
   * Suffix for non-empty lists.
   */
  MsgRes suffix();
}