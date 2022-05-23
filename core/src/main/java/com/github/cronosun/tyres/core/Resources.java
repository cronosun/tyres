package com.github.cronosun.tyres.core;

import java.io.InputStream;
import java.util.Locale;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface Resources {
  Messages msg();

  Strings str();

  Binaries bin();

  Resolver resolver();

  Common common();

  /**
   * Resources functionality that cannot be clearly assigned to one of the resouce types (messages,
   * strings, binaries).
   */
  interface Common {
    /**
     * Validates the given bundle. Throws {@link TyResException} if the bundle is not correct / has validation errors.
     * This method should only be used in unit-tests.
     * <p>
     * Implementation notes: The behaviour of this method is highly implementation specific. For production code,
     * this implementation might even be a no-op. If it's implemented, it should do about this:
     * <p>
     * - Assert that all declared resources are found for the given locales (except the resource is marked as
     * optional, see {@link Validation}).
     * - If the resource is a {@link MsgRes}, also asserts that the message pattern is valid and the number of
     * arguments match with the pattern.
     * - If the validator supports it, also asserts that there are no unused (superfluous) resources.
     */
    void validate(Object bundle, Set<Locale> locales);

    /**
     * What should be done if the resource cannot be found.
     *
     * Used for example in {@link Messages#get(MsgRes, Locale)}.
     */
    MsgNotFoundStrategy notFoundStrategy();

    /**
     * Generates the fallback message (note, this is not to be confused with the default message,
     * see {@link ResInfo.Str#defaultValue()}).
     */
    String fallbackFor(ResInfo resInfo, Object[] args);
    // TODO: in "ResourcesCommon" -> also add something like @Nullable currentLocale (can be null)
  }

  interface Resolver {
    /**
     * Calls {@link Resolvable#maybe(Resources, Locale)}}.
     */
    @Nullable
    String maybe(Resolvable message, Locale locale);

    /**
     * Calls {@link Resolvable#get(Resources, MsgNotFoundStrategy, Locale)}}.
     */
    String get(Resolvable message, MsgNotFoundStrategy notFoundStrategy, Locale locale);

    /**
     * Calls {@link Resolvable#get(Resources, MsgNotFoundStrategy, Locale)}} with {@link Resources.Common#notFoundStrategy()}.
     */
    String get(Resolvable message, Locale locale);
  }

  /**
   * Message resources.
   * <p>
   * Note: Message resources internally are based on {@link Strings}: This means that if you get a string from
   * {@link Strings} with a {@link ResInfo} that's a {@link ResInfo} from a message, you'll get a result too
   * (the message pattern).
   */
  interface Messages {
    /**
     * Returns the message.
     * <p>
     * If there's no such resource: Depending on {@link MsgNotFoundStrategy}, either returns the fallback message
     * or throws {@link TyResException}.
     */
    String get(MsgRes resource, MsgNotFoundStrategy notFoundStrategy, Locale locale);

    /**
     * Calls {@link #get(MsgRes, MsgNotFoundStrategy, Locale)} with {@link Resources.Common#notFoundStrategy()}.
     */
    String get(MsgRes resource, Locale locale);

    /**
     * Returns the message (if the resource can be found) or <code>null</code> if there's no such message.
     * <p>
     * Note: Expect the implementation to throw {@link TyResException} if something is wrong (such as an invalid
     * message format or invalid arguments).
     */
    @Nullable
    String maybe(MsgRes resource, Locale locale);
  }

  interface Strings {
    /**
     * Returns the string from the resources if found. Returns <code>null</code> if given string resource cannot
     * be found.
     */
    @Nullable
    String maybe(StrRes resource, Locale locale);

    /**
     * Calls {@link #get(StrRes, MsgNotFoundStrategy, Locale)} with {@link Resources.Common#notFoundStrategy()}.
     */
    String get(StrRes resource, Locale locale);

    /**
     * Returns the string from the resources, if found. If not found, either throws {@link TyResException} or
     * returns a fallback string, depending on {@link MsgNotFoundStrategy}.
     */
    String get(StrRes resource, MsgNotFoundStrategy notFoundStrategy, Locale locale);
  }

  interface Binaries {
    /**
     * Returns the binary as input stream or <code>null</code> if resource cannot be found.
     */
    @Nullable
    InputStream maybe(BinRes resource, Locale locale);

    InputStream get(BinRes resource, Locale locale);
  }
}
