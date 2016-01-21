package player;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import sound.SequencePlayer;

import interpreter.Util;

import interpreter.Lexer;
import interpreter.Parser;
import interpreter.NoteLengthVisitor;
import interpreter.SequenceBuilderVisitor;

import interpreter.Piece;

/**
 * Main entry point of your application.
 */
public class Main {

  /**
   * Plays the input file using Java MIDI API and displays
   * header information to the standard output stream.
   *
   * <p>Your code <b>should not</b> exit the application abnormally using
   * System.exit()</p>
   *
   * @param file the name of input abc file
   */
  public static void play(String file) {

    String input;

    try {
      input = Util.readStringFromFile(file);
    } catch (IOException e) {
      System.out.println("Unable to read contents of file " + file);
      return;
    }

    try {

      // create the lexer and use it as input to the parser
      Lexer lexer = new Lexer(input);
      Parser parser = new Parser(lexer);

      // run the parser to produce a piece object, the root of the AST
      Piece piece = parser.parse();

      // Walk through the AST to get the smallest number of
      // ticks per default note length
      NoteLengthVisitor noteLengthVisitor = new NoteLengthVisitor(piece);
      int ticksPerDefaultNoteLength = noteLengthVisitor.computeTicksPerBeat();

      SequencePlayer player = new SequencePlayer(piece.getDefaultNoteLengthsPerMinute(), ticksPerDefaultNoteLength);
      SequenceBuilderVisitor sequenceBuilderVisitor = new SequenceBuilderVisitor(player, ticksPerDefaultNoteLength);
      piece.accept(sequenceBuilderVisitor);
      System.out.println(piece);
      player.play();

    } catch (MidiUnavailableException e) {
      System.out.println("Error playing file, MIDI playback is unavailable");
    } catch (InvalidMidiDataException e) {
      System.out.println("Error playing file, invalid MIDI data");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }

    return;
  }

  /**
   * Main function that takes a single command line argument, a path to an abc file,
   * and plays it back
   * @param args
   */
  public static void main(String[] args) {

    if (args.length != 1) {
      System.out.println("Invalid number of arguments. Please input a path to a file in ABC format.");
      return;
    }

    play(args[0]);

  }

}
