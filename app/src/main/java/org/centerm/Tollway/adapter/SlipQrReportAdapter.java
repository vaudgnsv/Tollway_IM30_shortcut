package org.centerm.Tollway.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.database.QrCode;

import java.text.DecimalFormat;
import java.util.List;

public class SlipQrReportAdapter extends RecyclerView.Adapter<SlipQrReportAdapter.ViewHolder> {

    private final String TAG = "SlipQrReportAdapter";
    private Context context;
    private List<QrCode> qrCodeList = null;

    public SlipQrReportAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_qr_report_detail, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DecimalFormat decimalFormat = new DecimalFormat("##,###,##0.00");
        holder.amountQrLabel.setText(decimalFormat.format(Double.valueOf(qrCodeList.get(position).getAmount().replaceAll(",",""))));
        holder.traceQrLabel.setText(qrCodeList.get(position).getTrace());
        Log.d(TAG, "onBindViewHolder: " + qrCodeList.get(position).getDate());
        String timeHH = qrCodeList.get(position).getTime().substring(0,2);
        String timeMM = qrCodeList.get(position).getTime().substring(2,4);
        String timeSS = qrCodeList.get(position).getTime().substring(4,6);
        holder.dateTimeQrLabel.setText(qrCodeList.get(position).getDate()+" , " + timeHH +":" +timeMM +":" +timeSS);
    }

    @Override
    public int getItemCount() {
        return qrCodeList != null ? qrCodeList.size() : 0;
    }

    public void setItem(List<QrCode> item) {
        qrCodeList = item;
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
