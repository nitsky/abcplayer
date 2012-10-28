package interpreter;

import java.util.ArrayList;
import java.util.List;

public class Measure implements MusicalElement {
    
    public static enum RepeatType {
        NONE,
        BEGIN,
        FIRST_ENDING,
        SECOND_ENDING,
        END,
        SECTION_END
    }
    
    private String name;
    private RepeatType repeatType = RepeatType.NONE;
    private final List<Chord> chords;
    
    public Measure() {
        this.chords = new ArrayList<Chord>();
    }
    
    public Measure(List<Chord> chords) {
        this.chords = chords;
    }
    
    public void addChord(Chord chord) {
        chords.add(chord);
    }
    
    public void addChords(List<Chord> chordList) {
        for (Chord chord : chordList)
            chords.add(chord);
    }
    
    public List<Chord> getChords() {
        return chords;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String newName) {
        this.name = newName;
    }
    
    public RepeatType getRepeatType() {
        return this.repeatType;
    }
    
    public void setRepeatType(RepeatType newRepeatType) {
        this.repeatType = newRepeatType;
    }
    
    public Meter getLength() {
        Meter length = new Meter(0,1);
        for (Chord chord : this.chords) {
            length = length.add(chord.getLength());
        }
        return length;
    }
    
    public <R> R accept(MusicalElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("");
        if (this.repeatType == RepeatType.FIRST_ENDING)
            s.append("[1 ");
        else if (this.repeatType == RepeatType.SECOND_ENDING)
            s.append("[2 ");
        else if (this.repeatType == RepeatType.BEGIN)
            s.append(": ");
        for (Chord chord : this.chords)
            s.append(chord.toString());
        if (this.repeatType == RepeatType.FIRST_ENDING || this.repeatType == RepeatType.END)
            s.append(":| ");
        else if (this.repeatType == RepeatType.SECTION_END)
            s.append("|] ");
        else
            s.append("| ");
        return s.toString();
    }
    
}
