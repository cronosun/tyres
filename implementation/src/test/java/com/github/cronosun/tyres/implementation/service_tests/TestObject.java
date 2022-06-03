package com.github.cronosun.tyres.implementation.service_tests;

import java.util.Objects;

public final class TestObject {

  private final int value;
  private final String text;

  public TestObject(int value, String text) {
    this.value = value;
    this.text = text;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TestObject that = (TestObject) o;
    return value == that.value && text.equals(that.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, text);
  }

  @Override
  public String toString() {
    return "TestObject{" + "value=" + value + ", text='" + text + '\'' + '}';
  }
}
