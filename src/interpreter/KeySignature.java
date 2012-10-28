package interpreter;

import java.util.List;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.File;

public class KeySignature {
	
    private static enum KeySignatureType {
        SHARP,
        FLAT,
    }
    
    private String key;
    private List<Character> notes;
    private KeySignatureType type;
    
    /**
     * Creates a new KeySignature object with the key specified by key
     * @param key   The key to use for the KeySignature
     */
	public KeySignature(String key) {
	    
	    boolean found = false;
	    
	    try {
	        
	        this.key = key;
	        File fXmlFile = new File(System.getProperty("user.dir") + "/src/interpreter/keysignature.xml");
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(fXmlFile);
	        doc.getDocumentElement().normalize();
	        NodeList keySignatureList = doc.getElementsByTagName("keysignature");
	        
	        this.notes = new ArrayList<Character>();
	        
	        for (int i = 0; i < keySignatureList.getLength(); i++) {
	           Element keySignature = (Element)keySignatureList.item(i);
	           if (this.getXMLTagValue("key", keySignature).equals(key)) {
	               
	               found = true;
	               
	               if (this.getXMLTagValue("type", keySignature).equals("Sharp"))
	                   this.type = KeySignatureType.SHARP;
	               else if (this.getXMLTagValue("type", keySignature).equals("Flat"))
	                   this.type = KeySignatureType.FLAT;
	               else
	                   throw new IllegalArgumentException("Error in keysignature specification");
	               
	               NodeList noteNodes = keySignature.getElementsByTagName("note");
	               for (int j = 0; j < noteNodes.getLength(); j++) {
	                   this.notes.add(noteNodes.item(j).getChildNodes().item(0).getNodeValue().charAt(0));
	               }
	               
	               break;
	           }
	        }
	        
        } catch (Exception e) {
            e.printStackTrace();
        }
	    
	    if (!found) {
	        throw new IllegalArgumentException("Key signature " + key + " not recognized");
	    }
	    
	}
	
	private String getXMLTagValue(String tag, Element element) {
	    NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        return nodes.item(0).getNodeValue();
	}
	
	/**
	 * processNote
	 * Applies the key signature to the specified note
	 * @param note     The note to process
	 */
	public void processNote(Note note) {
	    if (this.notes.contains(note.getPitch())) {
	        if (this.type == KeySignatureType.SHARP)
	            note.setAccidental(note.getAccidental() + 1);
	        else
	            note.setAccidental(note.getAccidental() - 1);
	    }
	}
	
    @Override
    public String toString() {
        return this.key;
    }

}
