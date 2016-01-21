
package interpreter;

import interpreter.Token.TokenType;
import interpreter.Measure.RepeatType;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map; 
import java.util.HashMap;

/**
 * 
 *
 */
public class Parser {
  
    private final Lexer lexer;
    private final Piece piece;
    
    private int currentMeasureNumber;
    private String currentVoiceName;
    
    /**
     * Creates a new Parser object.
     * @param lexer
     */
    public Parser(Lexer lexer) {
      this.lexer = lexer;
        this.piece = new Piece();
    }
  
    /**
     * Accesses the Piece object constructed by the Parser. 
     * @return the parser's Piece
     */
    public Piece getPiece() {
      return this.piece;
    }
  
    /**
     * Accesses the header value of a token if it is a header field.
     * @param token the header field from which to get the header value 
     * @param specify whether all whitespace should be stripped, for headers where it is necessary
     * @return the value of the header field, i.e. the contents after the colon
     */
    private String getHeaderValue(Token token, boolean stripAllWhitespace) {
      if (token.getType() != TokenType.HEADER_FIELD && token.getType() != TokenType.VOICE)
        throwParserException("Attempted to get header value from invalid token \'" + token.getString() + "\'");
          String result = token.getString().substring(2);
          if (stripAllWhitespace)
            result = result.replaceAll("[ \t]+", "");
          else
            result = result.replaceAll("^[ \t]+", "");
              return result; 
    }
  
    /**
     * Returns the first item in the list tokens
     * @param tokens
     * @return
     */
    public Token nextToken(List<Token> tokens) {
      return tokens.get(0);
    }
  
    /**
     * Removes and returns the first token from tokens
     * @param tokens
     * @return
     */
    public Token popToken(List<Token> tokens) {
      Token token = tokens.get(0);
        tokens.remove(0);
        return token;
    }
  
    /**
     * Parses the headers into the Parser's piece fields.  
     * @param tokensIn, a list of tokens for an entire piece 
     * @return a new list of tokens without the headers
     */
    public List<Token> parseHeaders(List<Token> tokensIn) {
      
        List<Token> tokens = new ArrayList<Token>(tokensIn);
        
        Token token;
        
        // apply default values to those fields that have them
        this.piece.setIndex(-1);
        this.piece.setTitle("Untitled");
        this.piece.setComposer("Unknown");
        this.piece.setDefaultNoteLengthsPerMinute(100);
        this.piece.setDefaultNoteLength(new Meter(1,8));
        this.piece.setTimeSignature(new Meter(4,4));
        
        // process index token
        token = popToken(tokens);
        if (token.getType() != TokenType.HEADER_FIELD || token.getString().charAt(0) != 'X')
          throwParserException("Index header ('X') not found at start of file");
            try {
              this.piece.setIndex(Integer.parseInt(this.getHeaderValue(token, true)));
            } catch (NumberFormatException e) {
              throwParserException("Unable to process Index header ('X') value \'" + this.getHeaderValue(token, true) + "\'");
            }
      
        // process title token
        token = popToken(tokens);
        if (token.getType() != TokenType.HEADER_FIELD || token.getString().charAt(0) != 'T')
          throwParserException("Title header ('T') not found immediately after index header at start of file");
            this.piece.setTitle(this.getHeaderValue(token, false));
            
            boolean endOfHeadersReached = false;
            while (!endOfHeadersReached) {
              
                token = nextToken(tokens);
                
                if (token.getType() != TokenType.HEADER_FIELD && token.getType() != TokenType.VOICE)
                  throwParserException("Invalid token found in header \'" + token.getString() +"\'");
                    
                    if (token.getType() == TokenType.VOICE) {
                      Voice voice = new Voice();
                        voice.setName(getHeaderValue(token, false));
                        this.piece.addVoice(voice);
                        popToken(tokens);
                        continue;
                    }
              
                char headerType = token.getString().charAt(0);
                
                switch (headerType) {
                  case 'C':
                           this.piece.setComposer(this.getHeaderValue(token, false));
                             break;
                  case 'K':
                           try {
                             this.piece.setKeySignature(new KeySignature(this.getHeaderValue(token, true)));
                           } catch (IllegalArgumentException e) {
                             throwParserException("Invalid key signature \'" + this.getHeaderValue(token, true) +"\'");
                           }
                           endOfHeadersReached = true;
                             break;
                  case 'L':
                           try {
                             this.piece.setDefaultNoteLength(new Meter(this.getHeaderValue(token, true)));
                           } catch (NumberFormatException e) {
                             throwParserException("Invalid default note length \'" + this.getHeaderValue(token, true) +"\'");
                           }
                           break;
                  case 'M':
                           String timeSignatureString = this.getHeaderValue(token, true);
                             if (timeSignatureString.equals("C"))
                               this.piece.setTimeSignature(new Meter(4,4));
                             else if (timeSignatureString.equals("C|"))
                               this.piece.setTimeSignature(new Meter(2,2));
                             else {
                               try {
                                 this.piece.setTimeSignature(new Meter(timeSignatureString));
                               } catch (NumberFormatException e) {
                                 throwParserException("Invalid time signature \'" + timeSignatureString +"\'");
                               }
                             }
                           break;
                  case 'Q':
                           try {
                             this.piece.setDefaultNoteLengthsPerMinute(Integer.parseInt(this.getHeaderValue(token, true)));
                           } catch (NumberFormatException e) {
                             throwParserException("Invalid tempo \'" + this.getHeaderValue(token, true) +"\'");
                           }
                           break;
                  default:
                          throwParserException("Unsupported header \'" + token.getString() +"\'");
                }
              
                popToken(tokens);
                
            }
      
        for (Token bodyToken : tokens)
          if (bodyToken.getType() == TokenType.HEADER_FIELD)
            throwParserException("Stray header found in body \'" + bodyToken.getString() +"\'");
              
              return tokens;
    }
  
    /**
     * Parses a repeat-expanded token list for different voices, and separates the 
     * corresponding parts. 
     * @param tokens a repeat-expanded token list
     * @return a map from Voice objects for each voice in the piece to token lists 
     *          corresponding to their full parts in the piece.. 
     */
    public Map<String, List<Token>> mergeVoices(List<Token> tokens) {
      
        Map<String, List<Token>> voiceMap = new HashMap<String, List<Token>>();
        
        // if the piece did not declare voices in its header, create a single voice
        // and add the entire body to it
        if (this.piece.getVoices().size() < 1) {
          Voice voice = new Voice();
            // give the voice a dummy name so it can be put in the map
            voice.setName("1");
            this.piece.addVoice(voice);
            voiceMap.put(voice.getName(), tokens);
            return voiceMap;
        }
      
        // fill the voiceMap with a list for each voice
        for (Voice voice : this.piece.getVoices()) {
          voiceMap.put(voice.getName(), new ArrayList<Token>());
        }
      
        // the first token in the body must be a voice
        if (tokens.get(0).getType() != TokenType.VOICE)
          throwParserBodyException("Header declared voices but body does not begin with a voice");
            
            String currentVoiceName = getHeaderValue(tokens.get(0), false);
            for (Token token : tokens) {
              if (token.getType() == TokenType.VOICE) {
                currentVoiceName = getHeaderValue(token, false);
                  if (!voiceMap.keySet().contains(currentVoiceName))
                    throwParserException("Found voice \'" + currentVoiceName + "\' in body that was not declared in header");
              }
              else
                voiceMap.get(currentVoiceName).add(token); 
            }
      
        return voiceMap;
        
    }
  
    /**
     * parseNote
     * Produces a Note object from a set of tokens
     * @param tokens            The list of tokens to use
     * @param accidentals       A map of accidentals in the current bar. Modifies this map if the note has an accidental on it
     * @param timeFactor        Applies the time factor to the length of the note
     * @return                  A note parsed from the input tokens
     */
    public Note parseNote(List<Token> tokens, Map<String, Integer> accidentals, Meter timeFactor) {
      
        Note note = new Note();
        
        int accidental = 0;
        boolean noteHasAccidental = false;
        
        //loop through token list linearly to process note
        for (Token token : tokens) {
          switch (token.getType()) {
            case NOTE_NAME:
                           
                             char pitch = token.getString().charAt(0);
                             if (Character.isLowerCase(pitch) && pitch != 'z') {
                               pitch = Character.toUpperCase(pitch);
                                 note.setOctave(note.getOctave() + 1);
                             }
                           note.setPitch(pitch);
                             
                             break;
                             
            case NOTE_OCTAVE_MODIFIER:
                                      
                                        // decrement the octave for each ',' and increment it for each '\''
                                        // note: this allows c, to be a valid, note, equivalent to C which the spec wasn't clear about
                                        if (token.getString().charAt(0) == ',')
                                          note.setOctave(note.getOctave() - 1);
                                        else if (token.getString().charAt(0) == '\'')
                                          note.setOctave(note.getOctave() + 1);
                                        else
                                          throwParserBodyException("Bad octave modifier \'" + token.getString() + "\'");
                                            
                                            break;
                                            
            case NOTE_ACCIDENTAL:
                                 
                                   int sharps = Util.countOccurrencesOfChar(token.getString(), '^');
                                   int flats = Util.countOccurrencesOfChar(token.getString(), '_');
                                   int natural = Util.countOccurrencesOfChar(token.getString(), '=');
                                   if ( (sharps > 0 && flats > 0) || (sharps > 2) || (flats > 2) || (natural > 0 && (sharps > 0 || flats > 0)) )
                                     throwParserBodyException("Illegal accidental \'" + token.getString() + "\'");
                                       accidental =  sharps - flats;
                                       noteHasAccidental = true;
                                       
                                       break;
                                       
            case NOTE_LENGTH:
                             
                               int numerator = 0, denominator = 0;
                               String numString, demString;
                               
                               // parse out the numerator and denominator
                               if (token.getString().contains("/")) {
                                 numString = token.getString().replaceAll("[^/]*$","");
                                   numString = numString.replaceAll("/", "");
                                   demString = token.getString().replaceAll("^[^/]*","");
                                   demString = demString.replaceAll("/", "");
                               } else {
                                 numString = token.getString();
                                   demString = "";
                               }
                             
                               // the default value for the numerator is 1
                               if (numString.equals(""))
                                 numerator = 1;
                               else
                                 numerator = Integer.parseInt(numString);
                                   
                                   // the default value for the denominator is 2
                                   if (!token.getString().contains("/"))
                                     denominator = 1;
                                   else if (demString.equals(""))
                                     denominator = 2;
                                   else
                                     denominator = Integer.parseInt(demString);
                                       
                                       note.setLength(new Meter(numerator, denominator));
                                       break;
                                       
            default:
                    throwParserBodyException("Invalid token \'" + token.getString() + "\'");
                      break;
          }
        }
      
        // create a note with the same pitch and octave, ignoring length
        Note accidentalNote = new Note();
        accidentalNote.setPitch(note.getPitch());
        accidentalNote.setOctave(note.getOctave());
        
        // add the accidentalNote to the accidental map if the note has an accidental on it
        if (noteHasAccidental) {
          accidentals.put(accidentalNote.toString(), accidental);
        }
      
        // apply accidental if accidentalNote is found in list of accidentals
        // otherwise, apply the key signature
        if (accidentals.keySet().contains(accidentalNote.toString()))
          note.setAccidental(accidentals.get(accidentalNote.toString()));
        else
          this.piece.getKeySignature().processNote(note);
            
            // apply time factor to note duration
            note.setLength(note.getLength().multiply(timeFactor));
            
            return note;
    }
  
    /**
     * parseChord
     * Parses a chord from a list of tokens, given that the tokens are formatted correctly 
     * @param tokens
     * @param accidentals
     * @param timeFactor
     * @return
     */
    public Chord parseChord(List<Token> tokens, Map<String, Integer> accidentals, Meter timeFactor) {
      
        int i = 0;
        List<Note> noteList = new ArrayList<Note>();
        
        while (i < tokens.size()) {
          
            //adds note to chord
            List<Token> noteTokens = new ArrayList<Token>();
            noteTokens.add(tokens.get(i++));
            if (noteTokens.get(0).getType() == TokenType.NOTE_ACCIDENTAL) {
              Token noteNameToken = tokens.get(i++);
                if (noteNameToken.getType() != TokenType.NOTE_NAME)
                  throwParserBodyException("Accidental was not followed by note name");
                    noteTokens.add(noteNameToken);
            } else if (noteTokens.get(0).getType() != TokenType.NOTE_NAME) {
              throwParserBodyException("Expected note name but got \'" + noteTokens.get(0).getString() + "\'");
            }
          while (i < tokens.size() && !Arrays.asList(noteSeparators).contains(tokens.get(i).getType()))
            noteTokens.add(tokens.get(i++));
              if (noteTokens.size() > 0)
                noteList.add(parseNote(noteTokens, accidentals, timeFactor));
                  
        }
      
        return new Chord(noteList);
        
    }
  
    /**
     * The types of tokens that, when reached, indicate the current note being processed has ended
     */
    private final TokenType[] noteSeparators = { 
      TokenType.NOTE_NAME, TokenType.NOTE_ACCIDENTAL, TokenType.CHORD_BEGIN, TokenType.TUPLET
    };
  
    /**
     * parseMeasure, parses a list of tokens representing a measure, given 
     * that the tokens are in accordance with the grammar for a measure
     * @param tokens The list of tokens to build the chord list from
     * @return The list of chords represented by the tokens
     */ 
    public Measure parseMeasure(List<Token> tokens) {
      
        int i = 0;
        Measure measure = new Measure();
        Map<String, Integer> accidentals = new HashMap<String, Integer>();
        
        while (i < tokens.size()) {
          
            Token token = tokens.get(i);
            List<Token> chordTokens;
            
            switch (token.getType()) {
              
              case CHORD_BEGIN:
                               
                                 chordTokens = new ArrayList<Token>();
                                 i++; // pass over the chord_begin token
                               while (i < tokens.size() && tokens.get(i).getType() != TokenType.CHORD_END)
                                 chordTokens.add(tokens.get(i++));
                                   if (i == tokens.size())
                                     throwParserBodyException("Chord begin \'[\' was not followed by chord end \']\'");
                                       if (chordTokens.size() > 0)
                                         measure.addChord(parseChord(chordTokens, accidentals, new Meter(1,1)));
                                           i++; // pass over the chord_end token
                               break;
                                 
              case TUPLET:
                          
                            i++; // pass over the tuplet token
                          int tupletCount = Integer.parseInt(String.valueOf(token.getString().charAt(1)));
                            Meter timeFactor = new Meter();
                            switch (tupletCount) {
                              case 2:
                                     timeFactor = new Meter(3,2);
                                       break;
                              case 3:
                                     timeFactor = new Meter(2,3);
                                       break;
                              case 4:
                                     timeFactor = new Meter(3,4);
                                       break;
                              default:
                                      throwParserBodyException("Invalid tuplet length detected");
                            }
                          //loop through notes for tuplet and process them one-by-one
                          for (int j = 0; j < tupletCount; j++) {
                            chordTokens = new ArrayList<Token>();
                              chordTokens.add(tokens.get(i++));
                              if (chordTokens.get(0).getType() == TokenType.NOTE_ACCIDENTAL) {
                                Token noteNameToken = tokens.get(i++);
                                  if (noteNameToken.getType() != TokenType.NOTE_NAME)
                                    throwParserBodyException("Accidental was not followed by note name");
                                      chordTokens.add(noteNameToken);
                              } else if (chordTokens.get(0).getType() != TokenType.NOTE_NAME) {
                                throwParserBodyException("Expected note name but got \'" + chordTokens.get(0).getString() + "\'");
                              }
                            while (i < tokens.size() && !Arrays.asList(noteSeparators).contains(tokens.get(i).getType()))
                              chordTokens.add(tokens.get(i++));
                                if (chordTokens.size() > 0)
                                  measure.addChord(parseChord(chordTokens, accidentals, timeFactor));
                          }
                          break;
                            
              default:
                      
                        //parse note in chord format for measure datatype
                        chordTokens = new ArrayList<Token>();
                        chordTokens.add(tokens.get(i++));
                        if (chordTokens.get(0).getType() == TokenType.NOTE_ACCIDENTAL) {
                          Token noteNameToken = tokens.get(i++);
                            if (noteNameToken.getType() != TokenType.NOTE_NAME)
                              throwParserBodyException("Accidental was not followed by note name");
                                chordTokens.add(noteNameToken);
                        } else if (chordTokens.get(0).getType() != TokenType.NOTE_NAME) {
                          throwParserBodyException("Expected note name but got \'" + chordTokens.get(0).getString() + "\'");
                        }
                      while (i < tokens.size() && !Arrays.asList(noteSeparators).contains(tokens.get(i).getType()))
                        chordTokens.add(tokens.get(i++));
                          if (chordTokens.size() > 0)
                            measure.addChord(parseChord(chordTokens, accidentals, new Meter(1,1)));
                              break;
                              
            }
          
        }
      
        return measure;
    }
  
    /**
     * parseVoice
     * Adds the series of measures represented by tokens to the specified voice
     * given that the measures are formatted correctly
     * @param voice     The voice to add the chords to
     * @param tokens    The tokens to build the chords from
     */ 
    public void parseVoice(Voice voice, List<Token> tokens) {
      
        // first run through the tokens to ensure there are no nested repeats
        boolean inRepeat = false;
        for (Token token : tokens) {
          if (token.getType() == TokenType.BAR_BEGIN_REPEAT)
            if (inRepeat)
              throwParserException("Found nested repeats");
            else
              inRepeat = true;
          else
            inRepeat = false;
        }
      
        // now run through the tokens to segment and parse the measures,
        // keeping track of the repeat type for each measure
        List<Token> measureTokens = new ArrayList<Token>();
        RepeatType repeatType = RepeatType.NONE;
        this.currentMeasureNumber = 1;
        for (Token token : tokens) {
          
            if (token.getType() == TokenType.BAR || token.getType() == TokenType.BAR_BEGIN_REPEAT || token.getType() == TokenType.BAR_END_REPEAT) {
              
                if (measureTokens.size() < 1 && token.getType() != TokenType.BAR_BEGIN_REPEAT)
                  throwParserBodyException("Bar line produces empty measure");
                    
                    Measure newMeasure = parseMeasure(measureTokens);
                    
                    // END differs from SECTION_END in that END causes a repeat to occur
                    // whereas SECTION_END can be the place that a repeat backtracks to
                    if (token.getType() == TokenType.BAR_END_REPEAT && repeatType != RepeatType.FIRST_ENDING)
                      repeatType = RepeatType.END;
                        if (token.getString().equals("|]"))
                          repeatType = RepeatType.SECTION_END;
                            
                            newMeasure.setRepeatType(repeatType);
                            voice.addMeasure(newMeasure);
                            this.currentMeasureNumber += 1;
                            measureTokens.clear();
                            
                            if (token.getType() == TokenType.BAR_BEGIN_REPEAT)
                              repeatType = RepeatType.BEGIN;
                            else
                              repeatType = RepeatType.NONE;
                                
            } else {
              if (token.getType() == TokenType.FIRST_ENDING) {
                if (repeatType != RepeatType.NONE)
                  throwParserException("Found invalid first ending in measure " + this.currentMeasureNumber);
                    repeatType = RepeatType.FIRST_ENDING;
              }
              else if (token.getType() == TokenType.SECOND_ENDING) {
                repeatType = RepeatType.SECOND_ENDING;
              } else {
                // otherwise, just add the token
                measureTokens.add(token);
              }
            }
          
        }
      
        // if the last measure doesn't have a
        // closing bar then throw an exception
        if (measureTokens.size() > 0) {
          throwParserException("Voice \'" + "\' missing closing bar at the end of the piece");
        }
      
        // display a warning if there is a measure whose length
        // doesn't match the time signature
        List<Integer> nonstandardLengthMeasures = new ArrayList<Integer>();
        for (int i = 0; i < voice.getMeasures().size(); i++) {
          Measure measure = voice.getMeasures().get(i);
            double totalLength = 0;
            for (Chord chord : measure.getChords())
              totalLength += chord.getLength().compute() * this.piece.getDefaultNoteLength().compute();
                if (Math.abs(totalLength - this.piece.getTimeSignature().compute()) > 0.01) // allow a threshold for rounding errors
                  nonstandardLengthMeasures.add(i);    
        }
      if (nonstandardLengthMeasures.size() > 0)
        System.out.println("Warning in voice \'" + voice.getName() +"\': the following measures have nonstandard length: " + nonstandardLengthMeasures);
          
    }
  
    /** 
     * parse
     * @return The piece produced by parsing the tokens generated by the Lexer passed in the constructor 
     * Parses the entire abc file into a Piece object, given that it is formatted correctly. 
     * @return The piece produced by parsing the tokens generated by the Lexer passed in the constructor 
     */
    public Piece parse() {
      
        List<Token> tokens = lexer.generateTokens();
        
        // parse the headers and merge the voices into voiceMap
        tokens = parseHeaders(tokens);
        Map<String, List<Token>> voiceMap = mergeVoices(tokens);
        
        // parse each voice
        for (String voiceName : voiceMap.keySet()) {
          for (Voice voice : this.piece.getVoices())
            if (voice.getName().equals(voiceName)) {
              this.currentVoiceName = voice.getName();
              List<Token> voiceTokens = voiceMap.get(voice.getName());
              this.parseVoice(voice, voiceTokens);
                break;
            }
        }
      
        return this.piece;
        
    }
  
    public void throwParserException(String errorString) {
      throw new IllegalArgumentException("Error parsing file: " + errorString);
    }
  
    public void throwParserBodyException(String errorString, String dataString) {
      StringBuilder message = new StringBuilder("");
        message.append("Error parsing file in voice \'" + this.currentVoiceName + "\' at measure " + this.currentMeasureNumber + "\n" + "Description: " + errorString);
        if (!dataString.equals(""))
          message.append("\n" + dataString);
            throw new IllegalArgumentException(message.toString());
    }
  
    public void throwParserBodyException(String errorString) {
      throwParserBodyException(errorString, "");
    }
  
}
