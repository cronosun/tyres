package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.experiment.BundleInfo;
import com.github.cronosun.tyres.core.experiment.MethodInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@ThreadSafe
public interface FallbackGenerator {
    String fallbackMsgFor(BundleInfo bundleInfo, MethodInfo methodInfo, @Nullable Locale locale, Object[] args);
}
