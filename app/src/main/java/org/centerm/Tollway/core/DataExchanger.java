package org.centerm.Tollway.core;

import android.util.Log;

//import org.apache.log4j.Logger;

/**
 * Created by yuhc on 2017/2/13.
 * 数据交换操作的帮助类
 */

public class DataExchanger {
    public static final int COMM_TYPE_TCP = 1;
    public static final int COMM_TYPE_HTTP = 2;
    public static final int COMM_TYPE_UART = 3;

    //private Logger logger = Logger.getLogger(this.getClass());
    private ICommunication mCommunication;
    //通讯方式：1 TCP; 2 http; 3 uart;
    int mCommType;

    public DataExchanger(int commType, String ip, int port, String ip2, int port2) {
        this.mCommType = commType;
        mCommunication = createCommunication(ip, port, ip2, port2);
    }

    public void doSequenceExchange(String firstTag, byte[] firstData, SequenceHandler handler){
        if (handler == null) {
            //logger.error("回调接收器为空，不发送数据");
            return;
        }
        handler.bindClient(this, true);
        //handler.sendNext(firstTag, firstData);
    }

    /**
     * 执行数据交换,目前只考虑短连接
     * @param clienData 待发送数据
     * @return  收到的返回数据
     */
    public byte[] doExchange(byte[] clienData){
        int count;
        Log.d("kang", "start doExchange");
        if (!mCommunication.connect()){
            //logger.error("^_^ 连接服务器失败！ ^_^");
            //Log.d("CENTERM APP", "HERE");
            throw new CommunicateException(1, "连接服务器失败");
        }
        //有数据就发送。无数据则直接进入接收
        long beforeTime = System.currentTimeMillis();
        if (clienData != null && clienData.length > 0){
            count = mCommunication.sendData(clienData);
            if (count != clienData.length){
                //logger.error("^_^ 发送数据失败！ ^_^");
                mCommunication.disconnect();
                throw new CommunicateException(2, "发送数据失败");
            }
        }

        byte[] receiveBuffer = mCommunication.receivedData(0);
        long afterTime = System.currentTimeMillis();
        long secDiffTime = (afterTime - beforeTime);

        Log.d("check_time", "doexchange:" + secDiffTime);
        if (receiveBuffer == null || receiveBuffer.length == 0){
            //logger.error("^_^ 接收数据失败！ ^_^");
            mCommunication.disconnect();
            throw new CommunicateException(3, "接收数据失败");
        }

        mCommunication.disconnect();
        return receiveBuffer;
    }

    /**
     * 执行数据交换,目前只考虑短连接
     * @param clienData 待发送数据
     * @return  收到的返回数据
     */
    public byte[] doExchange(byte[] clienData, CustomSocketListener customSocketListener){
        int count;
        Log.d("kang", "start doExchange");

        if (!mCommunication.connect(customSocketListener)){
            //logger.error("^_^ 连接服务器失败！ ^_^");
             Log.d("CENTERM APP", "HERE");
             throw new CommunicateException(1, "Connection Failure");
        }
        //有数据就发送。无数据则直接进入接收
        long beforeTime = System.currentTimeMillis();
        if (clienData != null && clienData.length > 0){
            count = mCommunication.sendData(clienData);
            if (count != clienData.length){
                //logger.error("^_^ 发送数据失败！ ^_^");
                mCommunication.disconnect();
                throw new CommunicateException(2, "Failed to Send Data");
            }
        }

        Thread.currentThread().yield();
        Log.d("kang","send finish");
        //byte[] receiveBuffer = mCommunication.receivedData(0);
        byte[] receiveBuffer = mCommunication.receivedData(0,customSocketListener);
        long afterTime = System.currentTimeMillis();
        long secDiffTime = (afterTime - beforeTime);

        Log.d("check_time", "doexchange" + secDiffTime);
        if (receiveBuffer == null || receiveBuffer.length == 0)
        {
            //logger.error("^_^ 接收数据失败！ ^_^");
            mCommunication.disconnect();
            throw new CommunicateException(3, "Failed to Receive Data");
        }

        mCommunication.disconnect();
        //customSocketListener.Received(receiveBuffer);
        return receiveBuffer;
    }

    private ICommunication createCommunication(String ip, int port, String ip2, int port2) {
        ICommunication communication = null;
        switch (mCommType) {
            case COMM_TYPE_TCP:
                communication = new TcpCommunication(ip, port, ip2, port2);
                break;
            default:
                break;
        }

        return communication;
    }
    }
