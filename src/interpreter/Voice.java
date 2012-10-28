package interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Voice implements MusicalElement {
	
    private String name;
    private final List<Measure> measures;
	
    public Voice() {
		this.measures = new ArrayList<Measure>();
	}
	
    public Voice(Measure measure) {
        this.measures = new ArrayList<Measure>(Arrays.asList(measure));
    }
    
    public Voice(List<Measure> measures) {
		this.measures = measures;
	}
    
    public void addMeasure(Measure measure) {
        measures.add(measure);
	}
	
    public void addMeasures(List<Measure> measureList) {
		for (Measure measure : measureList)
		    measures.add(measure);
	}
	
    public List<Measure> getMeasures() {
		return measures;
	}
	
    public String getName() {
        return this.name;
    }
    
    public void setName(String newName) {
        this.name = newName;
    }
    
    public <R> R accept(MusicalElementVisitor<R> visitor) {
        return visitor.visit(this);
    }
    
    @Override
	public String toString() {
		StringBuilder s = new StringBuilder("Voice: " + this.getName() + "\n");
		for (Measure measure : this.measures)
			s.append(measure.toString());
		return s.toString();
	}
    
}
