package com.github.cronosun.tyres.core;

import java.lang.reflect.Method;
import org.jetbrains.annotations.Nullable;

@ThreadSafe
public interface ResInfo {
  /**
   * Information about the bundle.
   */
  BundleInfo bundle();

  Method method();

  /**
   * The name. This is either {@link Method#getName()} or the name from the {@link Rename}-annotation (if
   * this annotation is present).
   */
  String name();

  /**
   * The default value from the {@link Default}-annotation or null if there's no such annotation.
   * <p>
   * Note: The default value is not the same as the fallback value. The default value is a "normal" value that
   * is considered to be OK. This default value can be used instead of writing a message bundle for the default
   * locale.
   * <p>
   * Example with default values in the bundle:
   * <pre>
   *     messages.properties    -> values for the default locale
   *     messages_de.properties -> german translation
   * </pre>
   * <p>
   * Instead of doing that, you can use the {@link Default}-annotation instead of <code>messages.properties</code>.
   */
  @Nullable
  String defaultValue();

  /**
   * Returns information about this instance for debugging.
   */
  @Override
  String toString();

  /**
   * Returns information for debugging information: Gives enough information so a developer can identify this
   * resource. This can also be used to generate the fallback message.
   */
  default String debugReference() {
    var baseName = bundle().baseName().value();
    var name = name();
    return "{" + baseName + "::" + name + "}";
  }
}
