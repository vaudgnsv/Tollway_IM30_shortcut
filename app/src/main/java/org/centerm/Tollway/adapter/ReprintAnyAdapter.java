package org.centerm.Tollway.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.database.TransTemp;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReprintAnyAdapter extends RecyclerView.Adapter<ReprintAnyAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<TransTemp> voidList = null;
    private View.OnClickListener onClickListener = null;
    final static int LIMIT = 9; //show 10 item มีบัค โชว์ผิด จริงๆต้องโชว์ 10 ล่าสุด ไม่ใช่ 10 อันแรก
    public ReprintAnyAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_reprint_list, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        ///SINN 20180911 reprint any set void font
        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");

//        holder.traceLabel.setText(voidList.get(position).getEcr());

        holder.traceLabel.setText(voidList.get(position).getEcr());//K.GAME 181017

//        holder.cardNoLabel.setText(voidList.get(position).getCardNo());
//        holder.amountLabel.setText(decimalFormat.format(Double.valueOf(voidList.get(position).getAmount())));
        if (voidList.get(position).getVoidFlag().equals("Y")) {
//            holder.typeLabel.setText("VOID");
            holder.amountLabel.setTextColor(Color.RED);
            holder.amountLabel.setText("-" + decimalFormat.format(Double.valueOf(voidList.get(position).getAmount().replaceAll(",",""))));
        } else {
            holder.amountLabel.setTextColor(Color.GREEN);
            holder.amountLabel.setText(decimalFormat.format(Double.valueOf(voidList.get(position).getAmount().replaceAll(",",""))));
//            holder.typeLabel.setText("SALE");
        }

//20180912 SINN Add detail icon & trans name
//        holder.reprint_any_icon
//        holder.reprint_any_transname
        if(voidList.get(position).getHostTypeCard().equalsIgnoreCase("POS")||
                voidList.get(position).getHostTypeCard().equalsIgnoreCase("EPS")||
                voidList.get(position).getHostTypeCard().equalsIgnoreCase("TMS"))
        {
//            holder.reprint_any_transname.setText("เครดิต");
//            holder.reprint_any_transname.setText("บัตรเดบิต/เครดิต");  //SINN 20181119 change wording.
            holder.reprint_any_transname.setText("บัตรเครดิต/เดบิต");  // Paul_20190205
            holder.reprint_any_icon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_card));
        }
        else if(voidList.get(position).getHostTypeCard().equalsIgnoreCase("QR"))
        {
//            holder.reprint_any_transname.setText("คิวอาร์โค๊ด");
            holder.reprint_any_transname.setText("คิวอาร์โค้ด");    // Paul_20190205
            holder.reprint_any_icon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_qr));
        }
        else if(voidList.get(position).getHostTypeCard().equalsIgnoreCase("GHC"))
        {
            holder.reprint_any_transname.setText("สิทธิรักษาพยาบาล");
            holder.reprint_any_icon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_plus));
        }


//        holder.reprint_any_date.setText(timeformat(voidList.get(position).getTransTime()));
        holder.reprint_any_date.setText(dateThai(voidList.get(position).getTransDate())+" "+timeformat(voidList.get(position).getTransTime()));
        Log.d("1919_ReprintAny",voidList.get(position).getTransDate());

//END 20180912 SINN Add detail icon & trans name


//        holder.timeLabel.setText(voidList.get(position).getTransTime());
        holder.voidLinearLayout.setTag(position);
    }

    public static String dateThai(String strDate)
    {
        String Months[] = {
                "มกราคม", "กุมภาพันธ์ ", "มีนาคม", "เมษายน",

                "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม",

                "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};

        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        int year=0,month=0,day=0;
        try {
            Date date = df.parse(strDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DATE);
        } catch (ParseException e) {
// TODO Auto-generated catch block
            e.printStackTrace();

        }


        return String.format("%s %s %s", day,Months[month],year+543);

    }



    public String timeformat(String szTime) {
        String timeformat = "";
        timeformat =szTime.toString().replaceAll(":","");
        return timeformat.substring(0,2)+":"+timeformat.substring(2,4);
    }


    @Override
    public int getItemCount() {
//        return voidList != null ? voidList.size() : 0;
//        if (voidList == null){
//            return 0;
//        }
//        else {
//            return Math.min(voidList.size(), LIMIT) + 1; //taking the footer into account
//        }
        //K.GAME 181022
        if (voidList == null) {
            return 0;
        } else {
            if (voidList.size() > LIMIT)
                return Math.min(voidList.size(), LIMIT) + 1; //t
            else
                return voidList != null ? voidList.size() : 0;
            // aking the footer into account
        }
        //END K.GAME 181022
    }

    public void setItem(ArrayList<TransTemp> item) {
        voidList = item;
    }

    public void clear() {
        if (voidList != null) {
            voidList.clear();
            notifyDataSetChanged();
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public TransTemp getItem(int position) {
        return voidList.get(position);
    }

    public TransTemp getItemWithErcInvoid(String erc) {
        StringBuilder ercNumber = new StringBuilder();
        if (erc.length() < 6) {
            for (int i = erc.length(); i < 6; i++) {
                ercNumber.append("0");
            }
        } else {
            ercNumber.insert(0, erc);
        }
        for (int i = 0; i < voidList.size(); i++) {
            if (voidList.get(i).getEcr().equalsIgnoreCase(ercNumber.toString())) {
                return voidList.get(i);
            }
        }
        return null;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout voidLinearLayout;
        private TextView traceLabel;
        //        private TextView cardNoLabel;
        private TextView amountLabel;
        //        private TextView typeLabel;
//        private TextView timeLabel;
//20180912 SINN Add detail icon & trans name
        private ImageView reprint_any_icon;
        private TextView reprint_any_transname;
        private TextView reprint_any_date;

        public ViewHolder(View itemView) {
            super(itemView);
            traceLabel = itemView.findViewById(R.id.traceLabel);
//            cardNoLabel = itemView.findViewById(R.id.cardNoLabel);
            amountLabel = itemView.findViewById(R.id.amountLabel);
//            typeLabel = itemView.findViewById(R.id.typeLabel);
//            timeLabel = itemView.findViewById(R.id.timeLabel);
            voidLinearLayout = itemView.findViewById(R.id.voidLinearLayout);
            voidLinearLayout.setOnClickListener(onClickListener);

//20180912 SINN Add detail icon & trans name
            reprint_any_icon = itemView.findViewById(R.id.reprint_any_icon);
            reprint_any_transname =  itemView.findViewById(R.id.reprint_any_transname);
            reprint_any_date =  itemView.findViewById(R.id.reprint_any_date);
        }
    }
}
