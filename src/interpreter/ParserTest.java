package interpreter;

import interpreter.Token.TokenType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * The tests are dependent entirely on the specs, which work "incrementally" - as in,
 * the parsing task is broken into smaller subtasks, which are assumed to give good outputs
 * to the larger tasks given good inputs. In this way, we can test submethods and
 * avoid having to test all the cases for the submethods in the larger methods as well.
 * We just have to parse new possible inputs not accounted for in the submethods. This allows
 * us to partition the inputs to test in the following way:
 * 1. Test ParseNote
 *      - Correct ordering of note modifiers
 *      - Existence of note name
 *      - Different numbers of note modifiers
 *      - Existence of different note modifiers
 * 2. Test ParseChord
 *      - Parse chord with 1 note
 *      - Parse chord with multiple notes
 * 3. Test ParseMeasure
 *      - Parse measure with all notes
 *      - Parse measure with all chords
 *      - Parse measure with all tuplets
 *      - Parse measure with mix of notes and chords
 *      - Parse measure with mix of notes and tuplets
 *      - Parse measure with mix of chords and tuplets
 *      - Parse measure with mix of chords, notes, and tuplets
 *      - Parse measure with chords missing brackets
 * 4. Test ParseHeaders
 *      - Parse piece with all subset headers
 *      - Parse piece with some subset headers
 *      - Parse piece with missing required header
 * 5. Test MergeVoices
 *      - Merge voices for piece with one voice
 *      - Merge voices for piece with two voices
 *      - Merge voices for piece with two voices, split lines
 *      - Merge voices for piece with undeclared voice
 *      - Merge voices for piece without declared voice
 * 6. Test ExpandRepeats
 *      - Expand repeats for piece with no repeats
 *      - Expand repeats for piece with one repeat, one voice, one ending
 *      - Expand repeats for piece with one repeat, one voice, two endings
 *      - Expand repeats for piece with one repeat, two voices, one ending
 *      - Expand repeats for piece with one repeat, two voices, two endings
 *      - Expand repeats for piece with two repeats, one voice, one ending
 *      - Expand repeats for piece with two repeats, one voice, two endings
 *      - Expand repeats for piece with two repeats, two voices, one ending
 *      - Expand repeats for piece with two repeats, two voices, two endings
 *      - Expand repeats for piece with unmatched repeat
 * 7. Test ParsePiece
 *      - Parse piece with one voice, no repeats
 *      - Parse piece with mult voices, no repeats
 *      - Parse piece with one voice, mult repeats
 *      - Parse piece with mult voies, mult repeats
 *      - Parse piece with one voice, one repeat
 *      - Parse piece with mult voices, one repeat
 */


public class ParserTest {

    // test to confirm parseNote can correctly parse a note name
    @Test
    public void testParseNoteName() {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_NAME, "A")
              ));
        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));
        Note result = parser.parseNote(tokens, accidentals, new Meter(1,1));
        Note expectedResult = new Note('A',0,0, new Meter(1,1));
        assertEquals(expectedAccidentals, accidentals);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testParseNoteLength() {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_NAME, "A"), new Token(TokenType.NOTE_LENGTH, "2/3")
              ));
        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));
        Note result = parser.parseNote(tokens, accidentals, new Meter(1,1));
        Note expectedResult = new Note('A',0,0, new Meter(2,3));
        assertEquals(expectedAccidentals, accidentals);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testParseNoteOctave() {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_NAME, "A"), new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","),
              new Token(TokenType.NOTE_OCTAVE_MODIFIER, ",")
              ));
        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));
        Note result = parser.parseNote(tokens, accidentals, new Meter(1,1));
        Note expectedResult = new Note('A',-2,0, new Meter(1,1));
        assertEquals(expectedAccidentals, accidentals);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testParseNoteAccidental() {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        expectedAccidentals.put("A1/1", 1);
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_ACCIDENTAL, "^"),
              new Token(TokenType.NOTE_NAME, "A")
              ));
        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));
        Note result = parser.parseNote(tokens, accidentals, new Meter(1,1));
        Note expectedResult = new Note('A',0,1, new Meter(1,1));
        assertEquals(expectedAccidentals, accidentals);
        assertEquals(expectedResult, result);
    }

    // test a few combinations to make sure all the components work together in concert
    @Test
    public void testParseNoteCombos() {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_NAME, "A"), new Token(TokenType.NOTE_LENGTH, "/2")
              ));
        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));
        Note result = parser.parseNote(tokens, accidentals, new Meter(1,1));
        Note expectedResult = new Note('A',0,0, new Meter(1,2));
        assertEquals(expectedAccidentals, accidentals);
        assertEquals(expectedResult, result);
    }

    //Test Parse Chord

    @Test
    public void testParseChordFullSpecification()
    {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_ACCIDENTAL, "_"), new Token(TokenType.NOTE_NAME, "A"),
              new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","), new Token(TokenType.NOTE_LENGTH, "/2"),
              new Token(TokenType.NOTE_ACCIDENTAL, "^"), new Token(TokenType.NOTE_NAME, "c"),
              new Token(TokenType.NOTE_OCTAVE_MODIFIER, "\'"), new Token(TokenType.NOTE_LENGTH, "2/3")
              ));
        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));

        Chord result = parser.parseChord(tokens, accidentals, new Meter(1, 1));

        Note expected1 = new Note('A', -1, -1, new Meter(1, 2));
        Note expected2 = new Note('C', 2, 1, new Meter(2, 3));
        Chord expectedResult = new Chord();
        expectedResult.addNote(expected1);
        expectedResult.addNote(expected2);

        expectedAccidentals.put("A1/2", -1);
        expectedAccidentals.put("C2/3", 1);

        assertEquals(expectedResult, result);
    }

    @Test
    public void testParseChordNoNoteLength()
    {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_ACCIDENTAL, "_"), new Token(TokenType.NOTE_NAME, "A"),
              new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","), new Token(TokenType.NOTE_ACCIDENTAL, "^"),
              new Token(TokenType.NOTE_NAME, "c"), new Token(TokenType.NOTE_OCTAVE_MODIFIER, "\'"),
              new Token(TokenType.NOTE_LENGTH, "2/3")
              ));

        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));

        Chord result = parser.parseChord(tokens, accidentals, new Meter(1, 1));

        Note expected1 = new Note('A', -1, -1, new Meter(1, 1));
        Note expected2 = new Note('C', 2, 1, new Meter(2, 3));
        Chord expectedResult = new Chord();
        expectedResult.addNote(expected1);
        expectedResult.addNote(expected2);

        expectedAccidentals.put("A1/1", -1);
        expectedAccidentals.put("C2/3", 1);

        assertEquals(expectedResult, result);
    }

    @Test
    public void testParseChordNoAccidental()
    {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();

        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_ACCIDENTAL, "_"), new Token(TokenType.NOTE_NAME, "A"),
              new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","), new Token(TokenType.NOTE_NAME, "c"),
              new Token(TokenType.NOTE_OCTAVE_MODIFIER, "\'"), new Token(TokenType.NOTE_LENGTH, "2/3")
              ));

        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));

        Chord result = parser.parseChord(tokens, accidentals, new Meter(1, 1));

        Note expected1 = new Note('A', -1, -1, new Meter(1, 1));
        Note expected2 = new Note('C', 2, 0, new Meter(2, 3));
        Chord expectedResult = new Chord();
        expectedResult.addNote(expected1);
        expectedResult.addNote(expected2);

        expectedAccidentals.put("A1/1", -1);

        assertEquals(expectedResult, result);
    }

    @Test
    public void testParseChordNoOctaveModifier()
    {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_ACCIDENTAL, "_"), new Token(TokenType.NOTE_NAME, "A"),
              new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","), new Token(TokenType.NOTE_ACCIDENTAL, "^"),
              new Token(TokenType.NOTE_NAME, "c"), new Token(TokenType.NOTE_LENGTH, "2/3")
              ));

        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));

        Chord result = parser.parseChord(tokens, accidentals, new Meter(1, 1));

        Note expected1 = new Note('A', -1, -1, new Meter(1, 1));
        Note expected2 = new Note('C', 1, 1, new Meter(2, 3));
        Chord expectedResult = new Chord();
        expectedResult.addNote(expected1);
        expectedResult.addNote(expected2);

        expectedAccidentals.put("A1/1", -1);
        expectedAccidentals.put("C2/3", 1);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testParseChordOnlyNoteName()
    {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_NAME, "A"), new Token(TokenType.NOTE_ACCIDENTAL, "^"),
              new Token(TokenType.NOTE_NAME, "c"), new Token(TokenType.NOTE_OCTAVE_MODIFIER, "\'"),
              new Token(TokenType.NOTE_LENGTH, "2/3")
              ));

        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));

        Chord result = parser.parseChord(tokens, accidentals, new Meter(1, 1));

        Note expected1 = new Note('A', 0, 0, new Meter(1, 1));
        Note expected2 = new Note('C', 2, 1, new Meter(2, 3));
        Chord expectedResult = new Chord();
        expectedResult.addNote(expected1);
        expectedResult.addNote(expected2);

        expectedAccidentals.put("c'1/1", 1);
        assertEquals(expectedAccidentals, accidentals);
        assertEquals(expectedResult, result);
    }

    @Test
    public void testParseChordNoAccidentalNoOctaveModifier()
    {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_NAME, "A"), new Token(TokenType.NOTE_LENGTH, "/2"),
              new Token(TokenType.NOTE_ACCIDENTAL, "^"), new Token(TokenType.NOTE_NAME, "c"),
              new Token(TokenType.NOTE_OCTAVE_MODIFIER, "\'"), new Token(TokenType.NOTE_LENGTH, "2/3")
              ));

        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));

        Chord result = parser.parseChord(tokens, accidentals, new Meter(1, 1));

        Note expected1 = new Note('A', 0, 0, new Meter(1, 2));
        Note expected2 = new Note('C', 2, 1, new Meter(2, 3));
        Chord expectedResult = new Chord();
        expectedResult.addNote(expected1);
        expectedResult.addNote(expected2);

        expectedAccidentals.put("c'1/1",1);
        assertEquals(expectedAccidentals, accidentals);
        assertEquals(expectedResult, result);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseChordMissingNoteName()
    {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_ACCIDENTAL, "_"), new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","),
              new Token(TokenType.NOTE_ACCIDENTAL, "^"), new Token(TokenType.NOTE_NAME, "c"),
              new Token(TokenType.NOTE_OCTAVE_MODIFIER, "\'"), new Token(TokenType.NOTE_LENGTH, "2/3")
              ));

        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));

        Chord result = parser.parseChord(tokens, accidentals, new Meter(1, 1));

        Note expected1 = new Note('A', -1, -1, new Meter(1, 1));
        Note expected2 = new Note('C', 2, 1, new Meter(2, 3));
        Chord expectedResult = new Chord();
        expectedResult.addNote(expected1);
        expectedResult.addNote(expected2);

        expectedAccidentals.put("A1/1", -1);
        expectedAccidentals.put("C2/3", 1);
        assertEquals(expectedResult, result);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseChordBadNoteOrdering()
    {
      Map<String, Integer> accidentals = new HashMap<String, Integer>();
        Map<String, Integer> expectedAccidentals =  new HashMap<String, Integer>();
        List<Token> tokens = new ArrayList<Token>(Arrays.asList(
              new Token(TokenType.NOTE_NAME, "A"), new Token(TokenType.NOTE_ACCIDENTAL, "_"),
              new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","), new Token(TokenType.NOTE_ACCIDENTAL, "^"),
              new Token(TokenType.NOTE_NAME, "c"), new Token(TokenType.NOTE_OCTAVE_MODIFIER, "\'"),
              new Token(TokenType.NOTE_LENGTH, "2/3")
              ));

        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));

        Chord result = parser.parseChord(tokens, accidentals, new Meter(1, 1));

        Note expected1 = new Note('A', -1, -1, new Meter(1, 1));
        Note expected2 = new Note('C', 2, 1, new Meter(2, 3));
        Chord expectedResult = new Chord();
        expectedResult.addNote(expected1);
        expectedResult.addNote(expected2);

        expectedAccidentals.put("A1/1", -1);
        expectedAccidentals.put("C2/3", 1);
        assertEquals(expectedAccidentals, accidentals);
        assertEquals(expectedResult, result);
    }

    //Start Parse Measure Tests

    @Test
    public void testParseMeasureFullSpec()
    {
      List<Token> tokens = new ArrayList<Token>(Arrays.asList(
            new Token(TokenType.NOTE_ACCIDENTAL, "^^"), new Token(TokenType.NOTE_NAME, "A"),
            new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","), new Token(TokenType.NOTE_LENGTH, "3/4"),
            new Token(TokenType.CHORD_BEGIN, "["), new Token(TokenType.NOTE_ACCIDENTAL, "_"),
            new Token(TokenType.NOTE_NAME, "A"), new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","),
            new Token(TokenType.NOTE_LENGTH, "5/4"), new Token(TokenType.NOTE_ACCIDENTAL, "_"),
            new Token(TokenType.NOTE_NAME, "B"), new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","),
            new Token(TokenType.NOTE_LENGTH, "5/4"), new Token(TokenType.NOTE_ACCIDENTAL, "_"),
            new Token(TokenType.NOTE_NAME, "E"), new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","),
            new Token(TokenType.NOTE_LENGTH, "5/4"), new Token(TokenType.CHORD_END, "]"),
            new Token(TokenType.TUPLET, "(3"), new Token(TokenType.NOTE_ACCIDENTAL, "_"),
            new Token(TokenType.NOTE_NAME, "A"), new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","),
            new Token(TokenType.NOTE_ACCIDENTAL, "_"), new Token(TokenType.NOTE_NAME, "B"),
            new Token(TokenType.NOTE_OCTAVE_MODIFIER, ","), new Token(TokenType.NOTE_ACCIDENTAL, "_"),
            new Token(TokenType.NOTE_NAME, "C"), new Token(TokenType.NOTE_OCTAVE_MODIFIER, ",")
            ));


        Parser parser = new Parser(new Lexer(""));
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Measure expected = new Measure();
        Chord measureChord = new Chord();
        Chord noteChord = new Chord();
        Chord tupletChord1 = new Chord();
        Chord tupletChord2 = new Chord();
        Chord tupletChord3 = new Chord();

        Note measureNote = new Note('A', -1, 2, new Meter(3,4));
        noteChord.addNote(measureNote);
        expected.addChord(noteChord);

        Note chordNote1 = new Note('A', -1, -1, new Meter(5,4));
        Note chordNote2 = new Note('B', -1, -1, new Meter(5,4));
        Note chordNote3 = new Note('E', -1, -1, new Meter(5,4));
        measureChord.addNote(chordNote1);
        measureChord.addNote(chordNote2);
        measureChord.addNote(chordNote3);
        expected.addChord(measureChord);

        Note tupletNote1 = new Note('A', -1, -1, new Meter(2,3));
        Note tupletNote2 = new Note('B', -1, -1, new Meter(2,3));
        Note tupletNote3 = new Note('C', -1, -1, new Meter(2,3));
        tupletChord1.addNote(tupletNote1);
        tupletChord2.addNote(tupletNote2);
        tupletChord3.addNote(tupletNote3);
        expected.addChord(tupletChord1);
        expected.addChord(tupletChord2);
        expected.addChord(tupletChord3);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureAllChords()
    {
      Lexer lexer = new Lexer("[^A,2^B,2^C,2][_E,2_D,2_C,2]");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', -1, 1, new Meter(2, 1));
        chord1.addNote(note11);
        chord1.addNote(note12);
        chord1.addNote(note13);

        Chord chord2 = new Chord();
        Note note21 = new Note('E', -1, -1, new Meter(2, 1));
        Note note22 = new Note('D', -1, -1, new Meter(2, 1));
        Note note23 = new Note('C', -1, -1, new Meter(2, 1));
        chord2.addNote(note21);
        chord2.addNote(note22);
        chord2.addNote(note23);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureAllNotes()
    {
      Lexer lexer = new Lexer("^A,2^B,2");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Chord chord2 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        chord1.addNote(note11);
        chord2.addNote(note12);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureAllTuplets()
    {
      Lexer lexer = new Lexer("(3^A,^B,^C,(3^D,^E,^F,");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord(new Note('A', -1, 1, new Meter(2, 3)));
        Chord chord2 = new Chord(new Note('B', -1, 1, new Meter(2, 3)));
        Chord chord3 = new Chord(new Note('C', -1, 1, new Meter(2, 3)));

        Chord chord4 = new Chord(new Note('D', -1, 1, new Meter(2, 3)));
        Chord chord5 = new Chord(new Note('E', -1, 1, new Meter(2, 3)));
        Chord chord6 = new Chord(new Note('F', -1, 1, new Meter(2, 3)));

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);
        expected.addChord(chord4);
        expected.addChord(chord5);
        expected.addChord(chord6);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureNoChords()
    {
      Lexer lexer = new Lexer("^A,2(3^D,^E,^F,");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Chord chord2 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        chord1.addNote(note11);

        Chord chord3 = new Chord(new Note('D', -1, 1, new Meter(2, 3)));
        Chord chord4 = new Chord(new Note('E', -1, 1, new Meter(2, 3)));
        Chord chord5 = new Chord(new Note('F', -1, 1, new Meter(2, 3)));

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);
        expected.addChord(chord4);
        expected.addChord(chord5);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureNoNotes()
    {
      Lexer lexer = new Lexer("[^A,2^B,2^C,2](3^D,^E,^F,");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', -1, 1, new Meter(2, 1));
        chord1.addNote(note11);
        chord1.addNote(note12);
        chord1.addNote(note13);

        Chord chord2 = new Chord(new Note('D', -1, 1, new Meter(2, 3)));
        Chord chord3 = new Chord(new Note('E', -1, 1, new Meter(2, 3)));
        Chord chord4 = new Chord(new Note('F', -1, 1, new Meter(2, 3)));

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);
        expected.addChord(chord4);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureNoTuplets()
    {
      Lexer lexer = new Lexer("[^A,2^B,2^C,2]^B,2");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', -1, 1, new Meter(2, 1));
        chord1.addNote(note11);
        chord1.addNote(note12);
        chord1.addNote(note13);

        Chord chord2 = new Chord(new Note('B', -1, 1, new Meter(2,1)));

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureChordNoAccidental()
    {
      Lexer lexer = new Lexer("[^A,2^B,2C,2][_E,2_D,2_C,2]");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', -1, 0, new Meter(2, 1));
        chord1.addNote(note11);
        chord1.addNote(note12);
        chord1.addNote(note13);

        Chord chord2 = new Chord();
        Note note21 = new Note('E', -1, -1, new Meter(2, 1));
        Note note22 = new Note('D', -1, -1, new Meter(2, 1));
        Note note23 = new Note('C', -1, -1, new Meter(2, 1));
        chord2.addNote(note21);
        chord2.addNote(note22);
        chord2.addNote(note23);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureNoteNoAccidental()
    {
      Lexer lexer = new Lexer("^A,2B,2");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Chord chord2 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        Note note12 = new Note('B', -1, 0, new Meter(2, 1));
        chord1.addNote(note11);
        chord2.addNote(note12);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureTupletNoAccidental()
    {
      Lexer lexer = new Lexer("(3^A,B,^C,(3^D,^E,^F,");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord(new Note('A', -1, 1, new Meter(2, 3)));
        Chord chord2 = new Chord(new Note('B', -1, 0, new Meter(2, 3)));
        Chord chord3 = new Chord(new Note('C', -1, 1, new Meter(2, 3)));

        Chord chord4 = new Chord(new Note('D', -1, 1, new Meter(2, 3)));
        Chord chord5 = new Chord(new Note('E', -1, 1, new Meter(2, 3)));
        Chord chord6 = new Chord(new Note('F', -1, 1, new Meter(2, 3)));

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);
        expected.addChord(chord4);
        expected.addChord(chord5);
        expected.addChord(chord6);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureChordNoOctaveModifier()
    {
      Lexer lexer = new Lexer("[^A,2^B,2^C2][_E,2_D,2_C,2]");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', 0, 1, new Meter(2, 1));
        chord1.addNote(note11);
        chord1.addNote(note12);
        chord1.addNote(note13);

        Chord chord2 = new Chord();
        Note note21 = new Note('E', -1, -1, new Meter(2, 1));
        Note note22 = new Note('D', -1, -1, new Meter(2, 1));
        Note note23 = new Note('C', -1, -1, new Meter(2, 1));
        chord2.addNote(note21);
        chord2.addNote(note22);
        chord2.addNote(note23);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureNoteNoOctaveModifier()
    {
      Lexer lexer = new Lexer("^A2^B,2");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Chord chord2 = new Chord();
        Note note11 = new Note('A', 0, 1, new Meter(2, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        chord1.addNote(note11);
        chord2.addNote(note12);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureTupletNoOctaveModifier()
    {
      Lexer lexer = new Lexer("(3^A,^B,^C(3^D,^E,^F,");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord(new Note('A', -1, 1, new Meter(2, 3)));
        Chord chord2 = new Chord(new Note('B', -1, 1, new Meter(2, 3)));
        Chord chord3 = new Chord(new Note('C', 0, 1, new Meter(2, 3)));

        Chord chord4 = new Chord(new Note('D', -1, 1, new Meter(2, 3)));
        Chord chord5 = new Chord(new Note('E', -1, 1, new Meter(2, 3)));
        Chord chord6 = new Chord(new Note('F', -1, 1, new Meter(2, 3)));

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);
        expected.addChord(chord4);
        expected.addChord(chord5);
        expected.addChord(chord6);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureChordNoNoteLength()
    {
      Lexer lexer = new Lexer("[^A,^B,2^C,2][_E,2_D,2_C,2]");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(1, 1));
        //add another chord for rest??
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', -1, 1, new Meter(2, 1));
        chord1.addNote(note11);
        chord1.addNote(note12);
        chord1.addNote(note13);

        Chord chord2 = new Chord();
        Note note21 = new Note('E', -1, -1, new Meter(2, 1));
        Note note22 = new Note('D', -1, -1, new Meter(2, 1));
        Note note23 = new Note('C', -1, -1, new Meter(2, 1));
        chord2.addNote(note21);
        chord2.addNote(note22);
        chord2.addNote(note23);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureNoteNoNoteLength()
    {
      Lexer lexer = new Lexer("^A,^B,2^C,");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Chord chord2 = new Chord();
        Chord chord3 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(1, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', -1, 1, new Meter(1, 1));
        chord1.addNote(note11);
        chord2.addNote(note12);
        chord3.addNote(note13);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureChordOnlyNoteLength()
    {
      Lexer lexer = new Lexer("[^A,2^B,2C2][_E,2_D,2_C,2]");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', 0, 0, new Meter(2, 1));
        chord1.addNote(note11);
        chord1.addNote(note12);
        chord1.addNote(note13);

        Chord chord2 = new Chord();
        Note note21 = new Note('E', -1, -1, new Meter(2, 1));
        Note note22 = new Note('D', -1, -1, new Meter(2, 1));
        Note note23 = new Note('C', -1, -1, new Meter(2, 1));
        chord2.addNote(note21);
        chord2.addNote(note22);
        chord2.addNote(note23);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureNoteOnlyNoteLength()
    {
      Lexer lexer = new Lexer("A2^B,2");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Chord chord2 = new Chord();
        Note note11 = new Note('A', 0, 0, new Meter(2, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        chord1.addNote(note11);
        chord2.addNote(note12);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureChordOnlyAccidental()
    {
      Lexer lexer = new Lexer("[^A,2^B,2^C][_E,2_D,2_C,2]");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', 0, 1, new Meter(1, 1));
        //add rest chord?
        chord1.addNote(note11);
        chord1.addNote(note12);
        chord1.addNote(note13);

        Chord chord2 = new Chord();
        Note note21 = new Note('E', -1, -1, new Meter(2, 1));
        Note note22 = new Note('D', -1, -1, new Meter(2, 1));
        Note note23 = new Note('C', -1, -1, new Meter(2, 1));
        chord2.addNote(note21);
        chord2.addNote(note22);
        chord2.addNote(note23);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureNoteOnlyAccidental()
    {
      Lexer lexer = new Lexer("^A^B,2^C");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Chord chord2 = new Chord();
        Chord chord3 = new Chord();
        Note note11 = new Note('A', 0, 1, new Meter(1, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', 0, 1, new Meter(1, 1));
        chord1.addNote(note11);
        chord2.addNote(note12);
        chord3.addNote(note13);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureChordOnlyOctaveModifier()
    {
      Lexer lexer = new Lexer("[^A,2B,^C,2][_E,2_D,2_C,2]");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        Note note12 = new Note('B', -1, 0, new Meter(1, 1));
        //add rest chord?
        Note note13 = new Note('C', -1, 1, new Meter(2, 1));
        chord1.addNote(note11);
        chord1.addNote(note12);
        chord1.addNote(note13);

        Chord chord2 = new Chord();
        Note note21 = new Note('E', -1, -1, new Meter(2, 1));
        Note note22 = new Note('D', -1, -1, new Meter(2, 1));
        Note note23 = new Note('C', -1, -1, new Meter(2, 1));
        chord2.addNote(note21);
        chord2.addNote(note22);
        chord2.addNote(note23);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureNoteOnlyOctaveModifier()
    {
      Lexer lexer = new Lexer("A,^B,2C,");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Chord chord2 = new Chord();
        Chord chord3 = new Chord();
        Note note11 = new Note('A', -1, 0, new Meter(1, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', -1, 0, new Meter(1, 1));
        chord1.addNote(note11);
        chord2.addNote(note12);
        chord3.addNote(note13);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureChordOnlyName()
    {
      Lexer lexer = new Lexer("[^A,2B^C,2][_E,2_D,2_C,2]");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Note note11 = new Note('A', -1, 1, new Meter(2, 1));
        Note note12 = new Note('B', 0, 0, new Meter(1, 1));
        //add rest chord?
        Note note13 = new Note('C', -1, 1, new Meter(2, 1));
        chord1.addNote(note11);
        chord1.addNote(note12);
        chord1.addNote(note13);

        Chord chord2 = new Chord();
        Note note21 = new Note('E', -1, -1, new Meter(2, 1));
        Note note22 = new Note('D', -1, -1, new Meter(2, 1));
        Note note23 = new Note('C', -1, -1, new Meter(2, 1));
        chord2.addNote(note21);
        chord2.addNote(note22);
        chord2.addNote(note23);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureNoteOnlyName()
    {
      Lexer lexer = new Lexer("A^B,2C");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord();
        Chord chord2 = new Chord();
        Chord chord3 = new Chord();
        Note note11 = new Note('A', 0, 0, new Meter(1, 1));
        Note note12 = new Note('B', -1, 1, new Meter(2, 1));
        Note note13 = new Note('C', 0, 0, new Meter(1, 1));
        chord1.addNote(note11);
        chord2.addNote(note12);
        chord3.addNote(note13);

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);

        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void testParseMeasureTupletOnlyName()
    {
      Lexer lexer = new Lexer("(3AB,^C,(3^D,^E,^F,");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord(new Note('A', 0, 0, new Meter(2, 3)));
        Chord chord2 = new Chord(new Note('B', -1, 0, new Meter(2, 3)));
        Chord chord3 = new Chord(new Note('C', -1, 1, new Meter(2, 3)));

        Chord chord4 = new Chord(new Note('D', -1, 1, new Meter(2, 3)));
        Chord chord5 = new Chord(new Note('E', -1, 1, new Meter(2, 3)));
        Chord chord6 = new Chord(new Note('F', -1, 1, new Meter(2, 3)));

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);
        expected.addChord(chord4);
        expected.addChord(chord5);
        expected.addChord(chord6);

        assertEquals(expected.toString(), result.toString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testMissingNoteName()
    {
      Lexer lexer = new Lexer("(3^,B,^C,(3^D,^E,^F,");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord(new Note('A', -1, 1, new Meter(2, 3)));
        Chord chord2 = new Chord(new Note('B', -1, 0, new Meter(2, 3)));
        Chord chord3 = new Chord(new Note('C', -1, 1, new Meter(2, 3)));

        Chord chord4 = new Chord(new Note('D', -1, -1, new Meter(2, 3)));
        Chord chord5 = new Chord(new Note('E', -1, -1, new Meter(2, 3)));
        Chord chord6 = new Chord(new Note('F', -1, -1, new Meter(2, 3)));

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);
        expected.addChord(chord4);
        expected.addChord(chord5);
        expected.addChord(chord6);

        assertEquals(expected.toString(), result.toString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadNoteOrdering()
    {
      Lexer lexer = new Lexer("(3A^,B,^C,(3^D,^E,^F,");
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Measure result = parser.parseMeasure(tokens);

        Chord chord1 = new Chord(new Note('A', -1, 1, new Meter(2, 3)));
        Chord chord2 = new Chord(new Note('B', -1, 0, new Meter(2, 3)));
        Chord chord3 = new Chord(new Note('C', -1, 1, new Meter(2, 3)));

        Chord chord4 = new Chord(new Note('D', -1, -1, new Meter(2, 3)));
        Chord chord5 = new Chord(new Note('E', -1, -1, new Meter(2, 3)));
        Chord chord6 = new Chord(new Note('F', -1, -1, new Meter(2, 3)));

        Measure expected = new Measure();
        expected.addChord(chord1);
        expected.addChord(chord2);
        expected.addChord(chord3);
        expected.addChord(chord4);
        expected.addChord(chord5);
        expected.addChord(chord6);

        assertEquals(expected.toString(), result.toString());
    }

    //Start Parse Voice Tests

    @Test
    public void testParseVoiceFullSpec()
    {
      String reallyExpected = "Voice: 1\n[_a'1/2 B1/1 C,1/2] a2/3 b2/3 c2/3 ^D1/1 | B1/1 a2/3 b2/3 c2/3 [C,1/1 B1/2 E1/2] | ";
        String expected = "[_a'1/2BC,1/2](3abc^D|B(3abc[C,B1/2E1/2]|";
        Lexer lexer = new Lexer(expected);
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Voice voice = new Voice();
        voice.setName("1");
        parser.parseVoice(voice, tokens);
        String result = voice.toString();

        assertEquals(reallyExpected, result);
    }

    @Test
    public void testParseVoiceOnlyChords()
    {
      String reallyExpected = "Voice: null\n[_a'1/2 B1/1 C,1/2] [_e'1/1 D1/2 f'1/1] z2/1 | [a4/1 b4/1 c4/1] | ";
        String expected = "[_a'1/2BC,1/2][_e'D1/2f']z2|[a4b4c4]|";
        Lexer lexer = new Lexer(expected);
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Voice voice = new Voice();
        parser.parseVoice(voice, tokens);
        String result = voice.toString();

        assertEquals(reallyExpected, result);
    }

    @Test
    public void testParseVoiceOnlyTuplets()
    {
      String reallyExpected = "Voice: null\nC2/3 d'2/3 f2/3 a2/3 b2/3 c2/3 | A2/3 A2/3 E2/3 C,2/3 C2/3 c2/3 | ";
        String expected = "(3c,d'f(3abc|(3a,AE(3C,Cc|";
        Lexer lexer = new Lexer(expected);
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Voice voice = new Voice();
        parser.parseVoice(voice, tokens);
        String result = voice.toString();

        assertEquals(reallyExpected, result);
    }

    @Test
    public void testParseVoiceOnlyNotes()
    {
      String reallyExpected = "Voice: null\n_A,2/1 ^^b2/1 | C2/1 ^d'2/1 | ";
        String expected = "_A,2^^b2|C2^d'2|";
        Lexer lexer = new Lexer(expected);
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Voice voice = new Voice();
        parser.parseVoice(voice, tokens);
        String result = voice.toString();

        assertEquals(reallyExpected, result);
    }

    @Test
    public void testParseVoiceNoNotes()
    {
      String reallyExpected = "Voice: null\n[_a'1/2 B2/1 C,1/2] a2/3 b2/3 c2/3 | [_a'1/2 B2/1 C,1/2] a2/3 b2/3 c2/3 | ";
        String expected = "[_a'1/2B2C,1/2](3abc|[_a'1/2B2C,1/2](3abc|";
        Lexer lexer = new Lexer(expected);
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Voice voice = new Voice();
        parser.parseVoice(voice, tokens);
        String result = voice.toString();

        assertEquals(reallyExpected, result);
    }

    @Test
    public void testParseVoiceNoChords()
    {
      String reallyExpected = "Voice: null\na2/3 b2/3 c2/3 ^D1/1 e1/1 | ^a2/3 B2/3 c2/3 ^D1/1 e1/1 | ";
        String expected = "(3abc^DE'|(3^ab,c^De|";
        Lexer lexer = new Lexer(expected);
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Voice voice = new Voice();
        parser.parseVoice(voice, tokens);
        String result = voice.toString();

        assertEquals(reallyExpected, result);
    }

    @Test
    public void testParseVoiceNoTuplets()
    {
      String reallyExpected = "Voice: null\n[_a'1/2 B1/1 C,1/2] [_e'1/1 D1/2 f'1/1] z2/1 | ^E2/1 b1/1 c1/1 | ";
        String expected = "[_a'1/2BC,1/2][_e'D1/2f']z2|^E2bc|";
        Lexer lexer = new Lexer(expected);
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Voice voice = new Voice();
        parser.parseVoice(voice, tokens);
        String result = voice.toString();

        assertEquals(reallyExpected, result);
    }

    @Test
    public void testParseVoiceFunkyTime()
    {
      String reallyExpected = "Voice: null\n[_a'1/2 B1/1 C,1/2] [_e'1/1 D1/2 f'1/1] | ^E2/1 | ";
        String expected = "[_a'1/2BC,1/2][_e'D1/2f']|^E2|";
        Lexer lexer = new Lexer(expected);
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(2, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Voice voice = new Voice();
        parser.parseVoice(voice, tokens);
        String result = voice.toString();

        assertEquals(reallyExpected, result);
    }

    @Test
    public void testParseVoiceMeasureRepeat()
    {
      String expected = "Voice: null\n[_a'1/2 B1/1 C,1/2] [_e'1/1 D1/2 f'1/1] :| ^E2/1 | ";
        String testable = "[_a'1/2BC,1/2][_e'D1/2f']:|^E2|";
        Lexer lexer = new Lexer(testable);
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(2, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1, 4));
        Voice voice = new Voice();
        parser.parseVoice(voice, tokens);
        String result = voice.toString();

        assertEquals(expected, result);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseVoiceMissingNoteName()
    {
      String expected = "[_a'1/2BC,1/2][_'D1/2f']z2|[a4b4c4]|";
        Lexer lexer = new Lexer(expected);
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Voice voice = new Voice();
        parser.parseVoice(voice, tokens);
        String result = voice.toString();

        assertEquals(expected, result);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseVoiceBadNoteOrdering()
    {
      String expected = "[_a'1/2BC,1/2][e_'D1/2f']z2|[a4b4c4]|";
        Lexer lexer = new Lexer(expected);
        List<Token> tokens = lexer.generateTokens();

        Parser parser = new Parser(lexer);
        parser.getPiece().setKeySignature(new KeySignature("C"));
        parser.getPiece().setTimeSignature(new Meter(4, 4));
        parser.getPiece().setDefaultNoteLength(new Meter(1,4));
        Voice voice = new Voice();
        parser.parseVoice(voice, tokens);
        String result = voice.toString();

        assertEquals(expected, result);
    }

    @Test
    public void testParseHeadersAllFields()
    {
      Lexer lexer = new Lexer("X:0\nT:Bubbles\nC:Me\nM:4/4\nL:1/4\nQ:220\nV:upper\nV:middle\nK:C\nd");
        List<Token> tokens = lexer.generateTokens();
        Parser parser = new Parser(lexer);
        parser.parseHeaders(tokens);

        Piece piece = parser.getPiece();
        KeySignature key = piece.getKeySignature();
        int index = piece.getIndex();
        String title = piece.getTitle();
        String composer = piece.getComposer();
        Meter meter = piece.getTimeSignature();
        Meter noteLength = piece.getDefaultNoteLength();
        int tempo = piece.getDefaultNoteLengthsPerMinute();
        int numVoices = piece.getVoices().size();

        assertEquals(0, index);
        assertEquals("C", key.toString());
        assertEquals("Bubbles", title);
        assertEquals("Me", composer);
        assertEquals(new Meter(4,4), meter);
        assertEquals(new Meter(1,4), noteLength);
        assertEquals(220, tempo);
        assertEquals(2, numVoices);
    }

    @Test
    public void testParseHeadersSomeFields()
    {
      Lexer lexer = new Lexer("X:0\nT:Bubbles\nC:Me\nK:C\nd");
        List<Token> tokens = lexer.generateTokens();
        Parser parser = new Parser(lexer);
        parser.parseHeaders(tokens);

        Piece piece = parser.getPiece();
        KeySignature key = piece.getKeySignature();
        int index = piece.getIndex();
        String title = piece.getTitle();
        String composer = piece.getComposer();
        Meter meter = piece.getTimeSignature();
        Meter noteLength = piece.getDefaultNoteLength();
        int tempo = piece.getDefaultNoteLengthsPerMinute();

        assertEquals(0, index);
        assertEquals("C", key.toString());
        assertEquals("Bubbles", title);
        assertEquals("Me", composer);
        assertEquals(new Meter(4,4), meter);
        assertEquals(new Meter(1,8), noteLength);
        assertEquals(100, tempo);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseHeadersUnknownField()
    {
      Lexer lexer = new Lexer("X:0 T:Bubbles C:Me D:oblong M:4/4 L:1/4 Q:220 V:upper V:middle K:C d");
        List<Token> tokens = lexer.generateTokens();
        Parser parser = new Parser(lexer);
        parser.parseHeaders(tokens);

        Piece piece = parser.getPiece();
        KeySignature key = piece.getKeySignature();
        int index = piece.getIndex();
        String title = piece.getTitle();
        String composer = piece.getComposer();
        Meter meter = piece.getTimeSignature();
        Meter noteLength = piece.getDefaultNoteLength();
        int tempo = piece.getDefaultNoteLengthsPerMinute();
        int numVoices = piece.getVoices().size();

        assertEquals(0, index);
        assertEquals("C", key.toString());
        assertEquals("Bubbles", title);
        assertEquals("Me", composer);
        assertEquals(new Meter(4,4), meter);
        assertEquals(new Meter(1,4), noteLength);
        assertEquals(220, tempo);
        assertEquals(2, numVoices);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseHeadersNoTitle()
    {
      Lexer lexer = new Lexer("X:0 C:Me M:4/4 L:1/4 Q:220 V:upper V:middle K:C d");
        List<Token> tokens = lexer.generateTokens();
        Parser parser = new Parser(lexer);
        parser.parseHeaders(tokens);

        Piece piece = parser.getPiece();
        KeySignature key = piece.getKeySignature();
        int index = piece.getIndex();
        String title = piece.getTitle();
        String composer = piece.getComposer();
        Meter meter = piece.getTimeSignature();
        Meter noteLength = piece.getDefaultNoteLength();
        int tempo = piece.getDefaultNoteLengthsPerMinute();
        int numVoices = piece.getVoices().size();

        assertEquals(0, index);
        assertEquals("C", key.toString());
        assertEquals("Bubbles", title);
        assertEquals("Me", composer);
        assertEquals(new Meter(4,4), meter);
        assertEquals(new Meter(1,4), noteLength);
        assertEquals(220, tempo);
        assertEquals(2, numVoices);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParseHeadersNoIndex()
    {
      Lexer lexer = new Lexer("T:Bubbles C:Me M:4/4 L:1/4 Q:220 V:upper V:middle K:C d");
        List<Token> tokens = lexer.generateTokens();
        Parser parser = new Parser(lexer);
        parser.parseHeaders(tokens);

        Piece piece = parser.getPiece();
        KeySignature key = piece.getKeySignature();
        int index = piece.getIndex();
        String title = piece.getTitle();
        String composer = piece.getComposer();
        Meter meter = piece.getTimeSignature();
        Meter noteLength = piece.getDefaultNoteLength();
        int tempo = piece.getDefaultNoteLengthsPerMinute();
        int numVoices = piece.getVoices().size();

        assertEquals(0, index);
        assertEquals("C", key.toString());
        assertEquals("Bubbles", title);
        assertEquals("Me", composer);
        assertEquals(new Meter(4,4), meter);
        assertEquals(new Meter(1,4), noteLength);
        assertEquals(220, tempo);
        assertEquals(2, numVoices);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadDefaultNoteLength()
    {
      Lexer lexer = new Lexer("X:0 T:Bubbles C:Me M:4/4 L:ac Q:220 V:upper V:middle K:C d");
        List<Token> tokens = lexer.generateTokens();
        Parser parser = new Parser(lexer);
        parser.parseHeaders(tokens);

        Piece piece = parser.getPiece();
        KeySignature key = piece.getKeySignature();
        int index = piece.getIndex();
        String title = piece.getTitle();
        String composer = piece.getComposer();
        Meter meter = piece.getTimeSignature();
        Meter noteLength = piece.getDefaultNoteLength();
        int tempo = piece.getDefaultNoteLengthsPerMinute();
        int numVoices = piece.getVoices().size();

        assertEquals(0, index);
        assertEquals("C", key.toString());
        assertEquals("Bubbles", title);
        assertEquals("Me", composer);
        assertEquals(new Meter(4,4), meter);
        assertEquals(new Meter(1,4), noteLength);
        assertEquals(220, tempo);
        assertEquals(2, numVoices);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBadTimeSignature()
    {
      Lexer lexer = new Lexer("X:0 T:Bubbles C:Me M:whee L:1/4 Q:220 V:upper V:middle K:C d");
        List<Token> tokens = lexer.generateTokens();
        Parser parser = new Parser(lexer);
        parser.parseHeaders(tokens);

        Piece piece = parser.getPiece();
        KeySignature key = piece.getKeySignature();
        int index = piece.getIndex();
        String title = piece.getTitle();
        String composer = piece.getComposer();
        Meter meter = piece.getTimeSignature();
        Meter noteLength = piece.getDefaultNoteLength();
        int tempo = piece.getDefaultNoteLengthsPerMinute();
        int numVoices = piece.getVoices().size();

        assertEquals(0, index);
        assertEquals("C", key.toString());
        assertEquals("Bubbles", title);
        assertEquals("Me", composer);
        assertEquals(new Meter(4,4), meter);
        assertEquals(new Meter(1,4), noteLength);
        assertEquals(220, tempo);
        assertEquals(2, numVoices);
    }

    //test merge voices
    @Test
    public void testMergeVoicesOneVoice()
    {
      Map<String, List<Token>> result;
        Map<String, List<Token>> expected = new HashMap<String, List<Token>>();

        Lexer lexer = new Lexer("c d f g b 1/4");
        List<Token> tokens = lexer.generateTokens();
        Parser parser = new Parser(lexer);
        result = parser.mergeVoices(tokens);

        expected.put("1", tokens);
        //not sure about checking maps like this
        assertEquals(expected, result);
    }

    @Test
    public void testMergeVoicesMultipleVoices()
    {
      Map<String, List<Token>> result;
        Map<String, List<Token>> expected = new HashMap<String, List<Token>>();

        Lexer lexer = new Lexer("X:0\nT:Bubbles\nC:Me\nM:4/4\nL:1/4\nQ:220\nV:upper\nV:lower\nK:C\n" +
            "V:upper\nc d f g b 1/4 V:lower\nb c d g/2");
        List<Token> tokens = lexer.generateTokens();
        Parser parser = new Parser(lexer);
        tokens = parser.parseHeaders(tokens);
        result = parser.mergeVoices(tokens);
        List<Token> firstTokens = new ArrayList<Token>();
        firstTokens.add(tokens.get(1));
        firstTokens.add(tokens.get(2));
        firstTokens.add(tokens.get(3));
        firstTokens.add(tokens.get(4));
        firstTokens.add(tokens.get(5));
        firstTokens.add(tokens.get(6));
        List<Token> secondTokens = new ArrayList<Token>();
        secondTokens.add(tokens.get(8));
        secondTokens.add(tokens.get(9));
        secondTokens.add(tokens.get(10));
        secondTokens.add(tokens.get(11));
        secondTokens.add(tokens.get(12));

        expected.put("upper", firstTokens);
        expected.put("lower", secondTokens);

        assertEquals(expected, result);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testMergeVoicesMissingDeclaredVoice()
    {
      Map<String, List<Token>> result;
        Map<String, List<Token>> expected = new HashMap<String, List<Token>>();

        Lexer lexer = new Lexer("X:0\nT:Bubbles\nC:Me\nM:4/4\nL:1/4\nQ:220\nV:upper\nV:lower\nK:C\n" +
            "c d f g b 1/4 V:lower\nb c d g/2");
        List<Token> tokens = lexer.generateTokens();
        Parser parser = new Parser(lexer);
        tokens = parser.parseHeaders(tokens);
        result = parser.mergeVoices(tokens);
        List<Token> firstTokens = new ArrayList<Token>();
        firstTokens.add(tokens.get(1));
        firstTokens.add(tokens.get(2));
        firstTokens.add(tokens.get(3));
        firstTokens.add(tokens.get(4));
        firstTokens.add(tokens.get(5));
        firstTokens.add(tokens.get(6));
        List<Token> secondTokens = new ArrayList<Token>();
        secondTokens.add(tokens.get(8));
        secondTokens.add(tokens.get(9));
        secondTokens.add(tokens.get(10));
        secondTokens.add(tokens.get(11));
        secondTokens.add(tokens.get(12));

        expected.put("upper", firstTokens);
        expected.put("lower", secondTokens);

        assertEquals(expected, result);
    }

    //test full parse piece
    @Test
    public void testParsePieceFullSpec()
    {
      String currentDir = System.getProperty("user.dir");
        String allLinesTogether = "";
        String expected = "Index Number: 1\n" +
        "Title: Bagatelle No.25 in A, WoO.59\n" +
        "Composer: Ludwig van Beethoven\n" +
        "Default Note Length: 1/16\n" +
        "Time Signature: 3/8\n" +
        "Tempo: 240\n" +
        "Voice: 1\n" +
        "Voice: 2\n" +
        "Key Signature: Am\n" +
        "Voice: 1\n" +
        "e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 ^G1/1 B1/1 | c2/1 z1/1 E1/1 e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 c1/1 B1/1 | [1 A2/1 z2/1 :| [2 A2/1 z1/1 B1/1 c1/1 d1/1 | | : e3/1 G1/1 f1/1 e1/1 | d3/1 F1/1 e1/1 d1/1 | c3/1 E1/1 d1/1 c1/1 | B2/1 z1/1 E1/1 e1/1 z1/1 | z1/1 e1/1 e'1/1 z1/1 z1/1 ^d1/1 | e1/1 z1/1 z1/1 ^d1/1 e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 ^G1/1 B1/1 | c2/1 z1/1 E1/1 e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 c1/1 B1/1 | [1 A2/1 z1/1 B1/1 c1/1 d1/1 :| [2 A2/1 z1/1 [E1/1 c1/1] [F1/1 c1/1] [E1/1 G1/1 c1/1] | c4/1 f1/1 e1/1 | e2/1 d2/1 _b1/1 a1/1 | a1/1 g1/1 f1/1 e1/1 d1/1 c1/1 | _B2/1 A2/1 A1/2 G1/2 A1/2 _B1/2 | c4/1 d1/1 ^d1/1 | e3/1 e1/1 f1/1 A1/1 | c4/1 d1/1 B1/1 | c1/2 g1/2 G1/2 g1/2 A1/2 g1/2 B1/2 g1/2 c1/2 g1/2 d1/2 g1/2 | e1/2 g1/2 c'1/2 b1/2 a1/2 g1/2 f1/2 e1/2 d1/2 g1/2 f1/2 d1/2 | c1/2 g1/2 G1/2 g1/2 A1/2 g1/2 B1/2 g1/2 c1/2 g1/2 d1/2 g1/2 | e1/2 g1/2 c'1/2 b1/2 a1/2 g1/2 f1/2 e1/2 d1/2 g1/2 f1/2 d1/2 | e1/2 f1/2 e1/2 ^d1/2 e1/2 B1/2 e1/2 ^d1/2 e1/2 B1/2 e1/2 ^d1/2 | e3/1 B1/1 e1/1 ^d1/1 | e3/1 B1/1 e1/1 z1/1 | z1/1 ^d1/1 e1/1 z1/1 z1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 ^G1/1 B1/1 | c2/1 z1/1 E1/1 e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 c1/1 B1/1 | A2/1 z1/1 B1/1 c1/1 d1/1 | e3/1 G1/1 f1/1 e1/1 | d3/1 F1/1 e1/1 d1/1 | c3/1 E1/1 d1/1 c1/1 | B2/1 z1/1 E1/1 e1/1 z1/1 | z1/1 e1/1 e'1/1 z1/1 z1/1 ^d1/1 | e1/1 z1/1 z1/1 ^d1/1 e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 ^G1/1 B1/1 | c2/1 z1/1 E1/1 e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 c1/1 B1/1 | A2/1 z2/1 z2/1 | [E6/1 G6/1 _B6/1 ^c6/1] | [F4/1 A4/1 d4/1] [^c1/1 e1/1] [d1/1 f1/1] | [^G4/1 d4/1 f4/1] [^G2/1 d2/1 f2/1] | [A6/1 c6/1 e6/1] | [F4/1 d4/1] [E1/1 c1/1] [D1/1 B1/1] | [C4/1 ^F4/1 A4/1] [C2/1 A2/1] | [C2/1 A2/1] [E2/1 c2/1] [D2/1 B2/1] | [C6/1 A6/1] | [E6/1 G6/1 _B6/1 ^c6/1] | [F4/1 A4/1 d4/1] [^c1/1 e1/1] [d1/1 f1/1] | [d4/1 f4/1] [d2/1 f2/1] | [d6/1 f6/1] | [G4/1 _e4/1] [F1/1 d1/1] [_E1/1 c1/1] | [D4/1 F4/1 _B4/1] [D2/1 F2/1 A2/1] | [D4/1 F4/1 ^G4/1] [D2/1 F2/1 ^G2/1] | [C2/1 E2/1 A2/1] z2/1 z2/1 | [E2/1 B2/1] z2/1 z2/1 | A,2/3 C2/3 E2/3 A2/3 c2/3 e2/3 d2/3 c2/3 B2/3 | A2/3 c2/3 e2/3 a2/3 c'2/3 e'2/3 d'2/3 c'2/3 b2/3 | A2/3 c2/3 e2/3 a2/3 c'2/3 e'2/3 d'2/3 c'2/3 b2/3 | _b2/3 a2/3 _a2/3 g2/3 _g2/3 f2/3 e2/3 _e2/3 d2/3 | _d'2/3 c'2/3 b2/3 _b2/3 a2/3 _b2/3 g2/3 _g2/3 f2/3 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 ^G1/1 B1/1 | c2/1 z1/1 E1/1 e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 c1/1 B1/1 | A2/1 z1/1 B1/1 c1/1 d1/1 | e3/1 G1/1 f1/1 e1/1 | d3/1 F1/1 e1/1 d1/1 | c3/1 E1/1 d1/1 c1/1 | B2/1 z1/1 E1/1 e1/1 z1/1 | z1/1 e1/1 e'1/1 z1/1 z1/1 ^d1/1 | e1/1 z1/1 z1/1 ^d1/1 e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 ^G1/1 B1/1 | c2/1 z1/1 E1/1 e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 D1/1 c1/1 B1/1 | [C4/1 A4/1] |] \n" +
        "Voice: 2\n" +
        "z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | [1 A,,1/1 E,1/1 A,1/1 z1/1 :| [2 A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | | : C,1/1 E,1/1 C1/1 z1/1 z2/1 | G,,1/1 G,1/1 B,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 E1/1 z1/1 z1/1 E1/1 | e1/1 z1/1 z1/1 ^d1/1 e1/1 z1/1 | z1/1 ^d1/1 e1/1 z1/1 z2/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | [1 A,,1/1 E,1/1 A,1/1 z1/1 z2/1 :| [2 A,,1/1 E,1/1 A,1/1 [_B,1/1 C1/1] [A,1/1 C1/1] [G,1/1 _B,1/1 C1/1] | F,1/1 A,1/1 C1/1 A,1/1 C1/1 A,1/1 | F,1/1 _B,1/1 D1/1 _B,1/1 D1/1 _B,1/1 | F,1/1 E1/1 [F,1/1 G,1/1 _B,1/1] E1/1 [F,1/1 G,1/1 _B,1/1] E1/1 | F,1/1 A,1/1 C1/1 A,1/1 C1/1 A,1/1 | F,1/1 A,1/1 C1/1 A,1/1 C1/1 A,1/1 | E,1/1 A,1/1 C1/1 A,1/1 [D,1/1 D1/1] F,1/1 | G,1/1 E1/1 G,1/1 E1/1 G,1/1 F1/1 | [C2/1 E2/1] z1/1 [F1/1 G1/1] [E1/1 G1/1] [D1/1 F1/1 G1/1] | [C2/1 E2/1 G2/1] [F,2/1 A,2/1] [F,2/1 A,2/1] | C2/1 z1/1 [F1/1 G1/1] [E1/1 G1/1] [D1/1 F1/1 G1/1] | [C2/1 E2/1 G2/1] [F,2/1 A,2/1] [G,2/1 B,2/1] | [^G,2/1 B,2/1] z2/1 z2/1 | z6/1 | z4/1 z1/1 ^d1/1 | e1/1 z1/1 z1/1 ^d1/1 e1/1 z1/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | C,1/1 E,1/1 C1/1 z1/1 z2/1 | G,,1/1 G,1/1 B,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 E1/1 z1/1 z1/1 E1/1 | e1/1 z1/1 z1/1 ^d1/1 e1/1 z1/1 | z1/1 ^d1/1 e1/1 z1/1 z2/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 | A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 | A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 | A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 | A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 | [D,,1/1 A,,1/1] [D,,1/1 A,,1/1] [D,,1/1 A,,1/1] [D,,1/1 A,,1/1] [D,,1/1 A,,1/1] [D,,1/1 A,,1/1] | [^D,,1/1 A,,1/1] [^D,,1/1 A,,1/1] [^D,,1/1 A,,1/1] [^D,,1/1 A,,1/1] [^D,,1/1 A,,1/1] [^D,,1/1 A,,1/1] | [E,,1/1 A,,1/1] [E,,1/1 A,,1/1] [E,,1/1 A,,1/1] [E,,1/1 A,,1/1] [E,,1/1 ^G,,1/1] [E,,1/1 ^G,,1/1] | [A,,,1/1 A,,1/1] A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 | A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 | A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 | A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 A,,1/1 | _B,,1/1 _B,,1/1 _B,,1/1 _B,,1/1 _B,,1/1 _B,,1/1 | _B,,1/1 _B,,1/1 _B,,1/1 _B,,1/1 _B,,1/1 _B,,1/1 | _B,,1/1 _B,,1/1 _B,,1/1 _B,,1/1 _B,,1/1 _B,,1/1 | B,,1/1 B,,1/1 B,,1/1 B,,1/1 B,,1/1 B,,1/1 | C,2/1 z2/1 z2/1 | [E,2/1 ^G,2/1] z2/1 z2/1 | A,,,2/1 z2/1 [A,2/1 C2/1 E2/1] | [A,2/1 C2/1 E2/1] z2/1 [A,2/1 C2/1 E2/1] | [A,2/1 C2/1 E2/1] z2/1 [A,2/1 C2/1 E2/1] | [A,2/1 C2/1 E2/1] z2/1 z2/1 | z6/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | C,1/1 E,1/1 C1/1 z1/1 z2/1 | G,,1/1 G,1/1 B,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 E1/1 z1/1 z1/1 E1/1 | e1/1 z1/1 z1/1 ^d1/1 e1/1 z1/1 | z1/1 ^d1/1 e1/1 z1/1 z2/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | [A,,,4/1 A,,4/1] |] \n";
        BufferedReader reader;
        try
        {
          reader = new BufferedReader(new FileReader(currentDir + "/test_abc/fur_elise.abc"));
            String line;
            while((line = reader.readLine()) != null)
            {
              allLinesTogether += (line + "\n");
            }
        }
      catch(IOException e)
      {
        e.printStackTrace();
      }
      Lexer lexer = new Lexer(allLinesTogether);
        Parser parser = new Parser(lexer);
        Piece piece = parser.parse();
        assertEquals(expected, piece.toString());

    }

    @Test
    public void testParsePieceSingleEndingRepeatOneVoice()
    {
      String currentDir = System.getProperty("user.dir");
        String allLinesTogether = "";
        String expected = "Index Number: 1\n" +
        "Title: Bagatelle No.25 in A, WoO.59\n" +
        "Composer: Ludwig van Beethoven \n" +
        "Default Note Length: 1/16\n" +
        "Time Signature: 3/8\n" +
        "Tempo: 240\n" +
        "Voice: 1\n" +
        "Key Signature: Am\n" +
        "Voice: 1\n" +
        "e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 ^G1/1 B1/1 | c2/1 z1/1 E1/1 e1/1 ^d1/1 :| \n";
        BufferedReader reader;
        try
        {
          reader = new BufferedReader(new FileReader(currentDir + "/test_abc/singleEndRepOneV.abc"));
            String line;
            while((line = reader.readLine()) != null)
            {
              allLinesTogether += (line + "\n");
            }
        }
      catch(IOException e)
      {
        e.printStackTrace();
      }
      Lexer lexer = new Lexer(allLinesTogether);
        Parser parser = new Parser(lexer);
        Piece piece = parser.parse();
        assertEquals(expected, piece.toString());
    }

    @Test
    public void testParsePieceDoubleEndingRepeatOneVoice()
    {
      String currentDir = System.getProperty("user.dir");
        String allLinesTogether = "";
        String expected = "Index Number: 1\n" +
        "Title: Bagatelle No.25 in A, WoO.59\n" +
        "Composer: Ludwig van Beethoven \n" +
        "Default Note Length: 1/16\n" +
        "Time Signature: 3/8\n" +
        "Tempo: 240\n" +
        "Voice: 1\n" +
        "Key Signature: Am\n" +
        "Voice: 1\n" +
        "e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 ^G1/1 B1/1 | [1 c2/1 z1/1 E1/1 e1/1 ^d1/1 :| c2/1 z1/1 E1/1 f1/1 _a1/1 |] \n";

        BufferedReader reader;
        try
        {
          reader = new BufferedReader(new FileReader(currentDir + "/test_abc/doubleEndOneV.abc"));
            String line;
            while((line = reader.readLine()) != null)
            {
              allLinesTogether += (line + "\n");
            }
        }
      catch(IOException e)
      {
        e.printStackTrace();
      }
      Lexer lexer = new Lexer(allLinesTogether);
        Parser parser = new Parser(lexer);
        Piece piece = parser.parse();
        assertEquals(expected, piece.toString());
    }

    @Test
    public void testParsePieceSingleEndingRepeatMultVoice()
    {
      String currentDir = System.getProperty("user.dir");
        String allLinesTogether = "";
        String expected = "Index Number: 1\n" +
        "Title: Bagatelle No.25 in A, WoO.59\n" +
        "Composer: Ludwig van Beethoven\n" +
        "Default Note Length: 1/16\n" +
        "Time Signature: 3/8\n" +
        "Tempo: 240\n" +
        "Voice: 1\n" +
        "Voice: 2\n" +
        "Key Signature: Am\n" +
        "Voice: 1\n" +
        "e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 ^G1/1 B1/1 | c2/1 z1/1 E1/1 e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 c1/1 B1/1 | [1 A2/1 z2/1 :| \n" +
        "Voice: 2\n" +
        "z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | [1 A,,1/1 E,1/1 A,1/1 z1/1 :| \n";

        BufferedReader reader;
        try
        {
          reader = new BufferedReader(new FileReader(currentDir + "/test_abc/singleEndRepMultV.abc"));
            String line;
            while((line = reader.readLine()) != null)
            {
              allLinesTogether += (line + "\n");
            }
        }
      catch(IOException e)
      {
        e.printStackTrace();
      }
      Lexer lexer = new Lexer(allLinesTogether);
        Parser parser = new Parser(lexer);
        Piece piece = parser.parse();
        assertEquals(expected, piece.toString());

    }

    @Test
    public void testParsePieceDoubleEndingRepeatMultVoice()
    {
      String currentDir = System.getProperty("user.dir");
        String allLinesTogether = "";
        String expected = "Index Number: 1\n" +
        "Title: Bagatelle No.25 in A, WoO.59\n" +
        "Composer: Ludwig van Beethoven\n" +
        "Default Note Length: 1/16\n" +
        "Time Signature: 3/8\n" +
        "Tempo: 240\n" +
        "Voice: 1\n" +
        "Voice: 2\n" +
        "Key Signature: Am\n" +
        "Voice: 1\n" +
        "e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 ^G1/1 B1/1 | c2/1 z1/1 E1/1 e1/1 ^d1/1 | e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | A2/1 z1/1 C1/1 E1/1 A1/1 | B2/1 z1/1 E1/1 c1/1 B1/1 | [1 A2/1 z2/1 :| [2 A2/1 z1/1 B1/1 c1/1 d1/1 | \n" +
        "Voice: 2\n" +
        "z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | z6/1 | A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | E,,1/1 E,1/1 ^G,1/1 z1/1 z2/1 | [1 A,,1/1 E,1/1 A,1/1 z1/1 :| [2 A,,1/1 E,1/1 A,1/1 z1/1 z2/1 | \n";
        BufferedReader reader;
        try
        {
          reader = new BufferedReader(new FileReader(currentDir + "/test_abc/dblEndRepMultV.abc"));
            String line;
            while((line = reader.readLine()) != null)
            {
              allLinesTogether += (line + "\n");
            }
        }
      catch(IOException e)
      {
        e.printStackTrace();
      }
      Lexer lexer = new Lexer(allLinesTogether);
        Parser parser = new Parser(lexer);
        Piece piece = parser.parse();
        assertEquals(expected, piece.toString());
    }

    @Test
    public void testParsePieceOneMeasure()
    {
      String currentDir = System.getProperty("user.dir");
        String expected = "Index Number: 1\n" +
        "Title: Bagatelle No.25 in A, WoO.59\n" +
        "Composer: Ludwig van Beethoven \n" +
        "Default Note Length: 1/16\n" +
        "Time Signature: 3/8\n" +
        "Tempo: 240\n" +
        "Voice: 1\n" +
        "Key Signature: Am\n" +
        "Voice: 1\n" +
        "e1/1 ^d1/1 e1/1 B1/1 d1/1 c1/1 | \n";

        String allLinesTogether = "";
        BufferedReader reader;
        try
        {
          reader = new BufferedReader(new FileReader(currentDir + "/test_abc/pieceOneMeasure.abc"));
            String line;
            while((line = reader.readLine()) != null)
            {
              allLinesTogether += (line + "\n");
            }
        }
      catch(IOException e)
      {
        e.printStackTrace();
      }
      Lexer lexer = new Lexer(allLinesTogether);
        Parser parser = new Parser(lexer);
        Piece piece = parser.parse();
        assertEquals(expected, piece.toString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParsePieceUnmatchedRepeat()
    {
      String currentDir = System.getProperty("user.dir");
        String allLinesTogether = "";
        BufferedReader reader;
        try
        {
          reader = new BufferedReader(new FileReader(currentDir + "/test_abc/unmatchedRep.abc"));
            String line;
            while((line = reader.readLine()) != null)
            {
              allLinesTogether += (line + "\n");
            }
        }
      catch(IOException e)
      {
        e.printStackTrace();
      }
      Lexer lexer = new Lexer(allLinesTogether);
        Parser parser = new Parser(lexer);
        Piece piece = parser.parse();
        assertEquals(allLinesTogether, piece.toString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testParsePieceMissingDeclaredVoice()
    {
      String currentDir = System.getProperty("user.dir");
        String allLinesTogether = "";
        BufferedReader reader;
        try
        {
          reader = new BufferedReader(new FileReader(currentDir + "/test_abc/missingVoice.abc"));
            String line;
            while((line = reader.readLine()) != null)
            {
              allLinesTogether += (line + "\n");
            }
        }
      catch(IOException e)
      {
        e.printStackTrace();
      }
      Lexer lexer = new Lexer(allLinesTogether);
        Parser parser = new Parser(lexer);
        Piece piece = parser.parse();
        assertEquals(allLinesTogether, piece.toString());
    }
  @Test
  public void testPiece1()
  {
    String currentDir = System.getProperty("user.dir");
      String allLinesTogether = "";
      BufferedReader reader;
      try
      {
        reader = new BufferedReader(new FileReader(currentDir + "/sample_abc/testPiece1.abc"));
          String line;
          while((line = reader.readLine()) != null)
          {
            allLinesTogether += (line + "\n");
          }
      }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    Lexer lexer = new Lexer(allLinesTogether);
      Parser parser = new Parser(lexer);
      Piece piece = parser.parse();

      String expected = "Index Number: 83\n" +
      "Title: I'm in the shower\n" +
      "Composer: Eric Lander\n" +
      "Default Note Length: 1/4\n" +
      "Time Signature: 4/4\n" +
      "Tempo: 100\n" +
      "Voice: 1\n" +
      "Voice: 2\n" +
      "Key Signature: A\n" +
      "Voice: 1\n" +
      "^g1/1 ^g1/1 a3/4 z1/4 ^c1/1 | d1/2 z1/2 ^g2/1 z1/1 | _b1/1 B1/1 b1/1 b1/1 | ^^C1/1 __F1/1 ^c1/1 ^f1/1 | \n" +
      "Voice: 2\n" +
      "^e1/1 _f1/1 ^e1/1 _f1/1 | ^e1/1 E,,1/1 e'1/1 ^e1/1 | ^G,1/2 g'1/2 z2/1 | ^F,,3/2 ^^a1/2 A1/1 ^^a1/1 | \n";
      assertEquals(expected, piece.toString());
  }
  @Test
  public void testPiece2()
  {
    String currentDir = System.getProperty("user.dir");
      String allLinesTogether = "";
      BufferedReader reader;
      try
      {
        reader = new BufferedReader(new FileReader(currentDir + "/sample_abc/testPiece2.abc"));
          String line;
          while((line = reader.readLine()) != null)
          {
            allLinesTogether += (line + "\n");
          }
      }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    Lexer lexer = new Lexer(allLinesTogether);
      Parser parser = new Parser(lexer);
      Piece piece = parser.parse();

      String expected = "Index Number: 10\n" +
      "Title: IHTFP\n" +
      "Composer: MIT\n" +
      "Default Note Length: 1/4\n" +
      "Time Signature: 3/4\n" +
      "Tempo: 100\n" +
      "Voice: 1\n" +
      "Key Signature: Em\n" +
      "Voice: 1\n" +
      "^G1/1 z3/4 g'4/1 D1/2 :| a3/2 ^b3/2 | e2/6 A2/6 C,2/6 A2/1 | A3/4 d'3/4 _c3/4 _c3/4 | [c2/1 A2/1 ^F2/1] ^f1/1 :| [g1/1 ^A1/1 a1/1] [^A1/1 a1/1] _A1/1 | _F1/1 ^f1/2 _F1/1 F1/1 | : ^F1/1 ^d2/6 a2/6 c2/6 G1/1 | [1 e3/1 :| [2 f1/1 a1/2 c1/2 z1/2 z1/2 | \n";
      assertEquals(expected, piece.toString());
  }
  @Test
  public void testPiece3Nutcracker()
  {
    String currentDir = System.getProperty("user.dir");
      String allLinesTogether = "";
      BufferedReader reader;
      try
      {
        reader = new BufferedReader(new FileReader(currentDir + "/sample_abc/nutcracker.abc"));
          String line;
          while((line = reader.readLine()) != null)
          {
            allLinesTogether += (line + "\n");
          }
      }
    catch(IOException e)
    {
      e.printStackTrace();
    }
    Lexer lexer = new Lexer(allLinesTogether);
      Parser parser = new Parser(lexer);
      Piece piece = parser.parse();

      String expected = "Index Number: 1\n" +
      "Title: Dance of the Reed Flutes Nutcracker Suite (original key D, original tempo 72)\n" +
      "Composer: Tchaikovsky\n" +
      "Default Note Length: 1/8\n" +
      "Time Signature: 4/4\n" +
      "Tempo: 35\n" +
      "Voice: 1\n" +
      "Key Signature: G\n" +
      "Voice: 1\n" +
      "G,1/1 D,1/1 D,1/1 D,1/1 | G,1/1 D,1/1 D,1/1 D,6/7 | G,1/7 G1/7 D1/7 B,3/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/7 D,1/7 ^F1/7 A,5/7 | G,1/7 A1/7 D1/7 C4/7 D,1/7 G1/4 B1/4 d1/4 g1/4 b1/7 D,1/7 g1/7 d4/7 D,6/7 | G,1/7 c'1/7 g1/7 e3/14 b1/7 d3/14 D,1/7 c'1/7 e5/14 b1/7 d3/14 D,1/7 a1/7 c5/14 g1/7 B3/14 D,1/7 d1/7 B5/14 B1/7 G3/14 | A,1/7 G1/7 _E1/7 C5/7 A,5/7 g2/7 ^f1/7 D,1/7 d1/7 c4/7 D,6/7 | ^F,1/7 e1/7 c1/7 A3/14 E5/14 ^F,1/7 E1/7 C1/7 A,3/14 E3/14 ^F2/7 E1/7 D,1/7 ^C1/7 ^A1/7 G,2/7 G,1/7 D1/7 B,5/7 | ^F,1/7 e1/7 c1/7 A,3/14 E5/14 A,1/7 E1/7 C5/14 E3/14 ^F2/7 E1/7 G,1/7 ^C1/7 B,3/7 G,1/7 D1/7 B,5/7 | C,1/7 g1/7 _e1/7 A,3/14 G5/14 C,1/7 G1/7 _E5/14 G1/7 _E3/14 ^C,1/7 G1/7 E1/7 A,3/14 ^F1/7 E3/14 ^C,1/7 A1/7 E5/14 G1/7 E3/14 | D,1/7 G1/7 C1/7 A,3/14 ^F5/14 D,1/7 E1/7 C1/7 ^F,3/14 D1/7 C3/14 D,1/7 c1/7 ^F1/7 A,5/7 A1/4 ^F1/4 C1/4 A,1/4 | G1/7 G,1/7 D3/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/7 D,1/7 ^F1/7 A,5/7 | G,1/7 A1/7 D1/7 C4/7 D,1/7 G1/4 B1/4 d1/4 g1/4 b1/7 D,1/7 g1/7 d4/7 D,6/7 | G,1/7 c'1/7 g1/7 e3/14 b1/7 d3/14 D,1/7 c'1/7 e5/14 b1/7 d3/14 D,1/7 a1/7 c5/14 g1/7 B3/14 D,1/7 d1/7 B5/14 B1/7 G3/14 | A,1/7 G1/7 _E1/7 C5/7 A,5/7 g2/7 ^f1/7 D,1/7 d1/7 c4/7 D,6/7 | ^F,1/7 e1/7 c1/7 A3/14 E5/14 ^F,1/7 E1/7 C1/7 A,3/14 E3/14 ^F2/7 E1/7 D,1/7 ^C1/7 ^A1/7 G,2/7 G,1/7 D1/7 B,5/7 | ^A,1/7 g1/7 e1/7 ^c3/14 G5/14 ^A,1/7 G1/7 E1/7 ^C3/14 G3/14 A2/7 G1/7 B,1/7 E1/7 ^C3/7 B,1/7 ^F1/7 D5/7 | ^C,1/7 b1/7 g1/7 e3/14 B5/14 ^C,1/7 B1/7 G1/7 E3/14 B1/7 G3/14 D,1/7 B1/7 ^G1/7 F3/14 A1/7 F3/14 D,1/7 B1/7 ^G1/7 F3/14 A1/7 F3/14 | E,1/7 d1/7 _B1/7 ^F3/14 ^c1/7 A3/14 E,1/7 d1/7 _B1/7 ^F3/14 ^c1/7 G3/14 D,1/7 e1/7 _B1/7 G3/14 d1/7 A3/14 D,1/7 e1/7 _B1/7 G3/14 d1/7 ^F3/14 | C1/7 e1/7 A1/7 ^F3/14 d1/7 A1/7 ^F3/14 e1/7 A1/7 ^F3/14 d1/7 A1/7 ^F3/14 e1/7 A1/7 ^F3/14 d1/7 A3/14 A,1/7 e1/7 c1/7 ^F3/14 d1/7 c3/14 | F,1/7 e1/7 c1/7 A3/14 d1/7 c3/14 ^F,1/7 e1/7 c1/7 A3/14 d1/7 c3/14 B,1/7 e1/7 c1/7 ^F3/14 d1/7 c1/7 ^F3/14 e1/7 c3/14 A,1/7 d1/7 c3/14 | A,1/7 e1/7 c1/7 ^F3/14 d1/7 c1/7 ^F3/14 e1/7 c1/7 ^F3/14 d1/7 c1/7 ^F3/14 e1/7 c1/7 ^F3/14 d1/7 c3/14 ^F,1/7 e1/7 c1/7 A3/14 d1/7 c3/14 | D,1/7 e1/7 c1/7 ^F3/14 d1/7 c3/14 E,1/7 e1/7 c1/7 G3/14 d1/7 G3/14 G,1/7 e1/7 c1/7 A3/14 d1/7 c1/7 A3/14 e1/7 c3/14 ^F,1/7 d1/7 A3/14 | ^F,1/7 e1/7 c1/7 A3/14 d1/7 c1/7 A3/14 E1/7 C1/7 A,3/14 D1/7 C1/7 A,3/14 E1/7 C1/7 A,3/14 D1/7 C3/14 D,1/7 E1/7 C1/7 A,3/14 D1/7 C3/14 | D,1/7 E1/7 C1/7 ^F,3/14 D1/7 C3/14 ^F,1/7 E1/7 C1/7 A,3/14 D1/7 C3/14 D,1/7 E1/7 C1/7 A,3/14 D1/7 C1/7 ^F,3/14 E1/7 C1/7 G3/14 ^F1/7 C3/14 | ^F,1/7 D1/7 C3/14 G,1/7 E1/7 C3/14 ^G,1/7 F1/7 C3/14 A,1/7 F1/7 C3/14 ^F,1/7 D1/7 C3/14 G,1/7 E1/7 C3/14 ^G,1/7 F1/7 C3/14 A,1/7 F1/7 C5/14 | A,1/4 G,1/4 ^F,1/4 E,1/4 D,1/4 C,1/4 B,1/4 A,1/4 A,1/4 G,1/4 ^F,1/4 E,1/4 D,1/4 C,1/4 B,1/4 A,1/4 | G1/7 G,1/7 D3/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/7 D,1/7 ^F1/7 A,5/7 | G,1/7 A1/7 D1/7 C4/7 D,1/7 G1/4 B1/4 d1/4 g1/4 b1/7 D,1/7 g1/7 d4/7 D,6/7 | G,1/7 c'1/7 g1/7 e3/14 b1/7 d3/14 D,1/7 c'1/7 e5/14 b1/7 d3/14 D,1/7 a1/7 c5/14 g1/7 B3/14 D,1/7 d1/7 B5/14 B1/7 G3/14 | A,1/7 G1/7 _E1/7 C5/7 A,5/7 g2/7 ^f1/7 D,1/7 d1/7 c4/7 D,6/7 | ^F,1/7 e1/7 c1/7 D,3/14 E5/14 ^F,1/7 E1/7 C1/7 A,3/14 E3/14 ^F2/7 E1/7 D,1/7 ^C1/7 ^A1/7 G,2/7 G,1/7 D1/7 B,5/7 | ^F,1/7 e1/7 c1/7 A,3/14 E5/14 A,1/7 E1/7 C5/14 E3/14 ^F2/7 E1/7 G,1/7 ^C1/7 B,3/7 G,1/7 D1/7 B,5/7 | C,1/7 g1/7 _e1/7 A,3/14 G5/14 C,1/7 G1/7 _E5/14 G1/7 _E3/14 ^C,1/7 G1/7 E1/7 A,3/14 ^F1/7 E3/14 ^C,1/7 A1/7 E5/14 G1/7 E3/14 | D,1/7 G1/7 C1/7 A,3/14 ^F5/14 D,1/7 E1/7 C1/7 ^F,3/14 D1/7 C3/14 D,1/7 c1/7 ^F1/7 A,5/7 A1/4 ^F1/4 C1/4 A,1/4 | G1/7 G,1/7 D3/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/7 D,1/7 ^F1/7 A,5/7 | G,1/7 A1/7 D1/7 C4/7 D,1/7 G1/4 B1/4 d1/4 g1/4 b1/7 D,1/7 g1/7 d4/7 D,6/7 | G,1/7 c'1/7 g1/7 e3/14 b1/7 d3/14 D,1/7 c'1/7 e5/14 b1/7 d3/14 D,1/7 a1/7 c5/14 g1/7 B3/14 D,1/7 d1/7 B5/14 B1/7 G3/14 | A,1/7 G1/7 _E1/7 C5/7 A,5/7 g2/7 ^f1/7 D,1/7 d1/7 c4/7 D,6/7 | ^F,1/7 e1/7 c1/7 A3/14 E5/14 ^F,1/7 E1/7 C1/7 A,3/14 E3/14 ^F2/7 E1/7 D,1/7 ^C1/7 ^A1/7 G,2/7 G,1/7 D1/7 B,5/7 | A,1/7 e1/7 c5/14 E5/14 A,1/7 E1/7 C5/14 E3/14 ^F2/7 E1/7 G,1/7 ^C1/7 B,3/7 G,1/7 D1/7 B,5/7 | C,1/7 G1/7 _E3/14 C,1/7 _E1/4 G1/4 A1/4 G1/4 _e6/7 D,1/7 D1/4 ^F1/4 A1/4 ^F1/4 | d6/7 D,1/7 G1/4 B1/4 d1/4 G1/4 g1/1 G,1/2 G,5/14 | D,1/7 B,1/7 ^F,5/14 B,1/2 B,5/14 ^F,1/7 ^C1/7 ^A,3/14 D,1/7 D1/7 B,1/7 ^F,3/14 D1/7 B,5/14 D1/7 B,3/14 ^F,1/7 E1/7 ^C3/14 | D,1/7 ^F1/7 D1/7 B,3/14 ^F1/7 D5/14 ^F1/7 D3/14 ^F,1/7 G1/7 E3/14 D,1/7 ^F1/7 D1/7 B,3/14 ^F1/7 D5/14 ^F1/7 D3/14 ^F,1/7 E1/7 ^C3/14 | D,1/7 D1/7 B,1/7 ^F,3/14 D1/7 B,5/14 D1/7 B,3/14 ^F,1/7 E1/7 ^C3/14 D,1/7 D1/7 B,1/7 ^F,3/14 D1/7 B,5/14 D,1/7 B,3/14 ^F,1/7 ^C1/7 ^A,3/14 | D,1/7 B,1/7 ^F,5/14 B,1/2 B,5/14 ^F,1/7 ^C1/7 ^A,3/14 D,1/7 B,1/7 ^F,5/14 B,1/2 B,5/14 ^F,1/7 ^C1/7 ^A3/14 | D,1/7 B,1/7 ^F,5/14 B,1/2 B,5/14 ^F,1/7 ^C1/7 ^A,3/14 D,1/7 D1/7 B,1/7 ^F,3/14 D1/7 B,5/14 D1/7 B,3/14 ^F,1/7 E1/7 ^C3/14 | D,1/7 ^F1/7 D1/7 B,3/14 ^F1/7 D5/14 ^F1/7 D3/14 ^F,1/7 G1/7 E3/14 D,1/7 ^F1/7 D1/7 B,3/14 ^F1/7 D5/14 ^F1/7 D3/14 ^F,1/7 E1/7 ^C3/14 | D,1/7 D1/7 B,1/7 ^F,3/14 D1/7 B,5/14 D1/7 B,3/14 ^F,1/7 E1/7 ^C3/14 D,1/7 D1/7 B,1/7 ^F,3/14 D1/7 B,5/14 D,1/7 B,3/14 ^F,1/7 ^C1/7 ^A,3/14 | D,1/7 B,1/7 ^F,5/14 B,1/2 B,5/14 ^F,1/7 ^C1/7 ^A,3/14 D,1/7 B,1/7 ^F,5/14 D1/7 B,5/14 ^F1/7 D3/14 ^F,1/7 ^A1/7 ^C3/14 | D,1/7 B1/7 D1/7 ^F,3/14 B1/7 D5/14 B1/7 D3/14 ^F,1/7 ^c1/7 ^A3/14 D,1/7 d1/7 B1/7 ^F,3/14 d1/7 B5/14 d1/7 B3/14 ^F,1/7 e1/7 ^c3/14 | D,1/7 ^f1/7 d1/7 B,3/14 ^f1/7 d5/14 ^f1/7 d3/14 ^F,1/7 g1/7 e3/14 D,1/7 ^f1/7 d1/7 B,3/14 ^f1/7 d5/14 ^f1/7 d3/14 ^F,1/7 e1/7 ^c3/14 | D,1/7 d1/7 B1/7 ^F,3/14 d1/7 B5/14 d1/7 B3/14 ^F,1/7 e1/7 ^c3/14 D,1/7 d1/7 B1/7 ^F,3/14 d1/7 B5/14 d1/7 B3/14 ^F,1/7 ^c1/7 ^A3/14 | D,1/7 B1/7 B,1/7 ^F,3/14 B1/2 B5/14 ^F,1/7 ^c1/7 ^A3/14 D,1/7 B1/7 B,1/7 ^F,3/14 B1/2 B5/14 ^F,1/7 ^c1/7 ^A3/14 | D,1/7 B1/7 B,1/7 ^F,3/14 B1/2 B5/14 ^F,1/7 ^c1/7 ^A3/14 D,1/7 d1/7 B1/7 B,1/7 ^F,3/14 d1/7 B5/14 d1/7 B3/14 ^F,1/7 e1/7 ^c3/14 | D,1/7 ^f1/7 d1/7 B,3/14 ^f1/7 d5/14 ^f1/7 d3/14 ^F,1/7 g1/7 e3/14 D,1/7 ^f1/7 d1/7 B,3/14 ^f1/7 d5/14 ^f1/7 d3/14 ^F,1/7 e1/7 ^c3/14 | D,1/7 d1/7 B1/7 ^F,3/14 d1/7 B5/14 d1/7 B3/14 ^F,1/7 e1/7 ^c3/14 D,1/7 d1/7 B1/7 ^F,3/14 d1/7 B5/14 d1/7 B3/14 ^F,1/7 ^c1/7 ^A3/14 | D,1/7 B1/7 B,1/7 ^F,3/14 B1/2 B5/14 ^F,1/7 ^c1/7 ^A3/14 D,1/7 B1/7 B,1/7 ^F,3/14 B1/2 B5/14 ^F,1/7 ^c1/7 ^A3/14 | D,1/7 B1/7 B,1/7 ^F,3/14 B1/2 B5/14 ^F,1/7 ^c1/7 ^A3/14 D,1/7 B1/7 B,1/7 ^F,3/14 B1/2 B5/14 ^F,1/7 ^c1/7 ^A3/14 | D,1/7 d1/7 B,1/7 ^F,3/14 d5/14 ^F,1/7 ^A1/7 E5/14 ^c5/14 B,1/7 B1/7 ^F1/7 D3/14 d5/14 ^F,1/7 ^A1/7 E5/14 ^c5/14 | B,1/7 B1/7 ^F1/7 D3/14 d5/14 ^f1/7 ^A1/7 E5/14 ^c5/14 b1/7 B1/7 ^F1/7 D3/14 d1/2 B1/7 ^F1/7 D3/14 A1/7 C3/14 | G,1/7 G1/7 D1/7 B,3/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/7 D,1/7 ^F1/7 A,5/7 | G,1/7 A1/7 D1/7 C4/7 D,1/7 G1/4 B1/4 d1/4 g1/4 b1/7 D,1/7 g1/7 d4/7 D,6/7 | G,1/7 c'1/7 g1/7 e3/14 b1/7 d3/14 D,1/7 c'1/7 e5/14 b1/7 d3/14 D,1/7 a1/7 c5/14 g1/7 B3/14 D,1/7 d1/7 B5/14 B1/7 G3/14 | A,1/7 G1/7 _E1/7 C5/7 A,5/7 g2/7 ^f1/7 D,1/7 d1/7 c4/7 D,6/7 | ^F,1/7 e1/7 c1/7 A3/14 E5/14 ^F,1/7 E1/7 C1/7 A,3/14 E3/14 ^F2/7 E1/7 D,1/7 ^C1/7 ^A1/7 G,2/7 G,1/7 D1/7 B,5/7 | ^F,1/7 e1/7 c1/7 A,3/14 E5/14 A,1/7 E1/7 C5/14 E3/14 ^F2/7 E1/7 G,1/7 ^C1/7 B,3/7 G,1/7 D1/7 B,5/7 | C,1/7 g1/7 _e1/7 A,3/14 G5/14 C,1/7 G1/7 _E5/14 G1/7 _E3/14 ^C,1/7 G1/7 E1/7 A,3/14 ^F1/7 E3/14 ^C,1/7 A1/7 E5/14 G1/7 E3/14 | D,1/7 G1/7 C1/7 A,3/14 ^F5/14 D,1/7 E1/7 C1/7 ^F,3/14 D1/7 C3/14 D,1/7 c1/7 ^F1/7 A,5/7 A1/4 ^F1/4 C1/4 A,1/4 | G1/7 G,1/7 D3/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/14 ^F1/7 A,3/14 D,1/7 G1/7 B,5/7 D,1/7 ^F1/7 A,5/7 | G,1/7 A1/7 D1/7 C4/7 D,1/7 G1/4 B1/4 d1/4 g1/4 b1/7 D,1/7 g1/7 d4/7 D,6/7 | G,1/7 c'1/7 g1/7 e3/14 b1/7 d3/14 D,1/7 c'1/7 e5/14 b1/7 d3/14 D,1/7 a1/7 c5/14 g1/7 B3/14 D,1/7 d1/7 B5/14 B1/7 G3/14 | A,1/7 G1/7 _E1/7 C5/7 A,5/7 g2/7 ^f1/7 D,1/7 d1/7 c4/7 D,6/7 | ^F,1/7 e1/7 c1/7 A3/14 E5/14 ^F,1/7 E1/7 C1/7 A,3/14 E3/14 ^F2/7 E1/7 D,1/7 ^C1/7 ^A1/7 G,2/7 G,1/7 D1/7 B,5/7 | A,1/7 e1/7 c5/14 E5/14 A,1/7 E1/7 C5/14 E3/14 ^F2/7 E1/7 ^C1/7 B,4/7 G,1/7 D1/7 B,5/7 | C,1/7 G1/7 _E3/14 C,1/7 _E1/4 G1/4 A1/4 G1/4 _e6/7 D,1/7 D1/4 ^F1/4 A1/4 ^F1/4 | d6/7 D,1/7 G1/4 B1/4 d1/4 G1/4 g1/1 G,1/1 | \n";
      assertEquals(expected, piece.toString());

  }
}
