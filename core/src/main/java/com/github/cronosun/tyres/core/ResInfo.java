package com.github.cronosun.tyres.core;

import java.lang.reflect.Method;
import java.util.Objects;

@ThreadSafe
public final class ResInfo implements WithConciseDebugString {

  private final BundleInfo bundleInfo;
  private final Method method;
  private final ResInfoDetails details;

  public ResInfo(BundleInfo bundleInfo, Method method, ResInfoDetails details) {
    this.bundleInfo = Objects.requireNonNull(bundleInfo);
    this.method = Objects.requireNonNull(method);
    this.details = Objects.requireNonNull(details);
  }

  /**
   * Information about the bundle.
   */
  public BundleInfo bundle() {
    return bundleInfo;
  }

  /**
   * The method that has been used to get those {@link ResInfo} from.
   */
  public Method method() {
    return method;
  }

  public ResInfoDetails details() {
    return details;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResInfo resInfo = (ResInfo) o;
    return (
      bundleInfo.equals(resInfo.bundleInfo) &&
      method.equals(resInfo.method) &&
      details.equals(resInfo.details)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(bundleInfo, method, details);
  }

  @Override
  public String toString() {
    return ("ResInfo{" + details + ", " + method + ", " + bundleInfo + '}');
  }

  @Override
  public String conciseDebugString() {
    var baseName = bundle().baseName();
    var details = details();
    return ConciseDebugString
      .create()
      .start()
      .append(baseName)
      .child()
      .append(details)
      .end()
      .finish();
  }
}
