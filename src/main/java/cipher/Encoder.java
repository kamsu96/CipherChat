package cipher;

/***
 * Autor: Łukasz Sus 226015
 * Data: 17.10.2018
 */
public class Encoder {

    public static final String DEFAULT_KEY = "046c73498a53cbb732248168a3ced0ad";
    private String key;

    public Encoder(){
        this(DEFAULT_KEY );
    }

    public Encoder(String key) {
        this.key = key;
    }

    public String caesarEncode(String text) {
        int length = text.length();
        int keyLength = key.length();
        int keyIterator = 0;
        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < length; ++i) {
            int keyMove = (int) key.charAt(keyIterator);
            int x = positiveModulo(text.charAt(i) + keyMove, 256);
            sb.setCharAt(i, (char) x);
            keyIterator = (keyIterator + 1) % keyLength;
        }
        return sb.toString();
    }

    /**
     * There is no xorDecode method because xorEncode is invertible.
     * @param text
     * @return
     */
    public String xorEncode(String text) {
        int length = text.length();
        int keyLength = key.length();
        int keyIterator = 0;
        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < length; ++i) {
                int keyMove = (int) key.charAt(keyIterator);
                int x = (text.charAt(i)^keyMove) % 256;
                sb.setCharAt(i, (char) x);
                keyIterator = (keyIterator + 1) % keyLength;
        }
        return sb.toString();
    }

    public String matrixEncode(String text){
        return MatrixCipher.Encrypt(text);
    }

    public String caesarDecode(String text) {
        int length = text.length();
        int keyLength = key.length();
        int keyIterator = 0;
        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < length; ++i) {
            int keyMove = (int) key.charAt(keyIterator);
            int x = positiveModulo(text.charAt(i) - keyMove, 256);
            sb.setCharAt(i, (char) x);
            keyIterator = (keyIterator + 1) % keyLength;
        }
        return sb.toString();
    }

    public String matrixDecode(String text){
        return MatrixCipher.Decrypt(text);
    }

    public String encode(String text){
        text = this.caesarEncode(text);
        text = this.xorEncode(text);
        text = this.matrixEncode(text);
        return text;
    }

    public String decode(String text){
        text = this.matrixDecode(text);
        text = xorEncode(text);
        text = caesarDecode(text);
        return text;
    }

    private int getBitAt(char c, int index){
        int val = c << (7 - index);
        val = val >> 7;
        return val;
    }

    private boolean isCapitalLetter(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private boolean isLowercaseLetter(char c) {
        return c >= 'a' && c <= 'z';
    }

    private int positiveModulo(int dividend, int divisor) {
        return ((dividend) % divisor + divisor) % divisor;
    }

    private String negBits(String s) {
        StringBuffer sb = new StringBuffer(s);
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            sb.setCharAt(i, (char) (255 - (int) sb.charAt(i)));
        }
        return sb.toString();
    }
}
