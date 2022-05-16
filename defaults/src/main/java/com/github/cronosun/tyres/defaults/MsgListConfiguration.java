package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.Msg;
import com.github.cronosun.tyres.core.Res;
import com.github.cronosun.tyres.core.Resolvable;
import com.github.cronosun.tyres.core.TyRes;
import java.util.List;

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
  Res<Msg> empty();

  /**
   * Message to use if the list has one single item.
   */
  Res<Msg> single(Resolvable<? extends Msg> item);

  /**
   * Prefix for non-empty lists.
   */
  Res<Msg> prefix();

  /**
   * Delimiter for non-empty lists.
   */
  Res<Msg> delimiter();

  /**
   * Suffix for non-empty lists.
   */
  Res<Msg> suffix();
}
