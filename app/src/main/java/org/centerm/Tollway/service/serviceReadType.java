package org.centerm.Tollway.service;

/**
 * Created by yanglj on 2018-01-09.
 */

public class serviceReadType {
    private static serviceReadType instance;
    private byte readType;


    private serviceReadType(){

    }

    public static serviceReadType getInstance() {
        if (instance == null) {
            instance = new serviceReadType();
        }
        return instance;
    }

    public void setrReadType(byte type) {
        this.readType = type;
    }

    public byte getrReadType() {
        return this.readType;
    }

}
