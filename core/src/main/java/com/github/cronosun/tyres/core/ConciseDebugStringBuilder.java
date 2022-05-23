package com.github.cronosun.tyres.core;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

final class ConciseDebugStringBuilder {

  public static Object text(String text) {
    return new Text(text);
  }

  public static Object association(Object key, Object value) {
    return new Association(key, value);
  }

  public static String build(Iterable<?> items) {
    return build(items.iterator());
  }

  public static String build(Stream<?> items) {
    return build(items.iterator());
  }

  private static String build(Iterator<?> items) {
    var builder = new ConciseDebugStringBuilder();
    builder.list(items, false);
    return builder.builder.toString();
  }

  private final StringBuilder builder;

  private ConciseDebugStringBuilder() {
    this.builder = new StringBuilder();
  }

  private void list(Iterator<?> items, boolean embedded) {
    if (embedded) {
      builder.append('[');
    } else {
      builder.append('(');
    }
    var needSpace = false;
    while (items.hasNext()) {
      var item = items.next();
      final Object itemToAdd;
      if (item instanceof Optional) {
        var cast = (Optional<?>) item;
        if (cast.isEmpty()) {
          continue;
        } else {
          itemToAdd = cast.get();
        }
      } else {
        itemToAdd = item;
      }

      if (needSpace) {
        if (embedded) {
          builder.append(", ");
        } else {
          builder.append(' ');
        }
      }
      any(itemToAdd);
      needSpace = true;
    }
    if (embedded) {
      builder.append(']');
    } else {
      builder.append(')');
    }
  }

  private void any(Object object) {
    if (object == null) {
      builder.append("null");
    } else if (object instanceof WithConciseDebugString) {
      var cast = (WithConciseDebugString) object;
      builder.append(cast.conciseDebugString());
    } else if (object instanceof Text) {
      var cast = (Text) object;
      addText(cast.text);
    } else if (object instanceof Iterable) {
      var cast = (Iterable<?>) object;
      list(cast.iterator(), true);
    } else if (object instanceof Object[]) {
      var cast = (Object[]) object;
      list(Arrays.stream(cast).iterator(), true);
    } else if (object instanceof Stream) {
      var cast = (Stream<?>) object;
      list(cast.iterator(), true);
    } else if (object instanceof String) {
      var cast = (String) object;
      textOrString(cast);
    } else if (object instanceof Association) {
      var cast = (Association) object;
      addAssociation(cast);
    } else {
      // fallback
      any(object.toString());
    }
  }

  private void addAssociation(Association association) {
    builder.append('{');
    any(association.key);
    builder.append("->");
    any(association.value);
    builder.append('}');
  }

  private void textOrString(String string) {
    if (string.contains(" ")) {
      addText(string);
    } else {
      builder.append(string);
    }
  }

  private void addText(String text) {
    builder.append('\'');
    builder.append(text);
    builder.append('\'');
  }

  private static final class Text {

    final String text;

    public Text(String text) {
      this.text = text;
    }
  }

  private static final class Association {

    final Object key;
    final Object value;

    private Association(Object key, Object value) {
      this.key = key;
      this.value = value;
    }
  }
}
