package org.centerm.land.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.centerm.land.R;
import org.centerm.land.database.TransTemp;
import org.centerm.land.helper.CardPrefix;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SlipReportAdapter extends RecyclerView.Adapter<SlipReportAdapter.ViewHolder> {

    private Context context;
    private List<TransTemp> voidList = null;

    public SlipReportAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_report_detail, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        holder.cardNameLabel.setText(CardPrefix.getTypeCardName(voidList.get(position).getCardNo()));
        holder.expDateLabel.setText("xx/xx");
        if (voidList.get(position).getVoidFlag().equals("Y")) {
            holder.transactionLabel.setText("VOID SALE");
            holder.amountLabel.setText(" - " + voidList.get(position).getAmount());
        } else {
            holder.transactionLabel.setText("SALE");
            holder.amountLabel.setText(decimalFormat.format(Double.valueOf(voidList.get(position).getAmount())));
        }
        holder.approvalCodeLabel.setText(voidList.get(position).getApprvCode());
        String cutCardStart = voidList.get(position).getCardNo().substring(0, 6);
        String cutCardEnd = voidList.get(position).getCardNo().substring(12, voidList.get(position).getCardNo().length());
        String cardNo = cutCardStart + "XXXXXX" + cutCardEnd;
        holder.cardNumberLabel.setText(cardNo.substring(0,4) + " " + cardNo.substring(4,8) + " " + cardNo.substring(8,12) + " " +cardNo.substring(12,16));
        holder.traceNoLabel.setText(voidList.get(position).getEcr());
        String day = voidList.get(position).getTransDate().substring(6,8);
        String mount = voidList.get(position).getTransDate().substring(4,6);
        String year = voidList.get(position).getTransDate().substring(2,4);
        holder.dateTimeLabel.setText(day+" / " +mount + "/" + year + " , " + voidList.get(position).getTransTime());
    }

    @Override
    public int getItemCount() {
        return voidList != null ? voidList.size() : 0;
    }

    public void setItem(List<TransTemp> item) {
        voidList = item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cardNameLabel;
        private TextView expDateLabel;
        private TextView transactionLabel;
        private TextView approvalCodeLabel;
        private TextView cardNumberLabel;
        private TextView traceNoLabel;
        private TextView amountLabel;
        private TextView dateTimeLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            cardNameLabel = itemView.findViewById(R.id.cardNameLabel);
            expDateLabel = itemView.findViewById(R.id.expDateLabel);
            transactionLabel = itemView.findViewById(R.id.transactionLabel);
            approvalCodeLabel = itemView.findViewById(R.id.approvalCodeLabel);
            cardNumberLabel = itemView.findViewById(R.id.cardNumberLabel);
            traceNoLabel = itemView.findViewById(R.id.traceNoLabel);
            amountLabel = itemView.findViewById(R.id.amountLabel);
            dateTimeLabel = itemView.findViewById(R.id.dateTimeLabel);
        }
    }
}
