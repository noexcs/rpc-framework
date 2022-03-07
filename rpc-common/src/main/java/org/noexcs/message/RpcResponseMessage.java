package org.noexcs.message;

/**
 * @author com.noexcept
 */
public class RpcResponseMessage extends RpcMessage {

    public RpcResponseMessage(int sequenceId, Object returnValue, Exception exceptionValue) {
        super(sequenceId);
        this.returnValue = returnValue;
        this.exceptionValue = exceptionValue;
    }

    public RpcResponseMessage(int sequenceId) {
        super(sequenceId);
    }

    private Object returnValue;

    private Exception exceptionValue;

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public Exception getExceptionValue() {
        return exceptionValue;
    }

    public void setExceptionValue(Exception exceptionValue) {
        this.exceptionValue = exceptionValue;
    }

    @Override
    public String toString() {
        return "RpcResponseMessage{" +
                "returnValue=" + returnValue +
                ", exceptionValue=" + exceptionValue +
                '}';
    }
}