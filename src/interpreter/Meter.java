package interpreter;

public class Meter {
    
    private int numerator;
    private int denominator;
    
    public Meter() {
        this.numerator = 0;
        this.denominator = 0;
    }
    
    public Meter(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    public Meter(String string) {
        String numString, demString;
        numString = string.replaceAll("[^/]*$","").replaceAll("/", "");
        demString = string.replaceAll("^[^/]*","").replaceAll("/", "");
        this.numerator = Integer.parseInt(numString);
        this.denominator = Integer.parseInt(demString);
    }
    
    public int getNumerator() {
        return this.numerator;
    }
    
    public int getDenominator() {
        return this.denominator;
    }
    
    public double compute() {
        return (double)this.numerator / (double)this.denominator;
    }
    
    public Meter add(Meter other) {
        return new Meter(this.getNumerator() * other.getDenominator() + other.getNumerator() * this.getDenominator(), 
                this.getDenominator() * other.getDenominator() );
    }
    
    public Meter multiply(Meter other) {
        return new Meter(this.getNumerator() * other.getNumerator(), this.getDenominator() * other.getDenominator() );
    }
    
    @Override
    public String toString() {
        return this.numerator + "/" + this.denominator;
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Meter))
            return false;
        Meter otherMeter = (Meter)other;
        return ( (this.numerator == otherMeter.getNumerator()) && (this.denominator == otherMeter.getDenominator()) );
    }
    
}
