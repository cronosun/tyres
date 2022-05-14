package com.github.cronosun.tyres.core;

import java.util.Locale;

import org.jetbrains.annotations.Nullable;

@Deprecated
public interface MsgSource_OLD {
    /**
     * Returns the message.
     * 
     * Never returns null. If the resource cannot be found but has a default value (see 
     * {@link Default}), the default value is taken. If it does not have a default value, the
     * system returns a fallback message instead. 
     */
    String getMessage(MsgRes resource, @Nullable Object[] args, Locale locale);
   
    /**
     * Returns the message.
     * 
     * Never returns null. If the resource cannot be found, the given default message is used
     * instead.
     */
    String getMessage(MsgRes resource, @Nullable Object[] args, String defaultMessage, Locale locale);
    
    /**
     * Returns the message.
     * 
     * If the resource cannot be found, uses the default value (see {@link Default}) - if this
     * is missing too, returns `null`.
     */
    @Nullable
    String tryGetMessage(MsgRes resource, @Nullable Object[] args, Locale locale);

    default String getMessageOrThrow(MsgRes resource, @Nullable Object[] args, Locale locale) {
        var maybeMessage = tryGetMessage(resource, args, locale);
        if (maybeMessage ==null) {
            var info = resource.info();
            throw new TyResException("Message not found: " + info);
        }
        return maybeMessage;
    }
}
