package com.github.cronosun.tyres.defaults.no_args_parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.cronosun.tyres.core.MsgNotFoundStrategy;
import com.github.cronosun.tyres.defaults.Implementation;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class NoArgsParsingTest {

  @Test
  void msgResAlwaysUsesMsgFormatWhileStrResNeverUsesMsgFormat() {
    var resources = Implementation.newImplementation(MsgNotFoundStrategy.THROW);

    // TODO: oder doch nicht so?

    // note: Both messages are identical in the .properties file. But one (the MsgRes) uses
    // the MessageFormat - parsing rules apply - while the other (StrRes) does not.
    assertEquals(
      "The  character must be escaped in messages if you want this character.",
      resources.resolver().get(NoArgsParsingBundle.INSTANCE.msgRes(), Locale.ROOT)
    );
    assertEquals(
      "The ' character must be escaped in messages if you want this character.",
      resources.resolver().get(NoArgsParsingBundle.INSTANCE.strRes(), Locale.ROOT)
    );
  }
}
