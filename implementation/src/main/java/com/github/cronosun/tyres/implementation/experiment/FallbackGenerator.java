package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.experiment.ResInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@ThreadSafe
public interface FallbackGenerator {
    String fallbackMsgFor(ResInfo resInfo, @Nullable Locale locale, Object[] args);
}
