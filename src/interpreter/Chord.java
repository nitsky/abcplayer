package interpreter;

import java.util.ArrayList;
import java.util.List;

public class Chord implements MusicalElement {
	
    private final List<Note> notes;
	
    public Chord() {
		this.notes = new ArrayList<Note>();
	}
    
    public Chord(Note note) {
        this.notes = new ArrayList<Note>();
        this.notes.add(note);
    }
	
    public Chord(List<Note> notes) {
		this.notes = notes;
	}
	
    public void addNote(Note note) {
		notes.add(note);
	}
	
    public void addNotes(List<Note> noteList) {
		for (Note note : noteList)
			notes.add(note);
	}
	
    public List<Note> getNotes() {
		return notes;
	}
    
    public Meter getLength() {
        Meter result = new Meter(0,1);
        for (Note note : this.notes)
            if (note.getLength().compute() > result.compute())
                result = note.getLength();
        return result;
    }
	
    public <R> R accept(MusicalElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
    
    @Override
	public String toString() {
		StringBuilder s = new StringBuilder("");
		if (this.notes.size() > 1)
            s.append("[");
		for (Note note : notes)
			s.append(note.toString() + " ");
		if (this.notes.size() > 1) {
		    s.delete(s.length()-1, s.length()); // remove the space from the last note in the chord
		    s.append("] ");
		}
		return s.toString();
	}

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Chord))
            return false;
        Chord otherChord = (Chord)other;
        return this.notes.equals(otherChord.getNotes());
    }
    
}
