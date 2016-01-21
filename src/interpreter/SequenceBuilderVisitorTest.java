package interpreter;

import java.util.List;
import java.util.ArrayList;

//import static org.junit.Assert.*;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.Test;

import sound.SequencePlayer;

public class SequenceBuilderVisitorTest {

  // test to verify the sequence builder visitor can
  // produce a sequence player from a simple tune
  @Test
  public void sequenceBuilderVisitorSimpleTest() {

    Voice voice = new Voice();
    Measure measure;

    measure = new Measure();
    measure.addChord(new Chord(new Note('C',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('C',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('G',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('G',0,0,new Meter(1,1))));
    voice.addMeasure(measure);

    measure = new Measure();
    measure.addChord(new Chord(new Note('A',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('A',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('G',0,0,new Meter(2,1))));
    voice.addMeasure(measure);

    measure = new Measure();
    measure.addChord(new Chord(new Note('F',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('F',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('E',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('E',0,0,new Meter(1,1))));
    voice.addMeasure(measure);

    measure = new Measure();
    measure.addChord(new Chord(new Note('D',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('D',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('C',0,0,new Meter(2,1))));
    voice.addMeasure(measure);

    Piece piece = new Piece(voice, "Twinkle Twinkle Little Star", 120);

    NoteLengthVisitor noteLengthVisitor = new NoteLengthVisitor(piece);
    int ticksPerBeat = noteLengthVisitor.computeTicksPerBeat();

    try {
      SequencePlayer player = new SequencePlayer(piece.getDefaultNoteLengthsPerMinute(), ticksPerBeat);
      SequenceBuilderVisitor sequenceBuilderVisitor = new SequenceBuilderVisitor(player, 1);
      piece.accept(sequenceBuilderVisitor);
      player.play();
    } catch (MidiUnavailableException e) {
      System.out.println("Error playing file, got MidiUnavailableException");
    } catch (InvalidMidiDataException e) {
      System.out.println("Error playing file, got InvalidMidiDataException");
    }

  }

  // test to verify the sequence builder visitor can
  // correctly playback two simple voices
  @Test
  public void sequenceBuilderVisitorMultipleVoiceTest() {

    List<Voice> voices = new ArrayList<Voice>();
    Measure measure;

    Voice voiceMelody = new Voice();

    measure = new Measure();
    measure.addChord(new Chord(new Note('C',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('C',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('G',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('G',0,0,new Meter(1,1))));
    voiceMelody.addMeasure(measure);

    measure = new Measure();
    measure.addChord(new Chord(new Note('A',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('A',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('G',0,0,new Meter(2,1))));
    voiceMelody.addMeasure(measure);

    measure = new Measure();
    measure.addChord(new Chord(new Note('F',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('F',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('E',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('E',0,0,new Meter(1,1))));
    voiceMelody.addMeasure(measure);

    measure = new Measure();
    measure.addChord(new Chord(new Note('D',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('D',0,0,new Meter(1,1))));
    measure.addChord(new Chord(new Note('C',0,0,new Meter(2,1))));
    voiceMelody.addMeasure(measure);

    voices.add(voiceMelody);

    Voice voiceBass = new Voice();

    measure = new Measure();
    measure.addChord(new Chord(new Note('C',-1,0,new Meter(4,1))));
    voiceBass.addMeasure(measure);

    measure = new Measure();
    measure.addChord(new Chord(new Note('F',-1,0,new Meter(2,1))));
    measure.addChord(new Chord(new Note('C',-1,0,new Meter(2,1))));
    voiceBass.addMeasure(measure);

    measure = new Measure();
    measure.addChord(new Chord(new Note('F',-1,0,new Meter(2,1))));
    measure.addChord(new Chord(new Note('C',-1,0,new Meter(2,1))));
    voiceBass.addMeasure(measure);

    measure = new Measure();
    measure.addChord(new Chord(new Note('G',-2,0,new Meter(2,1))));
    measure.addChord(new Chord(new Note('C',-1,0,new Meter(2,1))));
    voiceBass.addMeasure(measure);

    voices.add(voiceBass);

    Piece piece = new Piece(voices, "Twinkle Twinkle Little Star", 120);

    NoteLengthVisitor noteLengthVisitor = new NoteLengthVisitor(piece);
    int ticksPerBeat = noteLengthVisitor.computeTicksPerBeat();

    try {
      SequencePlayer player = new SequencePlayer(piece.getDefaultNoteLengthsPerMinute(), ticksPerBeat);
      SequenceBuilderVisitor sequenceBuilderVisitor = new SequenceBuilderVisitor(player, 1);
      piece.accept(sequenceBuilderVisitor);
      player.play();
    } catch (MidiUnavailableException e) {
      System.out.println("Error playing file, got MidiUnavailableException");
    } catch (InvalidMidiDataException e) {
      System.out.println("Error playing file, got InvalidMidiDataException");
    }

  }

}
