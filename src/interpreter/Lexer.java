package interpreter;

import interpreter.Token.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.regex.*;

/**
* A lexer takes a string and splits it into tokens that are meaningful to a
* parser.
*/
public class Lexer {

  private String string;

  /**
  * Creates the lexer over the passed string.
  *
  * @param string
  *            The string to tokenize.
  */
  public Lexer(String string) {
    this.string = string;
  }

  /**
  * generateTokens
  * Generates the list of Tokens from the string passed in the
  * constructor. Throws an IllegalArgumentException if the input
  * has illegal characters in it.
  * @return The list of tokens extracted from the expression string
  */
  public List<Token> generateTokens() {

    List<Token> result = new ArrayList<Token>();

    // start by stripping away comment line. in abc, comments begin with the '%' symbol
    String input = this.string.replaceAll("%.*", "");

    // Generate a map of patterns, matching the token type to the pattern
    // that will detect it in the expression string
    Map<TokenType, Pattern> patternMap = new LinkedHashMap<TokenType, Pattern>();

    patternMap.put(TokenType.HEADER_FIELD, Pattern.compile("[CKLMQTX]:.*"));
    patternMap.put(TokenType.VOICE, Pattern.compile("V:.*"));
    patternMap.put(TokenType.TUPLET, Pattern.compile("\\([234]"));
    patternMap.put(TokenType.NOTE_ACCIDENTAL, Pattern.compile("\\^+|\\_+|="));
    patternMap.put(TokenType.NOTE_OCTAVE_MODIFIER, Pattern.compile("\\'|,"));
    patternMap.put(TokenType.NOTE_NAME, Pattern.compile("[zA-Ga-g]"));
    patternMap.put(TokenType.NOTE_LENGTH, Pattern.compile("[1-9]*/[1-9]*|[1-9]+"));
    patternMap.put(TokenType.BAR_BEGIN_REPEAT, Pattern.compile("\\|\\:"));
    patternMap.put(TokenType.BAR_END_REPEAT, Pattern.compile("\\:\\|"));
    patternMap.put(TokenType.BAR, Pattern.compile("\\[\\||\\|\\]|\\|\\||\\|"));
    patternMap.put(TokenType.FIRST_ENDING, Pattern.compile("\\[1"));
    patternMap.put(TokenType.SECOND_ENDING, Pattern.compile("\\[2"));
    patternMap.put(TokenType.CHORD_BEGIN, Pattern.compile("\\["));
    patternMap.put(TokenType.CHORD_END, Pattern.compile("\\]"));

    // Combine all the patterns into one so that we can move through the input
    // by matched expressions
    StringBuilder masterPatternStringBuilder = new StringBuilder();
    Iterator<TokenType> iterator = patternMap.keySet().iterator();
    while (iterator.hasNext()) {
      TokenType tokenType = iterator.next();
      masterPatternStringBuilder.append('(');
      masterPatternStringBuilder.append(patternMap.get(tokenType).pattern());
      masterPatternStringBuilder.append(')');
      if (iterator.hasNext()) {
        masterPatternStringBuilder.append('|');
      }
    }

    String masterPatternString = masterPatternStringBuilder.toString();

    /* Verify that there are no invalid characters by removing all matchable
    * characters in the input and verifying that the result is the empty string.
    */
    String leftoverString = input.replaceAll("[ \t]","").replaceAll(masterPatternString, "").replaceAll("\r\n|\n","");
    if (!leftoverString.equals("")) {
      throw new IllegalArgumentException("Invalid Characters Detected In Input: " + leftoverString);
    }

    Pattern masterPattern = Pattern.compile(masterPatternString);
    Matcher masterMatcher = masterPattern.matcher(input);

    /* For each token matched, determine its type by running it
    * through each pattern until the matching one is found, and
    * add it to the result
    */
    while (masterMatcher.find()) {
      String string = masterMatcher.group();
      for (TokenType tokenType : patternMap.keySet()) {
        if (patternMap.get(tokenType).matcher(string).matches() == true) {
          Token newToken = new Token(tokenType, string);
          result.add(newToken);
          break;
        }
      }
    }

    return result;
  }

}
