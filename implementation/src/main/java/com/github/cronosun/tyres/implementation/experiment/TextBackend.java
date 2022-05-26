package com.github.cronosun.tyres.implementation.experiment;

import com.github.cronosun.tyres.core.ThreadSafe;
import com.github.cronosun.tyres.core.experiment.BundleInfo;
import com.github.cronosun.tyres.core.experiment.MethodInfo;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@ThreadSafe
public interface TextBackend {
    @Nullable
    String maybeFmt(BundleInfo bundleInfo, MethodInfo methodInfo, Object[] args, Locale locale);
    @Nullable
    String maybeText(BundleInfo bundleInfo, MethodInfo methodInfo, Locale locale);
}
