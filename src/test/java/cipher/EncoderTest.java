package cipher;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncoderTest {

    Encoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new Encoder();
    }

    @Test
    void testCaesar() {
        String s = "Ala ma kota {}::\"\"\n";
        String encoded = encoder.caesarEncode(s);
        assertNotEquals(s, encoded);
        String decoded = encoder.caesarDecode(encoded);
        assertEquals(s, decoded);
    }

    @Test
    void testXorEncode(){
        String s = "Ala ma kota {}::\"\"\n";
        String encoded = encoder.xorEncode(s);
        assertNotEquals(s, encoded);
        String decoded = encoder.xorEncode(encoded);
        assertEquals(decoded, s);
    }

    @Test
    void textMatrixEncode(){
        String s = "Ala ma kota {}::\"\"\n";
        String encoded = encoder.matrixEncode(s);
        assertNotEquals(s, encoded);
        String decoded = encoder.matrixDecode(encoded);
        System.out.println(decoded.length());
        assertEquals(s, decoded);
    }

    @Test
    void testEncode(){
        String s = "Ala ma kota {}::\"\"\n";
        String encoded = encoder.encode(s);
        assertNotEquals(s, encoded);
        String decoded = encoder.decode(encoded);
        assertEquals(decoded, s);
    }
}