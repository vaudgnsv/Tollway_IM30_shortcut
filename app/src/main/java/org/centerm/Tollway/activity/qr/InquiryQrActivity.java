package org.centerm.Tollway.activity.qr;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.alipay.AliConfig;
import org.centerm.Tollway.alipay.AliVoidActivity;
import org.centerm.Tollway.database.QrCode;
import org.centerm.Tollway.utility.Utility;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class InquiryQrActivity extends AppCompatActivity {

    private final String TAG = "InquiryQrActivity";

    private ImageView img_search;
    private EditText edit_invoice;
    private ListView listview  = null;
    private ListViewAdapter mAdapter = null;

    private Realm realm = null;
    private int cnt;

    private Drawable icon;
    private String type;
    private String trace;
    private String amount;
    private String fee;
    private String amtplusfee;

    private String year;
    private String date;
    private String time;

    private String color_flag;
    private String void_flag;
    private String qrTid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.printf("utility:: %s onCreate 0001 \n",TAG);
        setContentView( R.layout.activity_inquiry_qr);
        initWidget();
    }

    public void initWidget() {
        // super.initWidget();
        img_search = findViewById( R.id.searchInvoiceImage);
        edit_invoice = findViewById( R.id.invoiceEt);
        listview = findViewById( R.id.mList);
        setMenuList();

        edit_invoice.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchQrDB();
                }
                return false;
            }
        });
    }

    private void searchQrDB() {
        trace = edit_invoice.getText().toString();
        trace = checkLength(trace, 6);

        //DB DATA
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        RealmResults<QrCode> saleTemp = realm.where(QrCode.class).equalTo("trace", trace).findAll();

        if(saleTemp.size() > 0){
            Intent service = new Intent(InquiryQrActivity.this, AliVoidActivity.class);
            service.putExtra("INVOICE", trace);
            service.putExtra("TYPE", AliConfig.Inquiry);
            startActivity(service);
            finish();
        }else{
            Utility.customDialogAlert(InquiryQrActivity.this, "ไม่พบรายการ", new Utility.OnClickCloseImage() {
                @Override
                public void onClickImage(Dialog dialog) {
                    dialog.dismiss();
                }
            });
        }
    }

    private void setMenuList() {

        mAdapter = new ListViewAdapter(this);
        listview.setAdapter(mAdapter);
        setTransData();
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQrDB();
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){
                System.out.printf("utility:: %s setMenuList listview position = %d \n",TAG,position);

                //DB DATA
                if (realm == null) {
                    realm = Realm.getDefaultInstance();
                }
                //                RealmResults<QrCode> saleTemp = realm.where(QrCode.class).findAll();
//                RealmResults<QrCode> saleTemp = realm.where(QrCode.class).findAll().sort("date", Sort.DESCENDING).sort("time",Sort.DESCENDING);
                RealmResults<QrCode> saleTemp = realm.where(QrCode.class).findAll().sort("trace", Sort.DESCENDING);
                trace =  saleTemp.get(position).getTrace();
//                realm = null;
                System.out.printf("utility:: %s setMenuList listview trace = %s \n",TAG,trace);

                qrTid =  saleTemp.get(position).getQrTid();
                Log.d(TAG,"qrTid:"+qrTid);

                Intent service = new Intent(InquiryQrActivity.this, AliVoidActivity.class);
                service.putExtra("INVOICE", trace);
                service.putExtra("TYPE", AliConfig.Inquiry);

                service.putExtra("QrTid",qrTid);

                startActivity(service);
                finish();
            }
        });
    }

    private void setTransData() {

        // K.jeff 20181103
        //Max ID
//        if (realm == null) {
//            realm = Realm.getDefaultInstance();
//        }
//        Number currentId = realm.where(QrCode.class).max("id");
//        realm = null;
//
//        if(currentId != null)
//            cnt = currentId.intValue();
//        else
//            cnt = 0;

        //DB DATA
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }

//        RealmResults<QrCode> saleTemp = realm.where(QrCode.class).findAll();
        RealmResults<QrCode> saleTemp = realm.where(QrCode.class).findAll().sort("trace", Sort.DESCENDING);

        cnt = saleTemp.size();       // K.jeff 20181103

        if(cnt >0){

            for(int i =0; i < cnt; i++){
                type = saleTemp.get(i).getHostTypeCard();

                switch(type){
                    case "QR" :
//                        icon = getResources().getDrawable(R.drawable.ic_qr);
//                        date =  saleTemp.get(i).getDate();
//                        time = saleTemp.get(i).getTime();
//                        date = checkDate_qr(date, time);
//                        amount =  saleTemp.get(i).getAmount();
//                        trace =  saleTemp.get(i).getTrace();
//                        color_flag = "0";
//                        void_flag = "N";
//                        mAdapter.addItem(icon, type, date, amount, trace, color_flag, void_flag);
                        icon = getResources().getDrawable( R.drawable.ic_qr);
                        date =  saleTemp.get(i).getDate();
                        time = saleTemp.get(i).getTime();
                        date = checkDate_qr(date, time);
                        amount =  saleTemp.get(i).getAmount();
                        trace =  saleTemp.get(i).getTrace();

                        if(saleTemp.get(i).getStatusSuccess().equals("1"))
                            color_flag = "1";
                        else if(saleTemp.get(i).getStatusSuccess().equals("0"))
                            color_flag = "0";
                        else
                            color_flag = "2";

                        void_flag = "N";
                        mAdapter.addItem(icon, type, date, amount, trace, color_flag, void_flag);
                        break;
                    case "WECHAT" :
                        icon = getResources().getDrawable( R.drawable.ic_wechat);
                        date =  saleTemp.get(i).getReqChannelDtm();
                        date = checkDate(date);
                        amount =  saleTemp.get(i).getAmt();
                        fee =  saleTemp.get(i).getFee();
                        amtplusfee =  saleTemp.get(i).getAmtplusfee();
                        trace =  saleTemp.get(i).getTrace();
                        color_flag = saleTemp.get(i).getRespcode();
                        void_flag = saleTemp.get(i).getVoidFlag();

                        if(!fee.equals("null") && !fee.isEmpty()) //20181115Jeff
                            mAdapter.addItem(icon, type +" PAY", date, amtplusfee, trace, color_flag, void_flag);
                        else
                            mAdapter.addItem(icon, type +" PAY", date, amount, trace, color_flag, void_flag);

                        break;
                    case "ALIPAY" :
                        icon = getResources().getDrawable( R.drawable.ic_alipay);
                        date =  saleTemp.get(i).getReqChannelDtm();
                        date = checkDate(date);
                        amount =  saleTemp.get(i).getAmt();
                        fee =  saleTemp.get(i).getFee();
                        amtplusfee =  saleTemp.get(i).getAmtplusfee();
                        trace =  saleTemp.get(i).getTrace();
                        color_flag = saleTemp.get(i).getRespcode();
                        void_flag = saleTemp.get(i).getVoidFlag();

                        if(!fee.equals("null") && !fee.isEmpty()) //20181115Jeff
                            mAdapter.addItem(icon, type, date, amtplusfee, trace, color_flag, void_flag);
                        else
                            mAdapter.addItem(icon, type, date, amount, trace, color_flag, void_flag);

                        break;
                }
            }
        }
        realm = null;
    }

    private String checkDate_qr(String date, String time) {
        //15/10/2018
        //170936
        String tmp;
        String tmp_year;
        String tmp_month;
        String tmp_day;
        String tmp_time;

        //year
        tmp_year = date.substring(6,10);
//        tmp_year = String.valueOf(Integer.parseInt(tmp_year)+483);
        tmp_year = String.valueOf(Integer.parseInt(tmp_year)+543);//K.GAME 181024

        //month
        tmp_month = date.substring(3,5);
        switch (tmp_month){
            case "01":
                tmp_month = "มกราคม"; //JAN
                break;
            case "02":
                tmp_month = "กุมภาพันธ์"; //FEB
                break;
            case "03":
                tmp_month = "มีนาคม"; //MAR
                break;
            case "04":
                tmp_month = "เมษายน"; //APR
                break;
            case "05":
                tmp_month = "พฤษภาคม"; //MAY
                break;
            case "06":
                tmp_month = "มิถุนายน"; //JUN
                break;
            case "07":
                tmp_month = "กรกฎาคม"; //JUL
                break;
            case "08":
                tmp_month = "สิงหาคม"; //AUG
                break;
            case "09":
                tmp_month = "กันยายน"; //SEP
                break;
            case "10":
                tmp_month = "ตุลาคม"; //OCT
                break;
            case "11":
                tmp_month = "พฤศจิกายน"; //NOV
                break;
            case "12":
                tmp_month = "ธันวาคม"; //DEC
                break;
        }
        //day
        tmp_day = date.substring(0,2);
        //Time
        tmp_time = time.substring(0,4);
        tmp_time = tmp_time.substring(0,2) + ":" + tmp_time.substring(2,4);

        tmp = tmp_day +" "+ tmp_month +" "+ tmp_year +" "+ tmp_time;

        return  tmp;
    }

    private String checkDate(String date) {
        //2018-10-15 16:03:30.596
        String tmp;
        String tmp_year;
        String tmp_month;
        String tmp_day;
        String tmp_time;

        //year
        tmp_year = date.substring(0,4);
//        tmp_year = String.valueOf(Integer.parseInt(tmp_year)+483);
        tmp_year = String.valueOf(Integer.parseInt(tmp_year)+543);//K.GAME 181024

        //month
        tmp_month = date.substring(5,7);
        switch (tmp_month){
            case "01":
                tmp_month = "มกราคม"; //JAN
                break;
            case "02":
                tmp_month = "กุมภาพันธ์"; //FEB
                break;
            case "03":
                tmp_month = "มีนาคม"; //MAR
                break;
            case "04":
                tmp_month = "เมษายน"; //APR
                break;
            case "05":
                tmp_month = "พฤษภาคม"; //MAY
                break;
            case "06":
                tmp_month = "มิถุนายน"; //JUN
                break;
            case "07":
                tmp_month = "กรกฎาคม"; //JUL
                break;
            case "08":
                tmp_month = "สิงหาคม"; //AUG
                break;
            case "09":
                tmp_month = "กันยายน"; //SEP
                break;
            case "10":
                tmp_month = "ตุลาคม"; //OCT
                break;
            case "11":
                tmp_month = "พฤศจิกายน"; //NOV
                break;
            case "12":
                tmp_month = "ธันวาคม"; //DEC
                break;
        }
        //day
        tmp_day = date.substring(8,10);
        //Time
        tmp_time = date.substring(11,16);

        tmp = tmp_day +" "+ tmp_month +" "+ tmp_year +" "+ tmp_time;

        return  tmp;
    }

    private class ViewHolder {
        public ImageView img_Icon;
        public TextView txt_type;
        public TextView txt_date;
        public TextView txt_amount;
        public TextView txt_trace;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<InquiryListData> mListData = new ArrayList<InquiryListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
//            return mListData.size();
            //K.GAME 181022
            if (mListData == null) {
                return 0;
            } else {
                if (mListData.size() > 9)
                    return Math.min(mListData.size(), 9) + 1; //t
                else
                    return mListData != null ? mListData.size() : 0;
                // aking the footer into account
            }
            //END K.GAME 181022
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate( R.layout.item_inquiry_list, null);

                holder.img_Icon =  convertView.findViewById( R.id.img_type);
                holder.txt_type = convertView.findViewById( R.id.txt_type);
                holder.txt_date = convertView.findViewById( R.id.txt_date);
                holder.txt_amount = convertView.findViewById( R.id.txt_amount);
                holder.txt_trace = convertView.findViewById( R.id.txt_trace);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            InquiryListData mData = mListData.get(position);

            holder.img_Icon.setImageDrawable(mData.Icon);
            holder.txt_type.setText(mData.Type);
            holder.txt_date.setText(mData.Date);
            holder.txt_trace.setText(mData.Trace);


//            switch(mData.Type){
//                case "QR" :
////                    holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount)));
////                    holder.txt_amount.setTextColor(Color.parseColor("#1DDB16")); //green
//                    //K.GAME 181022
//                    if (mData.Voidflag.equals("Y")) {
//                        holder.txt_amount.setTextColor(Color.parseColor("#FF0000")); //red
//                        holder.txt_amount.setText("- " + decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));//K.GAME 181024 replaceAll
//                    } else if (color_flag.equalsIgnoreCase("1")) {
//                        holder.txt_amount.setTextColor(Color.parseColor("#1DDB16")); //green
//                        holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));//K.GAME 181024 replaceAll
//                    } else if (color_flag.equalsIgnoreCase("0")) {
//                        holder.txt_amount.setTextColor(Color.parseColor("#F9C629")); //yellow //K.GAME 181024
//                        holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));//K.GAME 181024 replaceAll
//                    }
//                    //END K.GAME 181022
//                    break;
//                case "WECHATPAY" :
//                case "ALIPAY" :
//                    //Success
//                    if(mData.Respflag.equals("0")){
//                        if(mData.Voidflag.equals("N")){
//                            holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount)));
//                            holder.txt_amount.setTextColor(Color.parseColor("#1DDB16")); //green
//                    //VOID
//                        }else{
//                            holder.txt_amount.setText("-"+decimalFormat.format(Double.valueOf(mData.Amount)));
//                            holder.txt_amount.setTextColor(Color.parseColor("#CC3333")); //red
//                        }
//                    }else{
//                     //Time out
//                        if(mData.Voidflag.equals("Y")){
//                            holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount)));
//                            holder.txt_amount.setTextColor(Color.parseColor("#CC3333")); //red
//                        }else{
//                     //Inquiry
//                            holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount)));
//                            holder.txt_amount.setTextColor(Color.parseColor("#F9C629"));
//                        }
//                    }
//                    break;
//            }

            switch(mData.Type){
                case "QR" :
//                    //K.GAME 181022
//                    if (mData.Voidflag.equals("Y")) {
//                        holder.txt_amount.setTextColor(Color.parseColor("#FF0000")); //red
//                        holder.txt_amount.setText("- " + decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));//K.GAME 181024 replaceAll
//                    } else if (color_flag.equalsIgnoreCase("1")) {
//                        holder.txt_amount.setTextColor(Color.parseColor("#1DDB16")); //green
//                        holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));//K.GAME 181024 replaceAll
//                    } else if (color_flag.equalsIgnoreCase("0")) {
//                        holder.txt_amount.setTextColor(Color.parseColor("#F9C629")); //yellow //K.GAME 181024
//                        holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));//K.GAME 181024 replaceAll
//                    }
//                    //END K.GAME 181022

                    holder.txt_type.setText("คิวอาร์โค้ด");     // Paul_20190205

                    //Jeff 20181025
                    if (mData.Voidflag.equals("Y")) {
                        holder.txt_amount.setTextColor(Color.parseColor("#FF0000")); //red
                        holder.txt_amount.setText("- " + decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));//K.GAME 181024 replaceAll
                    }else{
                        if (mData.Respflag.equals("1")) {
                            holder.txt_amount.setTextColor(Color.parseColor("#1DDB16")); //green
                            holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));//K.GAME 181024 replaceAll
                        } else {
                            holder.txt_amount.setTextColor(Color.parseColor("#F9C629")); //yellow //K.GAME 181024
                            holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));//K.GAME 181024 replaceAll
                        }
                    }

                    break;
                case "WECHAT" :
                case "ALIPAY" :
                    //Success
                    if(mData.Respflag.equals("0")){
                        if(mData.Voidflag.equals("N")){
                            holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));
                            holder.txt_amount.setTextColor(Color.parseColor("#1DDB16")); //green
                            //VOID
                        }else{
                            holder.txt_amount.setText("-"+decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));
                            holder.txt_amount.setTextColor(Color.parseColor("#CC3333")); //red
                        }
                    }else{
                        //Time out
                        if(mData.Voidflag.equals("Y")){
                            holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));
                            holder.txt_amount.setTextColor(Color.parseColor("#CC3333")); //red
                        }else{
                            //Inquiry
                            holder.txt_amount.setText(decimalFormat.format(Double.valueOf(mData.Amount.replaceAll(",", ""))));
                            holder.txt_amount.setTextColor(Color.parseColor("#F9C629"));
                        }
                    }
                    break;
            }
            return convertView;
        }


        public void addItem(Drawable icon, String type, String date, String amount, String trace, String respFlag, String voidfFag){
            InquiryListData addInfo = null;
            addInfo = new InquiryListData();
            addInfo.Icon = icon;
            addInfo.Type = type;
            addInfo.Date = date;
            addInfo.Amount = amount;
            addInfo.Trace = trace;
            addInfo.Respflag = respFlag;
            addInfo.Voidflag = voidfFag;

            mListData.add(addInfo);
            dataChange();
        }

        public void dataChange(){
            mAdapter.notifyDataSetChanged();
        }
    }


    private String checkLength(String trace, int i ) {
        int tmp_lc = 0;

        tmp_lc = trace.length();

        for(int j = 0; j<(i-tmp_lc); j++)
            trace = "0" + trace;

        return trace;
    }
}
