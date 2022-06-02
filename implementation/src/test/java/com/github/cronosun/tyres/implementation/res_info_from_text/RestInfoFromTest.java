package com.github.cronosun.tyres.implementation.res_info_from_text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Objects;
import org.junit.jupiter.api.Test;

public class RestInfoFromTest {

  @Test
  public void textInfoCanBeReadFromText() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(RestInfoFromTextBundle.class);

    var resInfo1 = Objects.requireNonNull(bundle.message1().resInfo());
    var resInfo2 = Objects.requireNonNull(bundle.message2().resInfo());
    var resInfo3 = Objects.requireNonNull(bundle.message3("").resInfo());

    assertEquals("message1", resInfo1.name());
    assertEquals("message2", resInfo2.name());
    assertEquals("message3", resInfo3.name());
  }
}
