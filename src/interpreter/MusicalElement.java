package interpreter;

public interface MusicalElement {
  public interface MusicalElementVisitor<R> {
    public R visit(Piece piece);
    public R visit(Voice voice);
    public R visit(Measure measure);
    public R visit(Chord chord);
    public R visit(Note note);
  }
  public <R> R accept(MusicalElementVisitor<R> visitor);
}
