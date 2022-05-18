package com.github.cronosun.tyres.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FilenameTest {

  @Test
  void testRegularFilename() {
    var filename = Filename.from("some_file.pdf");
    assertEquals("some_file.pdf", filename.value());
    assertEquals("some_file", filename.base());
    assertEquals("pdf", filename.extension());
  }

  @Test
  void testFileWithoutExtension() {
    var filename = Filename.from("has_no_extension");
    assertEquals("has_no_extension", filename.value());
    assertEquals("has_no_extension", filename.base());
    assertNull(filename.extension());
  }

  @Test
  void testEmptyFile() {
    var filename = Filename.from("");
    assertEquals("", filename.value());
    assertEquals("", filename.base());
    assertNull(filename.extension());
  }

  @Test
  void testMultipleDotsExtensionIsTheLast() {
    var filename = Filename.from("this.is.some.file.txt");
    assertEquals("this.is.some.file.txt", filename.value());
    assertEquals("this.is.some.file", filename.base());
    assertEquals("txt", filename.extension());
  }
}
