package lyc.compiler.model;

import java.io.Serial;

public class InvalidCommentException extends CompilerException {

  @Serial
  private static final long serialVersionUID = -5521093847261004412L;

  public InvalidCommentException(String message) {
    super(message);
  }
}
