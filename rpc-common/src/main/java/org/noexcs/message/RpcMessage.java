package org.noexcs.message;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author com.noexcept
 * @since 1/17/2022 3:58 PM
 */
public class RpcMessage implements Serializable {

    public static AtomicInteger ID = new AtomicInteger();

    private final int sequenceId;

    public RpcMessage(int sequenceId) {
        this.sequenceId = sequenceId;
    }

    public int getSequenceId() {
        return sequenceId;
    }

    @Override
    public String toString() {
        return "RpcMessage{" +
                "sequenceId=" + sequenceId +
                '}';
    }
}
