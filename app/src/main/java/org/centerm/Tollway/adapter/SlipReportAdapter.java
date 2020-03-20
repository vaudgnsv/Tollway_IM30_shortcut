package org.centerm.Tollway.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.database.TransTemp;
import org.centerm.Tollway.helper.CardPrefix;

import java.text.DecimalFormat;
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
        View contentView = LayoutInflater.from(context).inflate( R.layout.item_report_detail, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");    // Paul_20190118
        holder.expDateLabel.setText("xx/xx");
        if (voidList.get(position).getVoidFlag().equals("Y")) {
            holder.transactionLabel.setText("VOID");    // Paul_20190208
            holder.amountLabel.setText(" - " + decimalFormat.format(Double.valueOf(voidList.get(position).getAmount())));   // Paul_20190118 100000.00 to 100,000.00
        } else {
            holder.transactionLabel.setText("SALE");
            holder.amountLabel.setText(decimalFormat.format(Double.valueOf(voidList.get(position).getAmount())));
        }
        holder.approvalCodeLabel.setText(voidList.get(position).getApprvCode());

        String typeHost = voidList.get(position).getHostTypeCard();
        if (!typeHost.equalsIgnoreCase("GHC")) {
            holder.cardNameLabel.setText( CardPrefix.getJSONTypeCardName(voidList.get(position).getCardNo()));   //20180807 SINN JSON Card range Config
            holder.cardNumberLabel.setText(CardPrefix.maskviewcard(" ", voidList.get(position).getCardNo()));  // Paul_20190314 last 4 digts after 1111222233334444555 = 1111-2222-3XXX-XXX4-555
        } else {
            String typeSale = voidList.get(position).getTypeSale().substring(1);
            if (typeSale.equalsIgnoreCase("1")) {
                holder.cardNumberLabel.setText(voidList.get(position).getIdCard());
            } else {
                holder.cardNumberLabel.setText(voidList.get(position).getCardNo());
            }

            holder.cardNameLabel.setText("CIVIL SERVANT RIGHT");
        }
        holder.traceNoLabel.setText(voidList.get(position).getEcr());
        String day = voidList.get(position).getTransDate().substring(6,8);
        String mount = voidList.get(position).getTransDate().substring(4,6);
        String year = voidList.get(position).getTransDate().substring(2,4);

        if ((voidList.get(position).getTransTime().contains(":") || voidList.get(position).getTransTime().contains(",") || voidList.get(position).getTransTime().contains(";")) == true) {
            holder.dateTimeLabel.setText(day + "/" + mount + "/" + year + "," + voidList.get(position).getTransTime());
        } else {
            String HH = voidList.get(position).getTransTime().substring(0, 2);
            String mm = voidList.get(position).getTransTime().substring(2, 4);
            String ss = voidList.get(position).getTransTime().substring(4, 6);
            holder.dateTimeLabel.setText(day + "/" + mount + "/" + year + "," + HH + ":" + mm + ":" + ss);
        }
        //////END GAME
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
            cardNameLabel = itemView.findViewById( R.id.cardNameLabel);
            expDateLabel = itemView.findViewById( R.id.expDateLabel);
            transactionLabel = itemView.findViewById( R.id.transactionLabel);
            approvalCodeLabel = itemView.findViewById( R.id.approvalCodeLabel);
            cardNumberLabel = itemView.findViewById( R.id.cardNumberLabel);
            traceNoLabel = itemView.findViewById( R.id.traceNoLabel);
            amountLabel = itemView.findViewById( R.id.amountLabel);
            dateTimeLabel = itemView.findViewById( R.id.dateTimeLabel);
        }
    }
}
