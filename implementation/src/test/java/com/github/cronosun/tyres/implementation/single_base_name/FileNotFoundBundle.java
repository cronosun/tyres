package com.github.cronosun.tyres.implementation.single_base_name;

import com.github.cronosun.tyres.core.Bin;
import com.github.cronosun.tyres.core.File;

public interface FileNotFoundBundle {
  @File("file_not_found.txt")
  Bin thisFileDoesNotExist();
}
