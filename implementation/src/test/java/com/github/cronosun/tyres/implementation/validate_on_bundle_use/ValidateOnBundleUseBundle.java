package com.github.cronosun.tyres.implementation.validate_on_bundle_use;

import com.github.cronosun.tyres.core.Fmt;
import com.github.cronosun.tyres.core.Text;

/**
 * This bundle is valid for English, but it's invalid for Italian (missing translation) and German (invalid message format).
 */
public interface ValidateOnBundleUseBundle {
  /**
   * This is valid for all locales (English, German and Italian).
   */
  Text alwaysValid();

  /**
   * This is only valid for English but invalid for German and Italian.
   */
  Fmt myMessage(String argument);
}
