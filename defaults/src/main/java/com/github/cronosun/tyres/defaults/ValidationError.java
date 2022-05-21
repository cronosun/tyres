package com.github.cronosun.tyres.defaults;

import com.github.cronosun.tyres.core.BundleInfo;
import com.github.cronosun.tyres.core.ResInfo;
import java.util.Objects;

public abstract class ValidationError {

  private ValidationError() {}

  public final class ResourceNotFound extends ValidationError {

    private final ResInfo resInfo;

    public ResourceNotFound(ResInfo resInfo) {
      this.resInfo = resInfo;
    }

    @Override
    public String toString() {
      return "ResourceNotFound{" + "resInfo=" + resInfo + '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ResourceNotFound that = (ResourceNotFound) o;
      return resInfo.equals(that.resInfo);
    }

    @Override
    public int hashCode() {
      return Objects.hash(resInfo);
    }
  }

  public final class SuperfluousResource extends ValidationError {

    private final BundleInfo bundle;
    private final String name;

    public SuperfluousResource(BundleInfo bundle, String name) {
      this.bundle = bundle;
      this.name = name;
    }

    @Override
    public String toString() {
      return "SuperfluousResource{" + "bundle=" + bundle + ", name='" + name + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      SuperfluousResource that = (SuperfluousResource) o;
      return bundle.equals(that.bundle) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(bundle, name);
    }
  }
}
