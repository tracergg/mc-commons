package gg.tracer.commons.itemstack.pdc;

import org.apache.commons.lang.SerializationUtils;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Bradley Steele
 */
public class MapPersistentDataType<K extends Serializable, V extends Serializable> implements PersistentDataType<byte[], HashMap<K, V>> {

    private final HashMap<K, V> COMPLEX_TYPE = new HashMap<>();

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<HashMap<K, V>> getComplexType() {
        return (Class<HashMap<K, V>>) COMPLEX_TYPE.getClass();
    }

    @Override
    public byte[] toPrimitive(HashMap<K, V> complex, PersistentDataAdapterContext context) {
        return SerializationUtils.serialize(complex);
    }

    @Override
    public HashMap<K, V> fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
        return getComplexType().cast(SerializationUtils.deserialize(primitive));
    }
}
