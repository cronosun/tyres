package com.github.cronosun.tyres.implementation.single_base_name;

import com.github.cronosun.tyres.core.Bin;
import com.github.cronosun.tyres.core.Default;
import com.github.cronosun.tyres.core.File;
import com.github.cronosun.tyres.core.Text;

public interface SingleBaseNameOkBundle {
  Text message1();

  Text message2();

  @Default("Some text")
  Text message3FromDefault();

  @File("my_file.txt")
  Bin myFile();
}
