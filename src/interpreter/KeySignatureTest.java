package interpreter;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class KeySignatureTest {

  /*
  * Test to confirm that there are no errors in keysignature.xml
  * by applying each key signature to all notes in the scale
  * and confirming that the result is the corresponding major or
  * minor scale.
  */

  @Test
  public void KeySignatureTestAll() {

    String[] keys = new String[] {
      "C", "G", "D", "A", "E", "B", "F#", "C#",
      "F", "Bb", "Eb", "Ab", "Db", "Gb", "Cb",
      "Am", "Em", "Bm", "F#m", "C#m", "G#m", "D#m", "A#m",
      "Dm", "Gm", "Cm", "Fm", "Bbm", "Ebm", "Abm"
    };

    List<Character> notes = new ArrayList<Character>(Arrays.asList(
    'C', 'D', 'E', 'F', 'G', 'A', 'B'
    ));

    for (String key : keys){

      KeySignature keySignature = new KeySignature(key);
      int start = notes.indexOf(key.charAt(0));
      List<Note> scale = new ArrayList<Note>();

      for (int i = start; i < start+7; i++) {
        Note note;
        if (i < 7) {
          note = new Note(notes.get(i), 0, 0, new Meter(1, 1));
        }
        else {
          note = new Note(notes.get(i-7), 1, 0, new Meter(1, 1));
        }
        keySignature.processNote(note);
        scale.add(note);
      }

      if (key.charAt(key.length() - 1) == 'm') {
        assertMinorScale(scale);
      }
      else {
        assertMajorScale(scale);
      }

    }

  }

  public void assertMajorScale(List<Note> scale) {
    int[] steps = {2, 2, 1, 2, 2, 2};
    for (int i = 0; i < steps.length; i++) {
      assertEquals(steps[i], noteDifference(scale.get(i), scale.get(i+1)));
    }
  }

  public void assertMinorScale(List<Note> scale) {
    int[] steps = {2, 1, 2, 2, 1, 2};
    for (int i = 0; i < steps.length; i++) {
      assertEquals(steps[i], noteDifference(scale.get(i), scale.get(i+1)));
    }
  }

  public static int noteDifference(Note a, Note b) {
    return b.midiValue() - a.midiValue();
  }

}
