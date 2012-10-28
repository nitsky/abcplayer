package interpreter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import interpreter.Token.TokenType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class LexerTest {
    @Test
    public void testPiece2() {
        
        String input;
        
        try {
            input = Util.readStringFromFile(System.getProperty("user.dir") + "/sample_abc/piece2.abc");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return;
        }
        
        Lexer lexer = new Lexer(input);
        List<Token> expectedResult = new ArrayList<Token>(Arrays.asList(
                new Token(TokenType.HEADER_FIELD, "X: 2"),
        		new Token(TokenType.HEADER_FIELD, "T: Piece No. 2"),
        		new Token(TokenType.HEADER_FIELD, "M: 4/4"),
        		new Token(TokenType.HEADER_FIELD, "L: 1/4"),
        		new Token(TokenType.HEADER_FIELD, "Q: 200 "),
        		new Token(TokenType.HEADER_FIELD, "K: C "),
        		new Token(TokenType.CHORD_BEGIN, "["),
        		new Token(TokenType.NOTE_ACCIDENTAL, "^"),
        		new Token(TokenType.NOTE_NAME, "F"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.NOTE_NAME, "e"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.CHORD_END, "]"),
        		new Token(TokenType.CHORD_BEGIN, "["),
        		new Token(TokenType.NOTE_NAME, "F"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.NOTE_NAME, "e"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.CHORD_END, "]"),
        		new Token(TokenType.NOTE_NAME, "z"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.CHORD_BEGIN, "["),
        		new Token(TokenType.NOTE_NAME, "F"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.NOTE_NAME, "e"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.CHORD_END, "]"),
        		new Token(TokenType.NOTE_NAME, "z"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.CHORD_BEGIN, "["),
        		new Token(TokenType.NOTE_NAME, "F"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.NOTE_NAME, "c"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.CHORD_END, "]"),
        		new Token(TokenType.CHORD_BEGIN, "["),
        		new Token(TokenType.NOTE_NAME, "F"),
        		new Token(TokenType.NOTE_NAME, "e"),
        		new Token(TokenType.CHORD_END, "]"),
        		new Token(TokenType.BAR, "|"),
        		new Token(TokenType.CHORD_BEGIN, "["),
        		new Token(TokenType.NOTE_NAME, "G"),
        		new Token(TokenType.NOTE_NAME, "B"),
        		new Token(TokenType.NOTE_NAME, "g"),
        		new Token(TokenType.CHORD_END, "]"),
        		new Token(TokenType.NOTE_NAME, "z"),
        		new Token(TokenType.NOTE_NAME, "G"),
        		new Token(TokenType.NOTE_NAME, "z"),
        		new Token(TokenType.BAR, "|"),
        		new Token(TokenType.NOTE_NAME, "c"),
        		new Token(TokenType.NOTE_LENGTH, "3/2"),
        		new Token(TokenType.NOTE_NAME, "G"),
        		new Token(TokenType.NOTE_LENGTH, "1/2"),
        		new Token(TokenType.NOTE_NAME, "z"),
        		new Token(TokenType.NOTE_NAME, "E"),
        		new Token(TokenType.BAR, "|"),
        		new Token(TokenType.NOTE_NAME, "E"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.NOTE_NAME, "A"),
        		new Token(TokenType.NOTE_NAME, "B"),
        		new Token(TokenType.NOTE_ACCIDENTAL, "_"),
        		new Token(TokenType.NOTE_NAME, "B"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.NOTE_NAME, "A"),
        		new Token(TokenType.BAR, "|"),
        		new Token(TokenType.TUPLET, "(3"),
        		new Token(TokenType.NOTE_NAME, "G"),
        		new Token(TokenType.NOTE_NAME, "e"),
        		new Token(TokenType.NOTE_NAME, "g"),
        		new Token(TokenType.NOTE_NAME, "a"),
        		new Token(TokenType.NOTE_NAME, "f"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.NOTE_NAME, "g"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.BAR, "|"),
        		new Token(TokenType.NOTE_NAME, "z"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.NOTE_NAME, "e"),
        		new Token(TokenType.NOTE_NAME, "c"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.NOTE_NAME, "d"),
        		new Token(TokenType.NOTE_LENGTH, "/2"),
        		new Token(TokenType.NOTE_NAME, "B"),
        		new Token(TokenType.NOTE_LENGTH, "3/4"),
        		new Token(TokenType.NOTE_NAME, "z"),
        		new Token(TokenType.NOTE_LENGTH, "3/4"),
        		new Token(TokenType.BAR, "||")));
        
        assertTrue(lexer.generateTokens().equals(expectedResult));
    
    }
    @Test
    public void testLexerTestPiece() {
        
        String input;
        
        try {
            input = Util.readStringFromFile(System.getProperty("user.dir") + "/sample_abc/lexer_test_piece.abc");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
            return;
        }
        
        Lexer lexer = new Lexer(input);
        List<Token> expectedResult = new ArrayList<Token>(Arrays.asList(new Token(TokenType.HEADER_FIELD, "X: 777"),
        		new Token(TokenType.HEADER_FIELD, "T: Michael's Favorite Piece"),
        		new Token(TokenType.HEADER_FIELD, "C: Michael Wu"),
        		new Token(TokenType.HEADER_FIELD, "M: 4/4"),
        		new Token(TokenType.HEADER_FIELD, "L: 1/4"),
        		new Token(TokenType.HEADER_FIELD, "Q: 120"),
        		new Token(TokenType.VOICE, "V: 1"),
        		new Token(TokenType.VOICE, "V: 2"),
        		new Token(TokenType.HEADER_FIELD, "K: F"),
        		new Token(TokenType.VOICE, "V: 1"),
        		new Token(TokenType.NOTE_NAME, "z"),
        		new Token(TokenType.NOTE_NAME, "G"),
        		new Token(TokenType.NOTE_NAME, "A"),
        		new Token(TokenType.NOTE_NAME, "c"),
        		new Token(TokenType.NOTE_LENGTH, "/"),
        		new Token(TokenType.NOTE_NAME, "z"),
        		new Token(TokenType.NOTE_LENGTH, "/"),
        		new Token(TokenType.BAR, "|"),
        		new Token(TokenType.FIRST_ENDING, "[1"),
        		new Token(TokenType.NOTE_NAME, "z"),
        		new Token(TokenType.NOTE_LENGTH, "2"),
        		new Token(TokenType.NOTE_ACCIDENTAL, "="),
        		new Token(TokenType.NOTE_NAME, "b"),
        		new Token(TokenType.NOTE_NAME, "e"),
        		new Token(TokenType.BAR_END_REPEAT, ":|"),
        		new Token(TokenType.SECOND_ENDING, "[2"),
        		new Token(TokenType.NOTE_NAME, "g"),
        		new Token(TokenType.NOTE_NAME, "b"),
        		new Token(TokenType.TUPLET, "(3"),
        		new Token(TokenType.NOTE_NAME, "C"),
        		new Token(TokenType.NOTE_NAME, "A"),
        		new Token(TokenType.NOTE_NAME, "B"),
        		new Token(TokenType.BAR, "|"),
        		new Token(TokenType.VOICE, "V: 2"),
        		new Token(TokenType.BAR_BEGIN_REPEAT, "|:"),
        		new Token(TokenType.CHORD_BEGIN, "["),
        		new Token(TokenType.NOTE_NAME, "a"),
        		new Token(TokenType.NOTE_LENGTH, "2"),
        		new Token(TokenType.NOTE_NAME, "b"),
        		new Token(TokenType.NOTE_LENGTH, "2"),
        		new Token(TokenType.NOTE_ACCIDENTAL, "^"),
        		new Token(TokenType.NOTE_NAME, "C"),
        		new Token(TokenType.NOTE_LENGTH, "2"),
        		new Token(TokenType.CHORD_END, "]"),
        		new Token(TokenType.NOTE_NAME, "z"),
        		new Token(TokenType.NOTE_NAME, "f"),
        		new Token(TokenType.BAR_END_REPEAT, ":|"),
        		new Token(TokenType.NOTE_NAME, "E"),
        		new Token(TokenType.NOTE_NAME, "a"),
        		new Token(TokenType.NOTE_OCTAVE_MODIFIER, "'"),
        		new Token(TokenType.NOTE_OCTAVE_MODIFIER, "'"),
        		new Token(TokenType.NOTE_NAME, "A"),
        		new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","),
        		new Token(TokenType.NOTE_NAME, "b"),
        		new Token(TokenType.NOTE_LENGTH, "/4"),
        		new Token(TokenType.NOTE_NAME, "e"),
        		new Token(TokenType.NOTE_LENGTH, "3/4"),
        		new Token(TokenType.BAR, "|"),
        		new Token(TokenType.NOTE_NAME, "D"),
        		new Token(TokenType.NOTE_LENGTH, "4"),
        		new Token(TokenType.BAR, "|]")));
        
        assertTrue(lexer.generateTokens().equals(expectedResult));  
    }
    
}
