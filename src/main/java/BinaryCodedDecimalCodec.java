import java.nio.ByteBuffer;

/**
 * Codec for Binary Coded Decimals
 * Ordinarily, decimal digits stored as text would be encoded using ASCII and so would be represented
 * by a single byte for every digit. To reduce storage and transmission rate requirements, BCD instead
 * packs each digit into a half byte (also known as a ‘nibble’).
 */
public class BinaryCodedDecimalCodec {

    /**
     * Encodes a decimal number to the BCD format
     * @param decimalNumber a {@code String}-represented positive integer
     * @return a BCD-encoded byte array
     * @throws IllegalArgumentException if the given {@code decimalNumber} argument is null, blank, whitespace, a non-integer or a negative integer
     */
    public byte[] encode(String decimalNumber) {
        //Preconditions
        //null or whitespace
        if (decimalNumber == null || decimalNumber.trim().equals("")) {
            throw new IllegalArgumentException("String argument decimalNumber must not be null, blank or whitespace");
        }
        //non-integer or negative integer
        try {
            int ignored = Integer.parseUnsignedInt(decimalNumber.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("String argument decimalNumber must be parseable as a positive integer", e);
        }

        //Prep
        //Trim whitespace
        decimalNumber = decimalNumber.trim();
        //Even length zero padding
        if (decimalNumber.length() % 2 != 0) {
            decimalNumber = "0" + decimalNumber;
        }

        //Logic
        char[] chars = decimalNumber.toCharArray();
        ByteBuffer byteBuffer = ByteBuffer.allocate(chars.length / 2);
        for (int i = 0; i < chars.length; i += 2) {
            int hi = chars[i] - 48; //ASCII value of '0' is 48
            int lo = chars[i + 1] - 48; //one could also use Character.getNumericValue(), which can handle string encodings other than UTF8 and ASCII, but it is less performant
            byte currByte = (byte) ((hi << 4) | lo);
            byteBuffer.put(currByte);
        }

        return byteBuffer.array();
    }

    /**
     * Decodes a BCD-encoded byte array to a decimal string
     * @param bcdData the BCD-encoded byte array to decode
     * @return a decimal string representation of the given BCD-encoded value
     * @throws IllegalArgumentException if {@code bcdData} is null or zero-length
     * @throws NumberFormatException if one or more nibbles are found that exceed a value of 9
     */
    public String decode(byte[] bcdData) {
        //Preconditions
        //null or zero-length byte array
        if (bcdData == null) {
            throw new IllegalArgumentException("byte array bcdData must be non-null");
        } else if (bcdData.length == 0) {
            throw new IllegalArgumentException("byte array bcdData must have at least one element");
        }

        //Logic
        ByteBuffer byteBuffer = ByteBuffer.wrap(bcdData);
        StringBuilder builder = new StringBuilder();
        while (byteBuffer.hasRemaining()) {
            byte currByte = byteBuffer.get();
            int hi = (currByte & 0xF0) >>> 4;
            int lo = currByte & 0xF;
            if (hi > 9 || lo > 9) {
                throw new NumberFormatException(String.format("One or more nibbles at position %d of the given byte array exceeds a value of 9", byteBuffer.position()));
            }
            builder.append(hi);
            builder.append(lo);
        }

        //regex strips leading zeroes https://stackoverflow.com/a/2800839
        return builder.toString().replaceFirst("^0+(?!$)", "");
    }
}
