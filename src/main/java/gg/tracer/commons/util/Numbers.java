package gg.tracer.commons.util;

/**
 * @author Bradley Steele
 */
public final class Numbers {

    private Numbers() {}

    @SuppressWarnings("unchecked")
    public static <T extends Number> T add(T n1, T n2) {
        Number value;

        if (n1 instanceof Byte) {
            value = n1.byteValue() + n2.byteValue();
        } else if (n1 instanceof Short) {
            value = n1.shortValue() + n2.shortValue();
        } else if (n1 instanceof Long) {
            value = n1.longValue() + n2.longValue();
        } else if (n1 instanceof Float) {
            value = n1.floatValue() + n2.floatValue();
        } else if (n1 instanceof Double) {
            value = n1.doubleValue() + n2.doubleValue();
        } else {
            value = n1.intValue() + n2.intValue();
        }

        return (T) value;
    }
}
