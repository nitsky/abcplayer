package interpreter;

import java.util.ArrayList;
import java.util.List;

public class Piece implements MusicalElement {

  private final List<Voice> voices;

  private int     index;
  private String  title;
  private String  composer;
  private Meter   timeSignature;
  private int     defaultNoteLengthsPerMinute;
  private Meter   defaultNoteLength;
  private KeySignature keySignature;

  public Piece() {
    this.voices = new ArrayList<Voice>();
  }

  public Piece(String title, int defaultNoteLengthsPerMinute) {
    this.voices = new ArrayList<Voice>();
    this.title = title;
    this.defaultNoteLengthsPerMinute = defaultNoteLengthsPerMinute;
  }

  public Piece(Voice voice) {
    this.voices = new ArrayList<Voice>();
    this.voices.add(voice);
  }

  public Piece(Voice voice, String title, int defaultNoteLengthsPerMinute) {
    this.voices = new ArrayList<Voice>();
    this.voices.add(voice);
    this.title = title;
    this.defaultNoteLengthsPerMinute = defaultNoteLengthsPerMinute;
  }

  public Piece(List<Voice> voices) {
    this.voices = voices;
  }

  public Piece(List<Voice> voices, String title, int defaultNoteLengthsPerMinute) {
    this.voices = voices;
    this.title = title;
    this.defaultNoteLengthsPerMinute = defaultNoteLengthsPerMinute;
  }

  public void addVoice(Voice voice) {
    this.voices.add(voice);
  }

  public void addVoices(List<Voice> voiceList) {
    for (Voice voice : voiceList)
      this.voices.add(voice);
  }

  public List<Voice> getVoices() {
    return this.voices;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String newTitle) {
    this.title = newTitle;
  }

  public String getComposer() {
    return this.composer;
  }

  public void setComposer(String newComposer) {
    this.composer = newComposer;
  }

  public Meter getTimeSignature() {
    return this.timeSignature;
  }

  public void setTimeSignature(Meter newTimeSignature) {
    this.timeSignature = newTimeSignature;
  }

  public KeySignature getKeySignature() {
    return this.keySignature;
  }

  public void setKeySignature(KeySignature newKeySignature) {
    this.keySignature = newKeySignature;
  }

  public int getDefaultNoteLengthsPerMinute() {
    return this.defaultNoteLengthsPerMinute;
  }

  public void setDefaultNoteLengthsPerMinute(int newDefaultNoteLengthsPerMinute) {
    this.defaultNoteLengthsPerMinute = newDefaultNoteLengthsPerMinute;
  }

  public Meter getDefaultNoteLength() {
    return this.defaultNoteLength;
  }

  public void setDefaultNoteLength(Meter newDefaultNoteLength) {
    this.defaultNoteLength = newDefaultNoteLength;
  }

  public int getIndex() {
    return this.index;
  }

  public void setIndex(int newIndex) {
    this.index = newIndex;
  }

  public <R> R accept(MusicalElementVisitor<R> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {

    StringBuilder s = new StringBuilder("");

    s.append("Index Number: " + this.index + "\n");
    s.append("Title: " + this.title + "\n");
    s.append("Composer: " + this.composer + "\n");
    s.append("Default Note Length: " + this.defaultNoteLength.toString() + "\n");
    s.append("Time Signature: " + this.timeSignature.toString() + "\n");
    s.append("Tempo: " + this.defaultNoteLengthsPerMinute + "\n");
    for (Voice voice : voices)
      s.append("Voice: " + voice.getName() + "\n");
    s.append("Key Signature: " + this.keySignature.toString() + "\n");

    for (Voice voice : voices)
      s.append(voice.toString() + "\n");

    return s.toString();

  }

}
