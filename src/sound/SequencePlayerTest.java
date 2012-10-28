package sound;

import static org.junit.Assert.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import org.junit.Test;

public class SequencePlayerTest {

    @Test
    public void testPiece1() {
        
        SequencePlayer player;
        try {
            
            player = new SequencePlayer(120, 12);
            int i = 0;
            
            player.addNote(new Pitch('C').toMidiNote(), (i += 12) - 12, 12);
            player.addNote(new Pitch('C').toMidiNote(), (i += 12) - 12, 12);
            player.addNote(new Pitch('C').toMidiNote(), (i += 9) - 9, 9);
            player.addNote(new Pitch('D').toMidiNote(), (i += 3) - 3, 3);
            player.addNote(new Pitch('E').toMidiNote(), (i += 12) - 12, 12);
            
            player.addNote(new Pitch('E').toMidiNote(), (i += 9) - 9, 9);
            player.addNote(new Pitch('D').toMidiNote(), (i += 3) - 3, 3);
            player.addNote(new Pitch('E').toMidiNote(), (i += 9) - 9, 9);
            player.addNote(new Pitch('F').toMidiNote(), (i += 3) - 3, 3);
            player.addNote(new Pitch('G').toMidiNote(), (i += 24) - 24, 24);
            
            player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), (i += 4) - 4, 4);
            player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), (i += 4) - 4, 4);
            player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), (i += 4) - 4, 4);
            player.addNote(new Pitch('G').toMidiNote(), (i += 4) - 4, 4);
            player.addNote(new Pitch('G').toMidiNote(), (i += 4) - 4, 4);
            player.addNote(new Pitch('G').toMidiNote(), (i += 4) - 4, 4);
            player.addNote(new Pitch('E').toMidiNote(), (i += 4) - 4, 4);
            player.addNote(new Pitch('E').toMidiNote(), (i += 4) - 4, 4);
            player.addNote(new Pitch('E').toMidiNote(), (i += 4) - 4, 4);
            player.addNote(new Pitch('C').toMidiNote(), (i += 4) - 4, 4);
            player.addNote(new Pitch('C').toMidiNote(), (i += 4) - 4, 4);
            player.addNote(new Pitch('C').toMidiNote(), (i += 4) - 4, 4);
            
            player.addNote(new Pitch('G').toMidiNote(), (i += 9) - 9, 9);
            player.addNote(new Pitch('F').toMidiNote(), (i += 3) - 3, 3);
            player.addNote(new Pitch('E').toMidiNote(), (i += 9) - 9, 9);
            player.addNote(new Pitch('D').toMidiNote(), (i += 3) - 3, 3);
            player.addNote(new Pitch('C').toMidiNote(), (i += 24) - 24, 24);
            
            player.play();

        } catch (MidiUnavailableException e) {
            fail("Got MidiUnavailableException");
        } catch (InvalidMidiDataException e) {
            fail("Got InvalidMidiDataException");
        }
        
    }
    
    @Test
    public void testPiece2() {
        SequencePlayer player;
        try {
            
            player = new SequencePlayer(200, 12);
            int i = 0;
            
            player.addNote(new Pitch('F').transpose(1).toMidiNote(), i, 6);
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), (i += 6) - 6, 6);
            player.addNote(new Pitch('F').transpose(1).toMidiNote(), i, 6);
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), (i += 6) - 6, 6);
            i += 6;
            player.addNote(new Pitch('F').transpose(1).toMidiNote(), i, 6);
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), (i += 6) - 6, 6);
            i += 6;
            player.addNote(new Pitch('F').transpose(1).toMidiNote(), i, 6);
            player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), (i += 6) - 6, 6);
            player.addNote(new Pitch('F').transpose(1).toMidiNote(), i, 12);
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), (i += 12) - 12, 12);
            
            player.addNote(new Pitch('G').toMidiNote(), i, 12);
            player.addNote(new Pitch('B').toMidiNote(), i, 12);
            player.addNote(new Pitch('G').transpose(Pitch.OCTAVE).toMidiNote(), (i += 12) - 12, 12);
            i += 12;
            player.addNote(new Pitch('G').toMidiNote(), (i += 12) - 12, 12);
            i += 12;
            
            player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), (i += 18) - 18, 18);
            player.addNote(new Pitch('G').toMidiNote(), (i += 6) - 6, 6);
            i += 12;
            player.addNote(new Pitch('E').toMidiNote(), (i += 12) - 12, 12);
            
            player.addNote(new Pitch('E').toMidiNote(), (i += 6) - 6, 6);
            player.addNote(new Pitch('A').toMidiNote(), (i += 12) - 12, 12);
            player.addNote(new Pitch('B').toMidiNote(), (i += 12) - 12, 12);
            player.addNote(new Pitch('B').transpose(-1).toMidiNote(), (i += 6) - 6, 6);
            player.addNote(new Pitch('A').toMidiNote(), (i += 12) - 12, 12);
            
            player.addNote(new Pitch('G').toMidiNote(), (i += 8) - 8, 8);
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), (i += 8) - 8, 8);
            player.addNote(new Pitch('G').transpose(Pitch.OCTAVE).toMidiNote(), (i += 8) - 8, 8);
            player.addNote(new Pitch('A').transpose(Pitch.OCTAVE).toMidiNote(), (i += 12) - 12, 12);
            player.addNote(new Pitch('F').transpose(Pitch.OCTAVE).toMidiNote(), (i += 6) - 6, 6);
            player.addNote(new Pitch('G').transpose(Pitch.OCTAVE).toMidiNote(), (i += 6) - 6, 6);
            
            i += 6;
            player.addNote(new Pitch('E').transpose(Pitch.OCTAVE).toMidiNote(), (i += 12) - 12, 12);
            player.addNote(new Pitch('C').transpose(Pitch.OCTAVE).toMidiNote(), (i += 6) - 6, 6);
            player.addNote(new Pitch('D').transpose(Pitch.OCTAVE).toMidiNote(), (i += 6) - 6, 6);
            player.addNote(new Pitch('B').toMidiNote(), (i += 9) - 9, 9);
            i += 9;
            
            player.play();

        } catch (MidiUnavailableException e) {
            fail("Got MidiUnavailableException");
        } catch (InvalidMidiDataException e) {
            fail("Got InvalidMidiDataException");
        }
        
    }

}
