package interpreter;

/**
 * A token is a lexical item that the parser uses.
 */
public class Token {
  /**
   * All the types of tokens that can be made.
   */
  public static enum TokenType {

    // The entire line of a header, ex. 'X:value'
    HEADER_FIELD,

    // Voice
    VOICE,

    // ex. 'A' 'c' 'G'
    NOTE_NAME,
    // ex.'/' '1/4' '3/4' '/4'
    NOTE_LENGTH,
    // ex. '^' '_' '='
    NOTE_ACCIDENTAL,
    // ex. ''' ','
    NOTE_OCTAVE_MODIFIER,

    // '[1'
    FIRST_ENDING,
    // '[2'
    SECOND_ENDING,

    // ex. '['
    CHORD_BEGIN,
    // ex. ']'
    CHORD_END,

    // ex. '(3' '(2'
    TUPLET,

    // '|', '[|', '|]'
    BAR,
    // '|:'
    BAR_BEGIN_REPEAT,
    // ':|'
    BAR_END_REPEAT,

  }

  private TokenType type;
  private String string;

  public Token(TokenType t, String s) {
    this.type = t;
    this.string = s;
  }

  public TokenType getType() {
    return this.type;
  }

  public String getString() {
    return this.string;
  }

  @Override
  public String toString() {
    return this.type + " " + this.getString();
  }

  @Override
  public boolean equals(Object other){
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof Token))return false;
    Token otherToken = (Token)other;
    return ((this.getType() == otherToken.getType()) && (this.getString().equals(otherToken.getString())));
  }

}
