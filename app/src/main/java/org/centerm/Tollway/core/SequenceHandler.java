package org.centerm.Tollway.core;

//import com.centerm.foreigncpay.CpayApplication;
//import com.centerm.foreigncpay.bean.RespStatusCode;

//import org.apache.log4j.Logger;

/**
 * 队列式网络请求的回调接收器{@link SocketClient#sendSequenceData(byte[], String, SequenceHandler)}
 * author:wanliang527</br>
 * date:2016/11/20</br>
 */

public abstract class SequenceHandler {

    //private Logger logger = Logger.getLogger(SequenceHandler.class);
    private DataExchanger client;
    private ResponseHandler handler;
    private String currentReqTag;
    private boolean isSync;

    public void bindClient(DataExchanger client, boolean isSync) {
        this.client = client;
        this.isSync = isSync;
        handler = new ResponseHandler() {
            @Override
            public void onSuccess(String statusCode, String msg, byte[] data) {
                _return(currentReqTag, data, statusCode, msg);
            }

            @Override
            public void onFailure(String code, String msg, Throwable error) {
                _return(currentReqTag, null, code, msg);
            }
        };
    }

    private void _return(String reqTag, byte[] respData, String code, String msg) {
        //logger.info("队列式网络请求==>[" + reqTag + "]结果返回==>" + (respData != null));
        onReturn(reqTag, respData, code, msg);
    }

    protected abstract void onReturn(String reqTag, byte[] respData, String code, String msg);


    public void finish() {
        //logger.info("队列式网络请求==>结束");
    }
}
