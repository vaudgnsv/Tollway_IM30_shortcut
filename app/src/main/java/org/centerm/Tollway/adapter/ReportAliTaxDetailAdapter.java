package org.centerm.Tollway.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.database.QrCode;

import java.text.DecimalFormat;
import java.util.ArrayList;

// Paul_20181219
public class ReportAliTaxDetailAdapter extends RecyclerView.Adapter<ReportAliTaxDetailAdapter.ViewHolder> {

    private Context context;
    private ArrayList<QrCode> taxList = null;

    public ReportAliTaxDetailAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_report_alipay_tax_detail, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
        if (!taxList.get(position).getFee().equals("null")){
            if (taxList.get(position).getVoidFlag().equals("N"))
                holder.amountQrLabel.setText(decimalFormat.format(Double.valueOf(taxList.get(position).getFee().replaceAll(",", ""))));
            else
                holder.amountQrLabel.setText("-"+ decimalFormat.format(Double.valueOf(taxList.get(position).getFee().replaceAll(",", ""))));
        }
        else
        {
            if (taxList.get(position).getVoidFlag().equals("N"))
                holder.amountQrLabel.setText("0.00");
            else
                holder.amountQrLabel.setText("-"+ "0.00");
        }
        holder.traceQrLabel.setText(taxList.get(position).getTrace());
//        Log.d(TAG, "onBindViewHolder: " + taxList.get(position).getReqChannelDtm());
        String date = taxList.get(position).getReqChannelDtm().substring(8,10) + "/" +taxList.get(position).getReqChannelDtm().substring(5,7) + "/" +taxList.get(position).getReqChannelDtm().substring(2,4);
        String timeHH = taxList.get(position).getReqChannelDtm().substring(11,13);
        String timeMM = taxList.get(position).getReqChannelDtm().substring(14,16);
        String timeSS = taxList.get(position).getReqChannelDtm().substring(17,19);
        holder.dateTimeQrLabel.setText(date+" , " + timeHH +":" +timeMM +":" +timeSS);
    }

    @Override
    public int getItemCount() {
        return taxList != null ? taxList.size() : 0;
    }

    public void clear() {
        if (taxList != null) {
            taxList.clear();
            notifyDataSetChanged();
        }
    }

    public void setItem(ArrayList<QrCode> item) {
        if (taxList == null) {
            taxList = new ArrayList<>();
        }
        taxList = item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView traceQrLabel;
        private TextView amountQrLabel;
        private TextView dateTimeQrLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            traceQrLabel = itemView.findViewById(R.id.traceQrLabel);
            amountQrLabel = itemView.findViewById(R.id.amountQrLabel);
            dateTimeQrLabel = itemView.findViewById(R.id.dateTimeQrLabel);
        }
    }
}
