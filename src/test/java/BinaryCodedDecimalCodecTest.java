import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BinaryCodedDecimalCodecTest {

    @Test
    void encode() {
        BinaryCodedDecimalCodec codec = new BinaryCodedDecimalCodec();
        byte[] bytes = codec.encode("54321");
        assertEquals(3, bytes.length);
        assertEquals(0b00000101, bytes[0]);
        assertEquals(0b01000011, bytes[1]);
        assertEquals(0b00100001, bytes[2]);
    }

    @Test
    void encodeNull() {
        BinaryCodedDecimalCodec codec = new BinaryCodedDecimalCodec();
        assertThrows(IllegalArgumentException.class, () -> codec.encode(null));
    }

    @Test
    void encodeBlank() {
        BinaryCodedDecimalCodec codec = new BinaryCodedDecimalCodec();
        assertThrows(IllegalArgumentException.class, () -> codec.encode(""));
    }

    @Test
    void encodeWhitespace() {
        BinaryCodedDecimalCodec codec = new BinaryCodedDecimalCodec();
        assertThrows(IllegalArgumentException.class, () -> codec.encode("\t"));
        assertThrows(IllegalArgumentException.class, () -> codec.encode("\n"));
        assertThrows(IllegalArgumentException.class, () -> codec.encode(" "));
    }


    @Test
    void encodeNonInteger() {
        BinaryCodedDecimalCodec codec = new BinaryCodedDecimalCodec();
        assertThrows(IllegalArgumentException.class, () -> codec.encode("foobar"));
    }

    @Test
    void encodeNegativeInteger() {
        BinaryCodedDecimalCodec codec = new BinaryCodedDecimalCodec();
        assertThrows(IllegalArgumentException.class, () -> codec.encode("-34058"));
    }

    @Test
    void decode() {
        BinaryCodedDecimalCodec codec = new BinaryCodedDecimalCodec();
        byte[] bytes = new byte[] {0b00000101, 0b01000011, 0b00100001};
        String decode = codec.decode(bytes);
        assertEquals("54321", decode);
    }

    @Test
    void decodeNull() {
        BinaryCodedDecimalCodec codec = new BinaryCodedDecimalCodec();
        assertThrows(IllegalArgumentException.class, () -> codec.decode(null));
    }

    @Test
    void decodeZeroLength() {
        BinaryCodedDecimalCodec codec = new BinaryCodedDecimalCodec();
        assertThrows(IllegalArgumentException.class, () -> codec.decode(new byte[0]));
    }

    @Test
    void decodeInvalidNumbers() {
        BinaryCodedDecimalCodec codec = new BinaryCodedDecimalCodec();
        byte[] bytes = new byte[] {0b00000101, 0b01001111, 0b00100001};
        assertThrows(NumberFormatException.class, () -> codec.decode(bytes));
    }
}