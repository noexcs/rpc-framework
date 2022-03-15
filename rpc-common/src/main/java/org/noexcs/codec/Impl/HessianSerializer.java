package org.noexcs.codec.Impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import org.noexcs.codec.Serializer;

import java.io.*;

/**
 * @author noexcs
 * @since 3/15/2022 6:02 PM
 */
public class HessianSerializer implements Serializer {

    @Override
    @SuppressWarnings("all")
    public <T> byte[] serialize(T object) {
            ByteArrayOutputStream bos = null;
            Hessian2Output hessian2Output = null;
        try {
            bos = new ByteArrayOutputStream();
            hessian2Output = new Hessian2Output(bos);
            hessian2Output.writeObject(object);
            hessian2Output.flushBuffer();
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("序列化失败", e);
        } finally {
            try {
                hessian2Output.close();
                bos.close();
            } catch (IOException ignored) {

            }
        }
    }

    @Override
    @SuppressWarnings("all")
    public <T> T deserialize(Class<T> clazz, byte[] bytes) {
        Hessian2Input hessian2Input = null;
        ByteArrayInputStream ois = null;
        try {
            ois = new ByteArrayInputStream(bytes);
            hessian2Input = new Hessian2Input(ois);
            return (T) hessian2Input.readObject(clazz);
        } catch (IOException e) {
            throw new RuntimeException("反序列化失败", e);
        } finally {
            try {
                hessian2Input.close();
                ois.close();
            } catch (IOException ignored) {

            }
        }
    }
}
