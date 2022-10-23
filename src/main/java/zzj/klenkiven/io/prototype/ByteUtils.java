package zzj.klenkiven.io.prototype;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 字节数组工具
 *
 * @author KlenKiven
 */
public final class ByteUtils {

    public static void setDateTimeValue(Date value, int offset, byte[] data) {
        setLongValue(value.getTime(), offset, data);
    }

    public static Date getDateTimeValue(int offset, byte[] data) {
        long time = getLongValue(offset, data);
        return new Date(time);
    }

    public static void setStringValue(String value, int offset, byte[] data) {
        byte[] strData = value.getBytes(StandardCharsets.UTF_8);
        int strByteLen = strData.length;
        setIntValue(strByteLen, offset, data);
        setBytesValue(strData, offset + 4, data);
    }

    public static String getStringValue(int offset, byte[] data) {
        int strByteLen = getIntValue(offset, data);
        return new String(data, 0, strByteLen, StandardCharsets.UTF_8);
    }

    public static void setDoubleValue(double value, int offset, byte[] data) {
        setLongValue(Double.doubleToLongBits(value), offset, data);
    }

    public static double setDoubleValue(int offset, byte[] data) {
        return Double.longBitsToDouble(getLongValue(offset, data));
    }

    public static void setFloatValue(float value, int offset, byte[] data) {
        setIntValue(Float.floatToIntBits(value), offset, data);
    }

    public static float setFloatValue(int offset, byte[] data) {
        return Float.intBitsToFloat(getIntValue(offset, data));
    }

    public static void setLongValue(long value, int offset, byte[] data) {
        setIntValue(((int) (value >>> 32)), offset, data);
        setIntValue(((int) (value)), offset + 4, data);
    }

    public static long getLongValue(int offset, byte[] data) {
        int int1 = getIntValue(offset, data);
        int int2 = getIntValue(offset + 4, data);

        return (((long) int1 << 32) + (int2 & 0xFFFFFFFFL));
    }

    public static void setIntValue(int value, int offset, byte[] data) {
        setByteValue(((byte) ((value >>> 24) & 0xFF)), offset, data);
        setByteValue(((byte) ((value >>> 16) & 0xFF)), offset + 1, data);
        setByteValue(((byte) ((value >>>  8) & 0xFF)), offset + 2, data);
        setByteValue(((byte) ((value) & 0xFF)), offset + 3, data);
    }

    public static int getIntValue(int offset, byte[] data) {
        int byte1 = getByteValue(offset, data);
        int byte2 = getByteValue(offset + 1, data);
        int byte3 = getByteValue(offset + 2, data);
        int byte4 = getByteValue(offset + 3, data);
        return ((byte1 << 24) + (byte2 << 16) + (byte3 << 8) + (byte4));
    }

    public static void setByteValue(byte value, int offset, byte[] data) {
        data[offset] = value;
    }

    public static byte getByteValue(int offset, byte[] data) {
        return data[offset];
    }

    public static void setBytesValue(byte[] bytes, int offset, byte[] data) {
        for (int i = 0; i < bytes.length; i++) {
            setByteValue(bytes[i], offset + i, data);
        }
    }

}
