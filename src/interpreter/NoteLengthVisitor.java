package interpreter;

import java.util.List;
import java.util.ArrayList;

import interpreter.MusicalElement.MusicalElementVisitor;

/**
 * The note length visitor traverses the AST and keeps track of the denominators 
 * of all notes it visits. Then, it computes the least common multiple of these 
 * values, producing the number of ticks per default note length for the Piece. 
 * This is done so that each Note in the Piece produces an integer number of ticks, 
 * meaning we take into account how finely every note subdivides a default note length. 
 * This is done because of the requirement in the java MIDI sequencer that all notes 
 * be scheduled at integer time steps, called ticks.
 */
public class NoteLengthVisitor implements MusicalElementVisitor<Void> {
    
    private final Piece piece;
    private List<Integer> denominators;
    
    public NoteLengthVisitor(Piece piece) {
        this.piece = piece;
    }
    
    private static int gcd(int a, int b)
    {
        while (b > 0)
        {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
    
    private static int lcm(int a, int b)
    {
        return a * (b / gcd(a, b));
    }

    private static int lcm(List<Integer> input)
    {
        int result = input.get(0);
        for(Integer i: input)
            result = lcm(result, i);
        return result;
    }
    
    public int computeTicksPerBeat() {
        this.denominators = new ArrayList<Integer>();
        this.piece.accept(this);
        if (this.denominators.size() < 1)
            return 1;
        else
            return lcm(this.denominators);
    }
    
    public Void visit(Piece piece) {
        for (Voice voice : piece.getVoices())
            voice.accept(this);
        return null;
    }
    
    public Void visit(Voice voice) {
        for (Measure measure : voice.getMeasures())
            measure.accept(this);
        return null;
    }
    
    public Void visit(Measure measure) {
        for (Chord chord : measure.getChords())
            chord.accept(this);
        return null;
    }
    
    public Void visit(Chord chord) {
        for (Note note : chord.getNotes())
            note.accept(this);
        return null;
    }
    
    public Void visit(Note note) {
        this.denominators.add(note.getLength().getDenominator());
        return null;
    }
    
}
