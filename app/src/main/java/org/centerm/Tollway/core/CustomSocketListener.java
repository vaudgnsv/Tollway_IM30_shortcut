package org.centerm.Tollway.core;

/**
 * Created by 29622 on 2018/3/6.
 */
public interface CustomSocketListener {
    /**
     * Connect Time Out
     */
    public abstract void ConnectTimeOut();
    /**
     * Transaction Time Out
     */
    public abstract void TransactionTimeOut();
    /**
     * Receive Data
     */
    public abstract void Received(byte[] data);
    /**
     * Connect Time Out
     */
    public abstract void Error(String error);
    /**
     * Connect Time Out
     */
    public abstract void Other();

}
