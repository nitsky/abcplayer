package interpreter;

public class Note implements MusicalElement {
	
    private char pitch;
    private int octave;
	private int accidental;
	private Meter length;
	
	private static final int[] scale = {
        /* A */ 9,
        /* B */ 11,
        /* C */ 0,
        /* D */ 2,
        /* E */ 4,
        /* F */ 5,
        /* G */ 7,
    };
	
	public static final int OCTAVE = 12;
	
	public Note() {
	    this.octave = 0;
	    this.accidental = 0;
	    this.length = new Meter(1,1);
	}
	
	public Note(char pitch, int octave, int accidental, Meter length) {
		this.pitch = pitch;
		this.octave = octave;
		this.accidental = accidental;
		this.length = length;
	}
	
	public int midiValue() {
	    if (this.pitch != 'z')
	        return scale[this.pitch - 'A'] + this.accidental + (12 * this.octave) + 60;
	    else
	        throw new IllegalArgumentException("Cannot get midi value for rest");
    }
	
	private static final String[] valToString = {
        "C", null, "D", null, "E", "F", null, "G", null, "A", null, "B"
    };
	
	public char getPitch() {
		return this.pitch;
	}
	
	public void setPitch(char newPitch) {
	    this.pitch = newPitch;
	}
	
	public int getAccidental() {
		return this.accidental;
	}
	
	public void setAccidental(int newAccidental) {
	    this.accidental = newAccidental;
	}
	
	public Meter getLength() {
	    return this.length;
	}
	
	public void setLength(Meter newLength) {
	    this.length = newLength;
	}
	
	public int getOctave() {
	    return this.octave;
	}
	
	public void setOctave(int newOctave) {
	    this.octave = newOctave;
	}
	
	public boolean isRest() {
	    return this.pitch == 'z';
	}
	
	public <R> R accept(MusicalElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
	
    @Override
    public String toString() {
        String suffix = "";
        String prefix = "";
        int oct = octave;
        int acc = this.accidental;
        
        if (this.pitch != 'z') {
            while (oct < 0) {
                suffix += ",";
                oct++;
            }
            
            while (oct > 1) {
                suffix += "'";
                oct--;
            }
        
            while (acc < 0) {
                prefix += "_";
                acc++;
            }
                
            while (acc > 0) {
                prefix += "^";
                acc--;
            }
        }
        
        String name;
        if (this.pitch != 'z')
            name = valToString[scale[this.pitch - 'A']];
        else
            name = "z";
        if (oct == 1) name = name.toLowerCase();

        return prefix + name + suffix + this.length.toString();
    }
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Note))
			return false;
		Note otherNote = (Note)other;
		return this.pitch == otherNote.getPitch() && this.accidental == otherNote.getAccidental() &&
		        this.octave == otherNote.getOctave() && this.length.equals(otherNote.getLength());
	}
	
}
