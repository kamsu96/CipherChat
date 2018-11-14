package cipher;

/**
 * @author Łukasz Sus
 */
public class CharBuffer {
    private static final int BYTE = 8;
    private int codeSize;
    private int buffer;
    private int numSignificantBits;

    public CharBuffer(int codeSize) {
        this.codeSize = codeSize;
        buffer = 0;
        numSignificantBits = 0;
    }

    /**
     * If code has more bits than specified in codeSize, code will be cut to size specified in codeSize.
     * If buffer has 8 or more significant bits, code will not be inserted and method returns false.
     * @param code code value
     * @return if succeeded
     */
    public boolean insertCode(int code){
        return insertCode(code, this.codeSize);
    }

    /**
     * If code has more bits than specified in codeSize, code will be cut to size specified in codeSize.
     * If buffer has 8 or more significant bits, code will not be inserted and method returns false.
     * @param code code value
     * @param codeSize number of significat bits
     * @return if succeeded
     */
    public boolean insertCode(int code, int codeSize){
        if(numSignificantBits>=BYTE){
            return false;
        }
        buffer = buffer << codeSize;
        int mask = (int)Math.pow(2,codeSize) - 1;
        code &= mask;
        buffer += code;
        numSignificantBits += codeSize;
        return true;
    }

    public boolean insertChar(int c){
        if(numSignificantBits>=BYTE){
            return false;
        }
        buffer = buffer << BYTE;
        buffer += c;
        numSignificantBits += BYTE;
        return true;
    }

    public boolean hasNextByte(){
        if(numSignificantBits >= BYTE){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean hasNextCode(){
        return hasNextCode(this.codeSize);
    }

    public boolean hasNextCode(int codeSize){
        if(numSignificantBits >= codeSize){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Method not only returns next char but it also removes it from buffer.
     * If it doesn't contain full byte it returns its content shifted to left as far as it is possible as byte.
     * @return next char (byte)
     */
    public char getNextChar(){
        int retVal;
        int mask = 255;     // 8 significant bits
        int offset = numSignificantBits - BYTE;
        if(offset > 0){
            mask = mask << offset;
            retVal = buffer & mask;
            buffer = buffer & ~mask;
            retVal = retVal >> offset;
            numSignificantBits -= BYTE;
        }
        else {
            offset *= -1;
            retVal = buffer << offset;
            buffer = 0;
            numSignificantBits = 0;
        }
        return (char)retVal;
    }

    public int getNextCode(){
        return getNextCode(this.codeSize);
    }

    public int getNextCode(int codeSize){
        int retVal;
        int offset = numSignificantBits - codeSize;
        if(offset < 0){
            return -1;
        }
        int mask = (int)Math.pow(2, codeSize) - 1;
        mask = mask << offset;

        retVal = (buffer & mask) >> offset;
        buffer = buffer & (~mask);
        if(numSignificantBits >= codeSize) {
            numSignificantBits -= codeSize;
        }
        else{
            numSignificantBits = 0;
        }
        return retVal;
    }
}