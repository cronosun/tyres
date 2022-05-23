package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.Resolvable;
import com.github.cronosun.tyres.core.StrRes;
import com.github.cronosun.tyres.core.TyRes;

/**
 * Configuration for {@link MsgList}.
 * <p>
 * A message list is formatted like this:
 *
 * <ul>
 *     <li>Empty list: {@link #empty()}</li>
 *     <li>Non-empty list: {@link #prefix()} ITEM1 {@link #delimiter()} ITEM2 {@link #suffix()}</li>
 * </ul>
 */
public interface MsgListConfiguration {
  MsgListConfiguration INSTANCE = TyRes.create(MsgListConfiguration.class);

  /**
   * Message to use if the list is empty.
   */
  StrRes empty();

  /**
   * Message to use if the list has one single item.
   */
  MsgRes single(Resolvable item);

  /**
   * Prefix for non-empty lists.
   */
  StrRes prefix();

  /**
   * Delimiter for non-empty lists.
   */
  StrRes delimiter();

  /**
   * Suffix for non-empty lists.
   */
  StrRes suffix();
}
