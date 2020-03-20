package org.centerm.Tollway.helper;

import java.util.ArrayList;
import java.util.List;

public class OnUsChoose {
    public static OnUsChoose onUsChooseInstance;

    public static List<BinRange> binRangeList = new ArrayList<BinRange>();

    public static class BinRange{
        String CardPrefix;
        String Category;
        String RouteTo;

        public String getCardPrefix() {
            return CardPrefix;
        }

        public void setCardPrefix(String cardPrefix) {
            CardPrefix = cardPrefix;
        }

        public String getCategory() {
            return Category;
        }

        public void setCategory(String category) {
            Category = category;
        }

        public String getRouteTo() {
            return RouteTo;
        }

        public void setRouteTo(String routeTo) {
            RouteTo = routeTo;
        }
    }

    public static OnUsChoose getInstance(){
        //单例获取对象
        if(onUsChooseInstance==null){
            onUsChooseInstance = new OnUsChoose();
        }
        //执行初始化列表
        if(binRangeList==null||binRangeList.size()==0){
            initBinRangeList();
        }
        return onUsChooseInstance;
    }

    //初始化列表
    public static void initBinRangeList(){
        if(binRangeList==null){
            binRangeList = new ArrayList<BinRange>();
        }
        BinRange binRange1 = new BinRange();
        binRange1.setCardPrefix("0060");
        binRange1.setCategory("ATM");
        binRange1.setRouteTo("TMS");

        BinRange binRange2 = new BinRange();
        binRange2.setCardPrefix("449932");
        binRange2.setCategory("VISA_DEBIT");
        binRange2.setRouteTo("BASE24");

        BinRange binRange3 = new BinRange();
        binRange3.setCardPrefix("453215");
        binRange3.setCategory("VISA_DEBIT");
        binRange3.setRouteTo("BASE24");

        BinRange binRange4 = new BinRange();
        binRange4.setCardPrefix("453216");
        binRange4.setCategory("VISA_DEBIT");
        binRange4.setRouteTo("BASE24");

        BinRange binRange5 = new BinRange();
        binRange5.setCardPrefix("473252");
        binRange5.setCategory("VISA_DEBIT");
        binRange5.setRouteTo("BASE24");

        BinRange binRange6 = new BinRange();
        binRange6.setCardPrefix("473254");
        binRange6.setCategory("VISA_DEBIT");
        binRange6.setRouteTo("BASE24");

        BinRange binRange7 = new BinRange();
        binRange7.setCardPrefix("473256");
        binRange7.setCategory("VISA_DEBIT");
        binRange7.setRouteTo("BASE24");

        BinRange binRange8 = new BinRange();
        binRange8.setCardPrefix("484830");
        binRange8.setCategory("VISA_DEBIT");
        binRange8.setRouteTo("BASE24");

        BinRange binRange9 = new BinRange();
        binRange9.setCardPrefix("484831");
        binRange9.setCategory("VISA_DEBIT");
        binRange9.setRouteTo("BASE24");

        BinRange binRange10 = new BinRange();
        binRange10.setCardPrefix("50436709");
        binRange10.setCategory("ATM_CORPORATE");
        binRange10.setRouteTo("TMS");

        BinRange binRange11 = new BinRange();
        binRange11.setCardPrefix("504367");
        binRange11.setCategory("ATM");
        binRange11.setRouteTo("TMS");

        BinRange binRange12 = new BinRange();
        binRange12.setCardPrefix("621654");
        binRange12.setCategory("UnionPay");
        binRange12.setRouteTo("BASE24");

        BinRange binRange13 = new BinRange();
        binRange13.setCardPrefix("931006");
        binRange13.setCategory("DEBIT_PROMPTCARD");
        binRange13.setRouteTo("BASE24");

        BinRange binRange14 = new BinRange();
        binRange14.setCardPrefix("9310061");
        binRange14.setCategory("WELFARE_CARD");
        binRange14.setRouteTo("BASE24");

        BinRange binRange15 = new BinRange();
        binRange15.setCardPrefix("990006");
        binRange15.setCategory("ATM_THAI_STD");
        binRange15.setRouteTo("TMS");
    }

    public static List<BinRange> getBinRangeList() {
        return binRangeList;
    }

    public static void setBinRangeList(List<BinRange> binRangeList) {
        OnUsChoose.binRangeList = binRangeList;
    }

    public  Boolean checkExist(String cardNo) {
        for (BinRange binRange:binRangeList) {
            if(binRange.getCardPrefix().startsWith(cardNo.trim())){
                return true;
            }
        }
        return false;
    }

    public  String checkCategory(String cardNo) {
        //特殊处理
        if (cardNo.startsWith("50436709")){
            return "ATM_CORPORATE";
        }
        if (cardNo.startsWith("9310061")){
            return "DEBIT_PROMPTCARD";
        }
        for (BinRange binRange:binRangeList){
            if (binRange.getCardPrefix().startsWith(cardNo.trim())){
                return binRange.getCategory();
            }
        }
        return "OTHER";
    }

    public String checkRouteTo(String cardNo){
        //特殊处理
        if (cardNo.startsWith("50436709")){
            return "TMS";
        }
        if (cardNo.startsWith("9310061")){
            return "BASE24";
        }
        for (BinRange binRange:binRangeList){
            if (binRange.getCardPrefix().startsWith(cardNo.trim())){
                return binRange.getRouteTo();
            }
        }
        return "BASE24";
    }

}
