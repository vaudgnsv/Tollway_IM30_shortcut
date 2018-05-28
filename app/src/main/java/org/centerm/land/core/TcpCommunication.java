package org.centerm.land.core;

import android.util.Log;

import com.centerm.smartpos.util.HexUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

//import org.apache.log4j.Logger;

/**
 * Created by yuhc on 2017/2/13.
 * 采用TCP连接方式的数据交互。此模块需要放到单独的线程中执行。
 */

public class TcpCommunication implements ICommunication {
    //private Logger logger = Logger.getLogger(this.getClass());
    private final static int RETRY_TIMES = 1;
    private boolean isTimeout;
    private int connection_timeout = 5000;
    private int transaction_timeout = 20000; // dummy for 20 seconds

    /**
     * 服务器IP
     */
    private String serverIP;

    /**
     * 服务器端口
     */
    private int serverPort;

    //客户端
    Socket mClientSocket = null;

    public TcpCommunication(String ip, int port)
    {
        this.serverIP = ip;
        this.serverPort = port;
        Log.d("CENTERM APP","server ip = "+this.serverIP);
        Log.d("CENTERM APP","server port = "+this.serverPort);
    }

    public TcpCommunication() {
        this.serverIP = "192.168.11.1";
        this.serverPort = 5000;
    }

    /**
     * 连接服务器
     *
     * @return true 成功
     */
    @Override
    public Boolean connect() {
        if (mClientSocket != null){
            //logger.debug("^_^ 服务器已连接！^_^");
            Log.d("CENTERM APP","^_^ 服务器已连接！^_^");
            return true;
        }

        //logger.debug("^_^ 服务器IP:" + serverIP + " PORT:" + serverPort + " ^_^");
        Log.d("CENTERM APP", "^_^ 服务器IP:" + serverIP + " PORT:" + serverPort + " ^_^");
        int retryCount = RETRY_TIMES;
        do {
            Log.d("CENTERM APP", "waiting for connecting ... ["+retryCount+"]");
            try {
                mClientSocket = new Socket();
                mClientSocket.setTcpNoDelay(true);
                SocketAddress socketAddress = new InetSocketAddress(serverIP, serverPort);
                mClientSocket.connect(socketAddress, connection_timeout);
                break;
            } catch (IOException e) {
                //logger.error("^_^ " + e.getMessage() + " ^_^");
                Log.d("CENTERM APP", "^_^ " + e.getMessage() + " ^_^");
                if(e.getMessage().toString().startsWith("failed to connect"))
                    Log.d("CENTERM APP", "connection timeout error");
            }
        } while (retryCount-- > 0);

        if (retryCount > 0) {
            return true;
        }

        mClientSocket = null;
        return false;
    }

    @Override
    public Boolean connect(CustomSocketListener customSocketListener) {
        if (mClientSocket != null){
            //logger.debug("^_^ 服务器已连接！^_^");
            return true;
        }

        //logger.debug("^_^ 服务器IP:" + serverIP + " PORT:" + serverPort + " ^_^");
        int retryCount = RETRY_TIMES;
        do {
            try {
                mClientSocket = new Socket();
                mClientSocket.setTcpNoDelay(true);
                SocketAddress socketAddress = new InetSocketAddress(serverIP, serverPort);
                mClientSocket.connect(socketAddress, connection_timeout);
                break;
            } catch (IOException e) {
                //logger.error("^_^ " + e.getMessage() + " ^_^");
                if(e.getMessage().toString().startsWith("failed to connect")){
                    customSocketListener.ConnectTimeOut();
                }else{
                    customSocketListener.Error(e.toString());
                }
            }
            //retryCount = retryCount -  1;
        } while (--retryCount > 0);

        Log.d("CENTERM_APP", "retry count = "+retryCount);
        if (retryCount > 0) {
            return true;
        }

        mClientSocket = null;
        return false;
    }

    @Override
    public int sendData(byte[] data) {
        if (mClientSocket == null){
            //logger.error("^_^ 服务器未连接！^_^");
            Log.d("CENTERM APP", "^_^ 服务器未连接！^_^");
            return -1;
        }
        if (mClientSocket.isOutputShutdown()){
            //logger.error("^_^ 数据发送已被关闭！^_^");
            Log.d("CENTERM APP", "^_^ 数据发送已被关闭！^_^");
            return -2;
        }
        if (data == null || data.length == 0){
            //logger.error("^_^ 待发送的数据为空 ^_^");
            Log.d("CENTERM APP", "^_^ 待发送的数据为空 ^_^");
            return 0;
        }

        try {
            OutputStream os = mClientSocket.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException e) {
            //logger.error("^_^ "+e.getMessage()+" ^_^");
            Log.d("CENTERM APP", "^_^ "+e.getMessage()+" ^_^");
            return -3;
        }

        //logger.debug("^_^ 发送数据："+ HexUtil.bytesToHexString(data));
        Log.d("CENTERM APP", "^_^ 发送数据："+ HexUtil.bytesToHexString(data));
        return data.length;
    }

    @Override
    public byte[] receivedData(int requestLen) {
        if (mClientSocket == null){
            //logger.error("^_^ 服务器未连接！^_^");
            Log.d("CENTERM APP", "^_^ 服务器未连接！^_^");
            throw new CommunicateException(-1,"服务器未连接！");
        }
        if (mClientSocket.isInputShutdown()){
            //logger.error("^_^ 服务器已关闭数据发送！^_^");
            Log.d("CENTERM APP", "^_^ 服务器已关闭数据发送！^_^");
            throw new CommunicateException(-2,"服务器已关闭数据发送！");
        }

        //logger.debug("^_^ 正在接收数据...... ^_^");
        Log.d("CENTERM APP", "^_^ 正在接收数据...... ^_^");
        byte[] receiveBuffer;
        int receivedLen;
//        CountDownTimer receiveTimer = new CommunicationTimer(timeOutS*1000, 1000);

        long expireTimeMs = transaction_timeout + System.currentTimeMillis();
        ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
        try {
            InputStream is = mClientSocket.getInputStream();
            if (requestLen > 0){
                // TODO: 2017/2/14  收取指定长度的数据

            }else {
                isTimeout = false;
                do {
                    if (System.currentTimeMillis() >= expireTimeMs) {
                        isTimeout = true;
                        break;
                    }
                    //处理网络环境不好时，数据的组包、完整性。
                    receivedLen = is.available();
                    if (receivedLen > 0) {
                        receiveBuffer = new byte[receivedLen];
                        if (is.read(receiveBuffer) <= 0) {
                            break;
                        }
                        dataBuffer.write(receiveBuffer);
                        if(isReceivedOver(dataBuffer.toByteArray())) {
                            break;
                        }
                    }
                }while (!isTimeout);

                dataBuffer.close();
            }
        } catch (Exception e) {
            //logger.error("^_^ 数据接收失败:" + e.getMessage() + "^_^");
            Log.d("CENTERM APP", "^_^ 数据接收失败:" + e.getMessage() + "^_^");
            throw new CommunicateException(-2, "数据接收失败:" + e.getMessage());
        }

        if (isTimeout){
            //logger.error("^_^ 数据接收超时 ^_^");
            Log.d("CENTERM APP", "^_^ 数据接收超时 ^_^");
            throw new CommunicateException(-3, "数据接收超时");
        }

        //logger.debug("^_^ 接收到的数据：" + HexUtil.bytesToHexString(dataBuffer.toByteArray()));
        Log.d("CENTERM APP", "^_^ 接收到的数据：" + HexUtil.bytesToHexString(dataBuffer.toByteArray()));
        return dataBuffer.toByteArray();
    }

@Override
    public byte[] receivedData(int requestLen, CustomSocketListener customSocketListener) {
        if (mClientSocket == null){
            //logger.error("^_^ 服务器未连接！^_^");
            throw new CommunicateException(-1,"服务器未连接！");
        }
        if (mClientSocket.isInputShutdown()){
            //logger.error("^_^ 服务器已关闭数据发送！^_^");
            throw new CommunicateException(-2,"服务器已关闭数据发送！");
        }

        //logger.debug("^_^ 正在接收数据...... ^_^");
        byte[] receiveBuffer;
        int receivedLen;
//        CountDownTimer receiveTimer = new CommunicationTimer(timeOutS*1000, 1000);

        long expireTimeMs = transaction_timeout + System.currentTimeMillis();
        ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
        try {
            InputStream is = mClientSocket.getInputStream();
            if (requestLen > 0){
                // TODO: 2017/2/14  收取指定长度的数据

            }else {
                isTimeout = false;
                do {
                    if (System.currentTimeMillis() >= expireTimeMs) {
                        isTimeout = true;
                        break;
                    }
                    //处理网络环境不好时，数据的组包、完整性。
                    receivedLen = is.available();
                    if (receivedLen > 0) {
                        receiveBuffer = new byte[receivedLen];
                        if (is.read(receiveBuffer) <= 0) {
                            break;
                        }
                        dataBuffer.write(receiveBuffer);
                        if(isReceivedOver(dataBuffer.toByteArray())) {
                            break;
                        }
                    }
                }while (!isTimeout);

                dataBuffer.close();
            }
        } catch (Exception e) {
            //logger.error("^_^ 数据接收失败:" + e.getMessage() + "^_^");
            throw new CommunicateException(-2, "数据接收失败:" + e.getMessage());
        }


            if (isTimeout){
                //logger.error("^_^ 数据接收超时 ^_^");
                Log.d("CENTERM APP", "^_^ 数据接收超时 ^_^");
                customSocketListener.TransactionTimeOut();
                throw new CommunicateException(-3, "数据接收超时");
            }

            //logger.debug("^_^ 接收到的数据：" + HexUtil.bytesToHexString(dataBuffer.toByteArray()));
            Log.d("CENTERM APP", "^_^ 接收到的数据：" + HexUtil.bytesToHexString(dataBuffer.toByteArray()));
            customSocketListener.Received(dataBuffer.toByteArray());
            return dataBuffer.toByteArray();
   }

    @Override
    public void disconnect() {
        if (mClientSocket == null){
            //logger.debug("^_^ 无连接 ^_^");
            Log.d("CENTERM APP", "^_^ 无连接 ^_^");
            return;
        }

        try {
            mClientSocket.close();
            mClientSocket = null;
        } catch (IOException e) {
            //logger.error("^_^ 断开连接失败:" + e.getMessage() + "^_^");
            Log.d("CENTERM APP", "^_^ 断开连接失败:" + e.getMessage() + "^_^");
            throw new CommunicateException(-1,"断开连接失败:" + e.getMessage());
        }
    }

    /**
     * 判断数据是否接收完整
     * @param receiveData   已接收的数据
     * @return  true 接收完整
     */
    @Override
    public Boolean isReceivedOver(byte[] receiveData) {
        if (receiveData == null || receiveData.length < 2) {
            return false;
        }

        int longPrex = HexUtil.bytes2short(receiveData) + 2;
        if (receiveData.length < longPrex) {
            return false;
        }

        return true;
    }
}
