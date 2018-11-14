package cipher;

/**
 * @author Sebastian Korniewicz
 */
public class MatrixCipher {
    public static String Encrypt(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        int size = (int) Math.ceil(Math.sqrt(text.length()));
        for(int i = 0; i<size; i++) {
            for (int j = 0; j<size;j++)
            {
                if(text.length()>j*size+i)
                    stringBuilder.append(text.charAt(j*size+i));
            }
        }

        return stringBuilder.toString();
    }

    public static String Decrypt(String text) {

        int size = (int) Math.ceil(Math.sqrt(text.length()));
        char[] chars = new char[text.length()];

        int iter = 0;
        for(int i = 0; i<size; i++) {
            for (int j = 0; j<size;j++)
            {
                if(text.length()>iter && i + j * size<text.length()) {
                    chars[i + j * size] =(char)(int)text.charAt(iter);
                    //stringBuilder.append(text);
                    iter++;
                }
            }
        }
        return new String(chars);
    }
}
