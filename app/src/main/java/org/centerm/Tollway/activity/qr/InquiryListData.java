package org.centerm.Tollway.activity.qr;

import android.graphics.drawable.Drawable;

import java.text.Collator;
import java.util.Comparator;

public class InquiryListData {
    public Drawable Icon;

    public String Voidflag;
    public String Respflag;

    public String Type;
    public String Date;
    public String Amount;
    public String Trace;

//    public void setIcon(Drawable icon) {
//        this.Icon = icon ;
//    }
//    public void setType(String type) {
//        this.Type = type ;
//    }
//    public void setDate(String date) {
//        this.Date = date ;
//    }
//    public void setAmount(String amount) {
//        this.Amount = amount ;
//    }
//    public void setTrace(String trace) {
//        this.Trace = trace ;
//    }

    /**
     * trace 로 정렬
     */
    public static final Comparator<InquiryListData> ALPHA_COMPARATOR = new Comparator<InquiryListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(InquiryListData mListDate_1, InquiryListData mListDate_2) {
            return sCollator.compare(mListDate_1.Trace, mListDate_2.Trace);
        }
    };

}
