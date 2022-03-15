package org.noexcs.codec;

/**
 * @author noexcs
 * @since 3/15/2022 6:01 PM
 */
public interface Serializer {

    <T> byte[] serialize(T object);

    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
