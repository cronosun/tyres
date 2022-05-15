package com.github.cronosun.tyres.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BaseNameTest {

  @Test
  public void testWithPackage() {
    var baseName = BaseName.fromPackageAndName("com.company.project", "MyClass");
    assertEquals("com.company.project.MyClass", baseName.value());
    assertEquals("com.company.project", baseName.packageName());
    assertEquals("MyClass", baseName.name());
  }

  @Test
  public void testWithoutPackage() {
    var baseName = BaseName.fromPackageAndName("", "MyClass");
    assertEquals("MyClass", baseName.value());
    assertEquals("", baseName.packageName());
    assertEquals("MyClass", baseName.name());
  }

  @Test
  public void testFromClass() {
    var baseName = BaseName.fromClass(DemoInterfaceForBaseName.class);
    assertEquals(DemoInterfaceForBaseName.class.getName(), baseName.value());
    assertEquals(DemoInterfaceForBaseName.class.getPackageName(), baseName.packageName());
    assertEquals(DemoInterfaceForBaseName.class.getSimpleName(), baseName.name());
  }

  @Test
  public void testWithEmptyName() {
    var baseName = BaseName.fromPackageAndName("", "");
    assertEquals("", baseName.value());
    assertEquals("", baseName.packageName());
    assertEquals("", baseName.name());
  }
}
