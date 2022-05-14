package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.BaseName;

public interface ResourceBundleProvider {
    /**
     * Returns the bundle. Never returns <code>null</code>: If there's no such bundle, returns an empty bundle.
     */
    TyResResourceBundle getBundle(BaseName baseName);
}
