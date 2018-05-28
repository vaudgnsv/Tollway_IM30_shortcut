package org.centerm.land.core;

import android.text.TextUtils;
import android.util.Log;

import com.centerm.smartpos.util.HexUtil;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * Created by Qzhhh on 2016/11/2.
 */

public class TlvUtils {

    /**
     * tlv格式字符串解析成MAP对象
     *
     * @param tlv tlv格式数据
     * @return
     */
    public static Map<String, String> tlvToMap(String tlv) {
        return tlvToMap(HexUtil.hexStringToByte(tlv));
    }

    /**
     * 若tag标签的第一个字节后四个bit为“1111”,则说明该tag占两个字节
     * 例如“9F33”;否则占一个字节，例如“95”
     *
     * @param tlv
     * @return
     */
    public static Map<String, String> tlvToMap(byte[] tlv) {
        Map<String, String> map = new HashMap<String, String>();
        if (tlv == null){
            return map;
        }
        int index = 0;
        while (index < tlv.length) {
            if ((tlv[index] & 0x1F) == 0x1F) { //tag双字节
                byte[] tag = new byte[2];
                System.arraycopy(tlv, index, tag, 0, 2);
                index += 2;

                int length = 0;
                if (tlv[index] >> 7 == 0) {     //表示该L字段占一个字节
                    length = tlv[index];    //value字段长度
                    index++;
                } else { //表示该L字段不止占一个字节

                    int lenlen = tlv[index] & 0x7F; //获取该L字段占字节长度
                    index++;

                    for (int i = 0; i < lenlen; i++) {
                        length = length << 8;
                        length += tlv[index] & 0xff;  //value字段长度 &ff转为无符号整型
                        index++;
                    }
                }

                byte[] value = new byte[length];
                System.arraycopy(tlv, index, value, 0, length);
                index += length;
                map.put(HexUtil.bcd2str(tag), HexUtil.bcd2str(value));
            } else { //tag单字节
                byte[] tag = new byte[1];
                System.arraycopy(tlv, index, tag, 0, 1);
                index++;

                int length = 0;
                if (tlv[index] >> 7 == 0) {    //表示该L字段占一个字节
                    length = tlv[index]; //value字段长度
                    index++;
                } else { //表示该L字段不止占一个字节

                    int lenlen = tlv[index] & 0x7F; //获取该L字段占字节长度
                    index++;

                    for (int i = 0; i < lenlen; i++) {
                        length = length << 8;
                        length += tlv[index] & 0xff;  //value字段长度&ff转为无符号整型
                        index++;
                    }
                }

                byte[] value = new byte[length];
                System.arraycopy(tlv, index, value, 0, length);
                index += length;
                map.put(HexUtil.bcd2str(tag), HexUtil.bcd2str(value));
            }
        }

        return map;
    }


    /**
     * 获取临时存储的tag 的tlv
     *
     * @param tag
     * @return
     */
    public static String getTag(Map<String, String> map, String tag) {
        String tlv = null;
        if (map != null) {
            String tagValue = map.get(tag);
            tlv = getTagForTagValue(tagValue, tag);
        }
        return tlv;
    }

    public static byte[] combine(int... bytes) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(4);
        for (int i = 0; i < bytes.length; i++) {
            bout.write(bytes[bytes.length - i - 1]);
        }
        for (int i = 0; i < (4 - bytes.length); i++) {
            bout.write(0);
        }
        return bout.toByteArray();
    }

    public static String combineReturnString(int... bytes) {
        String tag = "";
        for (int i = 0; i < bytes.length; i++) {
            tag += byteToHex((byte) bytes[i]);
        }
        return tag;
    }

    public static String getTagForTagValue(String tagValue, String tag) {
        String tlv = null;
        try {
            if (!TextUtils.isEmpty(tagValue)) {
                String length = Integer.toHexString(tagValue.length() / 2).toUpperCase();
                length = length.length() % 2 != 0 ? "0" + length : length;
                tlv = tag + length + tagValue;
            }
        } catch (Exception e) {
            Log.e("getTagForTagValue", e.toString());
        }
        return tlv;
    }

    public static String encodingTLV(Map tlvMap) {
        String str = "";
        Iterator iter = tlvMap.entrySet().iterator();
        String tag = "";
        String length = "";
        String value = "";
        Map.Entry entry;
        while (iter.hasNext()) {
            entry = (Map.Entry) iter.next();
            tag = (String) entry.getKey();
            value = (String) entry.getValue();
            //若value长度为奇数，需补一位
            if (value.length() % 2 != 0) {
                value += "F";
            }
            length = Integer.toHexString(value.length() / 2).toUpperCase();
            length = length.length() == 1 ? "0" + length : length;
            str += tag + length + value;
        }
        return str;
    }

    public static String keyValueToTlv(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return encodingTLV(map);
    }
    /**
     * 获取55数据交易
     *
     * @return
     * @throws Exception
     */
    public static String[] getFiled55() throws Exception {
        String[] tags = {
                combineReturnString(0x9F, 0x26),
                combineReturnString(0x9F, 0x27),
                combineReturnString(0x9F, 0x10),
                combineReturnString(0x9F, 0x06),
                combineReturnString(0x9F, 0x37),
                combineReturnString(0x9F, 0x36),
                combineReturnString(0x95),
                combineReturnString(0x9A),
                combineReturnString(0x9C),
                combineReturnString(0x9F, 0x02),
                combineReturnString(0x5F, 0x2A),
                combineReturnString(0x82),
                combineReturnString(0x9F, 0x1A),
                combineReturnString(0x9F, 0x03),
                combineReturnString(0x9F, 0x33),
                combineReturnString(0x9F, 0x34),
                combineReturnString(0x9F, 0x35),
                combineReturnString(0x9F, 0x1E),
                combineReturnString(0x84),
                combineReturnString(0x9F, 0x09),
                combineReturnString(0x9F, 0x41),
//                combineReturnString(0x9B),
                combineReturnString(0x8A),
                combineReturnString(0x9F, 0x74),
                combineReturnString(0x91),
                combineReturnString(0x71),
                combineReturnString(0x72)
        };
        return tags;
    }

    public static String[] getResultTlv() {
        String[] tags = {
                combineReturnString(0x5F, 0x34), // CSN
//            combineReturnString(0x4F), // AID
                combineReturnString(0x9F, 0x06), // AID
                combineReturnString(0x95), // TVR
                combineReturnString(0x9F, 0x36), // ATC
                combineReturnString(0x9F, 0x37), // UNPR NUM
                combineReturnString(0x82), // AIP
                combineReturnString(0x9F, 0x79), // 卡片余额
                combineReturnString(0x9F, 0x26),
                combineReturnString(0x9F, 0x27),
                combineReturnString(0x9F, 0x10),        //Issuer Application Data
                combineReturnString(0x9F, 0x37),        //Random Number
                combineReturnString(0x9F, 0x36),
                combineReturnString(0x95),
                combineReturnString(0x9A),
                combineReturnString(0x9C),
                combineReturnString(0x9F, 0x02),
                combineReturnString(0x5F, 0x2A),
                combineReturnString(0x82),
                combineReturnString(0x9F, 0x1A),
                combineReturnString(0x9F, 0x03),
                combineReturnString(0x9F, 0x33),
                combineReturnString(0x9F, 0x34),
                combineReturnString(0x9F, 0x35),
                combineReturnString(0x9F, 0x1E),
                combineReturnString(0x84),
                combineReturnString(0x9F, 0x09),
                combineReturnString(0x9F, 0x41),
                combineReturnString(0x9F, 0x63),
                combineReturnString(0xDF, 0x31),
                combineReturnString(0x8A),
                combineReturnString(0x5F, 0x28),
                combineReturnString(0x9F, 0x74),
                combineReturnString(0x9F, 0x79),
                combineReturnString(0x9F, 0x51),
                combineReturnString(0x9F, 0x5D),
                combineReturnString(0x9B),
                combineReturnString(0x50),
                combineReturnString(0x9F, 0x12),
                combineReturnString(0x9F, 0x4E) // 商户名称
        };
        return tags;
    }

    /**
     * 非接交易拒绝
     *
     * @return
     * @throws Exception
     */
    public static String[] getQTransToICCard() throws Exception {
        String[] tags = {
                combineReturnString(0x9F, 0x27),
                combineReturnString(0x9F, 0x10),
                combineReturnString(0x9F, 0x37),
                combineReturnString(0x9F, 0x36),
                combineReturnString(0x95),
                combineReturnString(0x9A),
                combineReturnString(0x9C),
                combineReturnString(0x9F, 0x02),
                combineReturnString(0x5F, 0x2A),
                combineReturnString(0x82),
                combineReturnString(0x5A),
                combineReturnString(0x9F, 0x1A),
                combineReturnString(0x9F, 0x34),
                combineReturnString(0x9F, 0x03),
                combineReturnString(0x5F, 0x34)
        };
        return tags;
    }

    /**
     * 冲正
     *
     * @return
     * @throws Exception
     */
    public static String[] getFiled55ForReserve() throws Exception {
        String[] tags = {
                combineReturnString(0x9F, 0x10),
                combineReturnString(0x9F, 0x36),
                combineReturnString(0x95),
                combineReturnString(0x9F, 0x1E),
                combineReturnString(0xDF, 0x31)
        };
        return tags;
    }

    /**
     * 脚本
     *
     * @return
     * @throws Exception
     */
    public static String[] getFiled55ForScriptResult() throws Exception {
        String[] tags = {
                combineReturnString(0x9F, 0x26),
                combineReturnString(0x9F, 0x10),
                combineReturnString(0x9F, 0x37),
                combineReturnString(0x9F, 0x36),
                combineReturnString(0x95),
                combineReturnString(0x9A),
                combineReturnString(0x82),
                combineReturnString(0x9F, 0x1A),
                combineReturnString(0x9F, 0x33),
                combineReturnString(0x9F, 0x1E),
                combineReturnString(0xDF, 0x31)
        };
        return tags;
    }

    /**
     * 脚本
     *
     * @return
     * @throws Exception
     */
    public static String[] getCommonTag() throws Exception {
        String[] tags = {
                combineReturnString(0x5A),
                combineReturnString(0x57),
                combineReturnString(0x5F, 0x34)
        };
        return tags;
    }

    public static String byteToHex(byte b) {
        return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF".charAt(b & 0xf));
    }


}
