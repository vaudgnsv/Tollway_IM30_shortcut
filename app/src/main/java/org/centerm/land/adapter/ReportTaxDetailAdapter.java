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

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReportTaxDetailAdapter extends RecyclerView.Adapter<ReportTaxDetailAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TransTemp> taxList = null;

    public ReportTaxDetailAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_report_tax_detail, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        holder.taxInvoiceNoLabel.setText(taxList.get(position).getTaxAbb());
        if (taxList.get(position).getVoidFlag().equalsIgnoreCase("Y")) {
            holder.productNameLabel.setText("VOID FEE");
        } else {
            holder.productNameLabel.setText("FEE");
        }
        String cutCardStart = taxList.get(position).getCardNo().substring(0, 6);
        String cutCardEnd = taxList.get(position).getCardNo().substring(12, taxList.get(position).getCardNo().length());
        String cardAll = cutCardStart + "XXXXXX" + cutCardEnd;

        holder.cardLabel.setText(cardAll.substring(0, 4) + " " +
                cardAll.substring(4, 8) + " " + cardAll.substring(8, 12) + " " + cardAll.substring(12, 16));
        holder.traceLabel.setText(taxList.get(position).getEcr());
        String dateTime = taxList.get(position).getTransDate().substring(6, 8)
                + "/"
                + taxList.get(position).getTransDate().substring(4, 6)
                + "/"
                + taxList.get(position).getTransDate().substring(0, 4) + " , " + taxList.get(position).getTransTime();
        holder.dateTimeTaxLabel.setText(dateTime);
        holder.feeLabel.setText(decimalFormat.format(Double.valueOf(taxList.get(position).getFee())));
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

    public void setItem(ArrayList<TransTemp> item) {
        if (taxList == null) {
            taxList = new ArrayList<>();
        }
        taxList = item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cardLabel;
        private TextView taxInvoiceNoLabel;
        private TextView productNameLabel;
        private TextView traceLabel;
        private TextView dateTimeTaxLabel;
        private TextView feeLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            cardLabel = itemView.findViewById(R.id.cardLabel);
            taxInvoiceNoLabel = itemView.findViewById(R.id.taxInvoiceNoLabel);
            productNameLabel = itemView.findViewById(R.id.productNameLabel);
            traceLabel = itemView.findViewById(R.id.traceLabel);
            dateTimeTaxLabel = itemView.findViewById(R.id.dateTimeTaxLabel);
            feeLabel = itemView.findViewById(R.id.feeLabel);
        }
    }
}
