package com.github.cronosun.tyres.core;

/**
 * Interface for all resources.
 *
 * Contract for implementations of this interface:
 *
 *  - must provide a static method: `TThis create(BundleInfo bundleInfo, ResInfo resInfo)`
 *  - `TThis` must be assignable to the implementation.
 */
public interface Res<TThis> {
  ResInfo info();
  TThis withArgs(Object[] args);
  Object[] args();
}
