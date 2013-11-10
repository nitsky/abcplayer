package sound;

import java.text.MessageFormat;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

/**
 * Schedules and plays a sequence of notes at given time steps (or "ticks")
 */
public class SequencePlayer {

    private Sequencer sequencer;
    private Track track;
    private int beatsPerMinute;

    private static int DEFAULT_CHANNEL = 0;    // midi channel - for our purpose always 0
    private static int DEFAULT_VELOCITY = 100; // the volume

    private void checkRep() {
        assert sequencer != null : "sequencer should be non-null";
        assert track != null : "track should be non-null";
        assert beatsPerMinute >= 0 : "should be positive number of beats per minute";
    }

    /**
     * @param beatsPerMinute
     *            : the number of beats per minute, where each beat is equal to
     *            a quarter note in duration
     * @param ticksPerQuarterNote
     *            : the number of ticks per quarter note
     * @throws MidiUnavailableException
     * @throws InvalidMidiDataException
     */
    public SequencePlayer(int beatsPerMinute, int ticksPerQuarterNote)
            throws MidiUnavailableException, InvalidMidiDataException {
        this.sequencer = MidiSystem.getSequencer();

        // Create a sequence object with with tempo-based timing, where
        // the resolution of the time step is based on ticks per quarter note
        Sequence sequence = new Sequence(Sequence.PPQ, ticksPerQuarterNote);
        this.beatsPerMinute = beatsPerMinute;

        // Create an empty track. Notes will be added to this track.
        this.track = sequence.createTrack();

        sequencer.setSequence(sequence);

        checkRep();
    }

    /**
     * @param eventType
     * @param note
     * @param tick
     * @pre eventType is a valid MidiMessage type in ShortMessage && note is a
     *      valid pitch value && tick => 0
     */
    private void addMidiEvent(int eventType, int note, int tick) throws InvalidMidiDataException {
        ShortMessage msg = new ShortMessage();
        msg.setMessage(eventType, DEFAULT_CHANNEL, note, DEFAULT_VELOCITY);
        MidiEvent event = new MidiEvent(msg, tick);
        this.track.add(event);
    }

    /**
     * @param note
     *            : the pitch value for the note to be played
     * @param startTick
     *            : the starting tick
     * @param numTicks
     *            : the number of ticks for which this note should be played
     * @pre note is a valid pitch value && startTick >= 0 && numTicks >= 0
     * @post schedule the note to be played starting at startTick for the
     *       duration of numTicks
     */
    public void addNote(int note, int startTick, int numTicks) {
        try {
            // schedule two events in the track, one for starting a note and
            // the other for ending the note.
            addMidiEvent(ShortMessage.NOTE_ON, note, startTick);
            addMidiEvent(ShortMessage.NOTE_OFF, note, startTick + numTicks);
        } catch (InvalidMidiDataException e) {
            String msg = MessageFormat.format("Cannot add note with the pitch {0} at tick {1} " +
            		"for duration of {2}", note, startTick, numTicks);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * @post the sequencer is opened to begin playing its track
     */
    public void play() throws MidiUnavailableException {
        sequencer.open();
        sequencer.setTempoInBPM(this.beatsPerMinute);

        // start playing!
        sequencer.start();

        // busy-wait until the sequencer stops playing - ugly, but couldn't
        // find a better way to do it
        while (sequencer.isRunning()) {
            Thread.yield();
        }

        // when done playing, close the sequencer
        sequencer.close();
    }

    /**
     * @post returns a string that displays the entire track information as a
     *       sequence of MIDI events, where each event is either turning on or
     *       off a note at a certain tick
     */
    @Override
    public String toString() {
        String trackInfo = "";
        
        for (int i = 0; i < track.size(); i++) {
            MidiEvent e = track.get(i);
            MidiMessage msg = e.getMessage();
            String msgString = "";

            if (msg instanceof javax.sound.midi.ShortMessage) {
                ShortMessage smg = ((ShortMessage) msg);
                int command = smg.getCommand();
                String commandType = "UnknownCommand";

                // determine the type of the command in this message
                if (command == ShortMessage.NOTE_OFF) {
                    commandType = "NOTE_OFF";
                } else if (command == ShortMessage.NOTE_ON) {
                    commandType = "NOTE_ON ";
                }

                msgString = "Event: " + commandType + " Pitch: " + smg.getData1() + " ";
            } else {
                msgString = "***** End of track *****  ";
            }

            trackInfo = trackInfo + msgString + " Tick: " + e.getTick() + "\n";
        }

        return trackInfo;
    }

}