package com.github.cronosun.tyres.core;

import java.lang.reflect.Method;
import java.util.Objects;

@ThreadSafe
public final class ResInfo {

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

  /**
   * Returns information for debugging information: Gives enough information so a developer can identify this
   * resource. This can also be used to generate the fallback message.
   */
  public String debugReference() {
    var baseName = bundle().baseName().value();
    var details = details();
    var kind = details.kind();
    switch (kind) {
      case STRING:
        var stringResouce = details.asStringResouce();
        return "{" + baseName + "::" + stringResouce.name() + "}";
      case FILE:
        var fileResource = details.asFileResource();
        return "{" + baseName + " FILE " + fileResource.filename() + "}";
      default:
        throw new TyResException("Unknown resource kind: " + kind);
    }
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
    return (
      "ResInfo{" + "bundleInfo=" + bundleInfo + ", method=" + method + ", details=" + details + '}'
    );
  }
}
