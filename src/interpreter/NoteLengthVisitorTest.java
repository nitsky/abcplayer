package interpreter;

import static org.junit.Assert.*;
import org.junit.Test;

public class NoteLengthVisitorTest {

    // test to confirm note length visitor can handle
    // a bunch of notes with the same duration
    @Test
    public void noteLengthVisitorSimpleTest() {
        
        Voice voice = new Voice(new Measure());
        for (int i = 0; i < 10; i++)
            voice.getMeasures().get(0).addChord(new Chord(new Note('A',0,0, new Meter(2,1))));
        
        Piece piece = new Piece();
        piece.addVoice(voice);
        NoteLengthVisitor noteLengthVisitor = new NoteLengthVisitor(piece);
        assertEquals(noteLengthVisitor.computeTicksPerBeat(), 1);
        
    }
    
    // test to confirm note length visitor can handle
    // dotted 8th notes, 16th notes and triplets in combination,
    // that should mostly cover the functionality of the class
    @Test
    public void noteLengthVisitor16thAndTripletTest() {
        
        Voice voice = new Voice(new Measure());
        
        voice.getMeasures().get(0).addChord(new Chord(new Note('A',0,0,new Meter(3,4))));
        voice.getMeasures().get(0).addChord(new Chord(new Note('A',0,0,new Meter(1,4))));
        
        voice.getMeasures().get(0).addChord(new Chord(new Note('A',0,0,new Meter(1,3))));
        voice.getMeasures().get(0).addChord(new Chord(new Note('A',0,0,new Meter(1,3))));
        voice.getMeasures().get(0).addChord(new Chord(new Note('A',0,0,new Meter(1,3))));
        
        Piece piece = new Piece();
        piece.addVoice(voice);
        NoteLengthVisitor noteLengthVisitor = new NoteLengthVisitor(piece);
        assertEquals(noteLengthVisitor.computeTicksPerBeat(), 12);
        
    }

}
