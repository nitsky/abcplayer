package interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class Util {
    
    /**
     * readStringFromFile
     * Reads the contents of a file into a string
     * @param path      The path of the file
     * @exception       Throws IOException if there was an error reading the file
     * @return          The contents of the file at path
     */
    public static String readStringFromFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }
    
    /**
     * countOccurrencesOfSubstring
     * Count the number of occurrences of a string within a string.
     * @param string        String to look for substring in.
     * @param substring     Substring to for
     * @return              Number of occurrences of substring in string
     */
    public static int countOccurrencesOfSubstring(final String string, final String substring)
    {
       int count = 0;
       int index = 0;

       while ((index = string.indexOf(substring, index)) != -1)
       {
          index++;
          count++;
       }

       return count;
    }

    /**
     * countOccurrencesOfChar
     * Count the number of occurrences of a character within a string
     * @param string     String to look for substring in
     * @param c          Character to look for
     * @return           Number of occurrences of c in string
     */
    public static int countOccurrencesOfChar(final String string, final char c)
    {
       return countOccurrencesOfSubstring(string, String.valueOf(c));
    }
    
}
