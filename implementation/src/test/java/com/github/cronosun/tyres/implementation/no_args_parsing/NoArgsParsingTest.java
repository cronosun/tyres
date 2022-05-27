package com.github.cronosun.tyres.implementation.no_args_parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.DefaultNotFoundConfig;
import com.github.cronosun.tyres.implementation.TestUtil;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class NoArgsParsingTest {

  @Test
  void msgResAlwaysUsesMsgFormatWhileStrResNeverUsesMsgFormat() {
    var resources = TestUtil.newInstance(DefaultNotFoundConfig.THROW);
    var bundle = resources.get(NoArgsParsingBundle.class);

    // note: Both messages are identical in the .properties file. But one (the Fmt) uses
    // the MessageFormat - parsing rules apply - while the other (Text) does not.
    assertEquals(
      "The  character must be escaped in messages if you want this character.",
      bundle.msgRes().get(Locale.ROOT)
    );
    assertEquals(
      "The ' character must be escaped in messages if you want this character.",
      bundle.strRes().get(Locale.ROOT)
    );
  }
}
