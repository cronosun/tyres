package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.MsgRes;
import com.github.cronosun.tyres.core.MsgSource;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class DefaultMsgSource implements MsgSource {
    @Override
    public String message(MsgRes resource, NotFoundStrategy notFoundStrategy, Locale locale) {
        return null;
    }

    @Nullable
    @Override
    public String maybeMessage(MsgRes resource, Locale locale) {
        return null;
    }

    @Override
    public NotFoundStrategy notFoundStrategy() {
        return null;
    }
}
