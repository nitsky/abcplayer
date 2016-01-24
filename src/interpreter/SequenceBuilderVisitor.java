package interpreter;

import sound.SequencePlayer;

import java.util.List;
import java.util.ArrayList;

import interpreter.Measure.RepeatType;
import interpreter.MusicalElement.MusicalElementVisitor;

public class SequenceBuilderVisitor implements MusicalElementVisitor<Integer> {

  private final SequencePlayer player;
  private final int ticksPerDefaultNoteLength;
  private int pos;

  public SequenceBuilderVisitor(SequencePlayer player, int ticksPerDefaultNoteLength) {
    this.pos = 0;
    this.ticksPerDefaultNoteLength = ticksPerDefaultNoteLength;
    this.player = player;
  }

  public Integer visit(Piece piece) {
    for (Voice voice : piece.getVoices()) {
      this.pos = 0;
      voice.accept(this);
    }
    return 0;
  }

  public Integer visit(Voice voice) {
    int i = 0;
    List<Integer> visitedEndings = new ArrayList<Integer>();
    while (i < voice.getMeasures().size()) {

      Measure measure = voice.getMeasures().get(i);

      // pass over a first ending if it has been visited
      if (measure.getRepeatType() == RepeatType.FIRST_ENDING) {
        if (visitedEndings.contains(i)) {
          i ++;
          continue;
        }
      }

      measure.accept(this);

      // if we hit a first ending for the first time, backtrack to the start of the repeat
      if (measure.getRepeatType() == RepeatType.FIRST_ENDING) {
        // backtrack until we get to the beginning of the piece
        // or a bar which marks where the repeat should return to
        visitedEndings.add(i);
        while (i > 0) {
          i--;
          if (voice.getMeasures().get(i).getRepeatType() == RepeatType.BEGIN) {
            break;
          }
          if (voice.getMeasures().get(i).getRepeatType() != RepeatType.NONE) {
            i++;
            break;
          }
        }
      }
      // similarly if we hit an end repeat, backtrack
      // if we have not yet visited it
      else if (measure.getRepeatType() == RepeatType.END)
        if (visitedEndings.contains(i)) {
          i++;
        } else {
          visitedEndings.add(i);
          while (i > 0) {
            i--;
            if (voice.getMeasures().get(i).getRepeatType() == RepeatType.BEGIN) {
              break;
            }
            if (voice.getMeasures().get(i).getRepeatType() != RepeatType.NONE) {
              i++;
              break;
            }
          }
        }
      else {
        // otherwise the measure is normal so just keep chugging along
        i++;
      }
    }
    return 0;
  }

  public Integer visit(Measure measure) {
    for (Chord chord : measure.getChords()) {
      pos += chord.accept(this);
    }
    return 0;
  }

  public Integer visit(Chord chord) {
    int duration = 0;
    for (Note note : chord.getNotes()) {
      duration = Math.max(duration, note.accept(this));
    }
    return duration;
  }

  public Integer visit(Note note) {
    int noteDuration = (int)Math.round(this.ticksPerDefaultNoteLength * note.getLength().compute());
    if (note.getPitch() != 'z') {
      player.addNote(note.midiValue(), pos, noteDuration);
    }
    return noteDuration;
  }

}
