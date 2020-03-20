package org.centerm.Tollway.core;

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
    private boolean isTimeout;
    private int connection_timeout = 5000;
    private int transaction_timeout = 20000; // dummy for 20 seconds

    private String serverIP;
    private int serverPort;
    private String serverIP2;
    private int serverPort2;

    Socket mClientSocket = null;
    Socket mClientSocket2 = null;

    public TcpCommunication(String ip, int port, String ip2, int port2)
    {
        this.serverIP = ip;
        this.serverPort = port;
        this.serverIP2 = ip2;
        this.serverPort2 = port2;
        Log.d("THAIVAN APP","server ip = "+this.serverIP);
        Log.d("THAIVAN APP","server port = "+this.serverPort);
        Log.d("THAIVAN APP","server ip2 = "+this.serverIP2);
        Log.d("THAIVAN APP","server port2 = "+this.serverPort2);
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
        System.out.printf("utility:: TcpCommu  connect 0001\n");
        try {
            mClientSocket = new Socket();
            mClientSocket.setTcpNoDelay(true);
            SocketAddress socketAddress = new InetSocketAddress(serverIP, serverPort);
            mClientSocket.connect(socketAddress, connection_timeout);
            return true;
        } catch (IOException e) {
            Log.d("THAIVAN APP", "PRIMARY_IP_CONNECTION_FAIL" + e.getMessage());
            try {
                System.out.printf("utility:: TcpCommu  connect 0002\n");
                mClientSocket = null;
                mClientSocket2 = new Socket();
                mClientSocket2.setTcpNoDelay(true);
                SocketAddress socketAddress2 = new InetSocketAddress(serverIP2, serverPort2);
                mClientSocket2.connect(socketAddress2, connection_timeout);
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.d("THAIVAN APP", "SECONDARY_IP_CONNECTION_FAIL" + e.getMessage());
            }
        }
        mClientSocket2 = null;
        return false;
    }

    @Override
    public Boolean connect(CustomSocketListener customSocketListener) {
        if (mClientSocket != null){
            return true;
        }
        try {
            System.out.printf("utility:: TcpCommu  connect 0003\n");
            mClientSocket = new Socket();
            mClientSocket.setTcpNoDelay(true);
            SocketAddress socketAddress = new InetSocketAddress(serverIP, serverPort);
            mClientSocket.connect(socketAddress, connection_timeout);
            return true;
        } catch (IOException e) {
            Log.d("THAIVAN APP", "PRIMARY_IP_CONNECTION_FAIL" + e.getMessage());
            try {
                System.out.printf("utility:: TcpCommu  connect 0004\n");
                mClientSocket = null;
                mClientSocket2 = new Socket();
                mClientSocket2.setTcpNoDelay(true);
                SocketAddress socketAddress2 = new InetSocketAddress(serverIP2, serverPort2);
                mClientSocket2.connect(socketAddress2, connection_timeout);
                return true;
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.d("THAIVAN APP", "SECONDARY_IP_CONNECTION_FAIL" + e.getMessage());
                if(e.getMessage().startsWith("failed to connect")){
                    customSocketListener.ConnectTimeOut();
                }
                else if(e.getMessage().startsWith("connect timed out")) {
                    customSocketListener.ConnectTimeOut();
                }
                else{
                    customSocketListener.Error(e.toString());
                }
            }
        }
        mClientSocket2 = null;
        return false;
    }

    @Override
    public int sendData(byte[] data) {
        if (mClientSocket == null && mClientSocket2 == null){
            //logger.error("^_^ 服务器未连接！^_^");
            Log.d("CENTERM APP", "^_^ 服务器未连接！^_^");
            return -1;
        }
        if (mClientSocket != null && mClientSocket.isOutputShutdown()){
            //logger.error("^_^ 数据发送已被关闭！^_^");
            Log.d("CENTERM APP", "^_^ 数据发送已被关闭！^_^");
            return -2;
        }
        if (mClientSocket2 != null&& mClientSocket2.isOutputShutdown()){
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
            if (mClientSocket != null){
                System.out.printf("utility:: TcpCommu  connect 0005\n");
                OutputStream os = mClientSocket.getOutputStream();
                os.write(data);
                os.flush();
            }else{
                System.out.printf("utility:: TcpCommu  connect 0006\n");
                OutputStream os = mClientSocket2.getOutputStream();
                os.write(data);
                os.flush();
            }
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
        if (mClientSocket == null && mClientSocket2 == null){
            //logger.error("^_^ 服务器未连接！^_^");
            Log.d("CENTERM APP", "^_^ 服务器未连接！^_^");
            throw new CommunicateException(-1,"服务器未连接！");
        }
        if (mClientSocket != null && mClientSocket.isInputShutdown()){
            //logger.error("^_^ 服务器已关闭数据发送！^_^");
            Log.d("CENTERM APP", "^_^ 服务器已关闭数据发送！^_^");
            throw new CommunicateException(-2,"服务器已关闭数据发送！");
        }
        if (mClientSocket2 != null && mClientSocket2.isInputShutdown()){
            //logger.error("^_^ 服务器已关闭数据发送！^_^");
            Log.d("CENTERM APP", "^_^ 服务器已关闭数据发送！^_^");
            throw new CommunicateException(-2,"服务器已关闭数据发送！");
        }
        //logger.debug("^_^ 正在接收数据...... ^_^");
        Log.d("CENTERM APP", "^_^ 正在接收数据...... ^_^");
        byte[] receiveBuffer;
        int receivedLen;

        long expireTimeMs = transaction_timeout + System.currentTimeMillis();
        ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
        try {
            InputStream is;
            if(mClientSocket != null) {
                is = mClientSocket.getInputStream();
            }else{
                is = mClientSocket2.getInputStream();
            }

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
        if (mClientSocket == null && mClientSocket2 == null){
            //logger.error("^_^ 服务器未连接！^_^");
            Log.d("CENTERM APP", "^_^ 服务器未连接！^_^");
            throw new CommunicateException(-1,"服务器未连接！");
        }
        if (mClientSocket != null && mClientSocket.isInputShutdown()){
            //logger.error("^_^ 服务器已关闭数据发送！^_^");
            Log.d("CENTERM APP", "^_^ 服务器已关闭数据发送！^_^");
            throw new CommunicateException(-2,"服务器已关闭数据发送！");
        }
        if (mClientSocket2 != null && mClientSocket2.isInputShutdown()){
            //logger.error("^_^ 服务器已关闭数据发送！^_^");
            Log.d("CENTERM APP", "^_^ 服务器已关闭数据发送！^_^");
            throw new CommunicateException(-2,"服务器已关闭数据发送！");
        }

        //logger.debug("^_^ 正在接收数据...... ^_^");
        byte[] receiveBuffer;
        int receivedLen;
//        CountDownTimer receiveTimer = new CommunicationTimer(timeOutS*1000, 1000);

        long expireTimeMs = transaction_timeout + System.currentTimeMillis();
        ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
        try {
            InputStream is;
            if(mClientSocket != null) {
                is = mClientSocket.getInputStream();
            }else{
                is = mClientSocket2.getInputStream();
            }
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
