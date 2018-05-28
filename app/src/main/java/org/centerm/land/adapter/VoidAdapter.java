package org.centerm.land.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.centerm.land.R;
import org.centerm.land.database.TransTemp;
import org.centerm.land.model.Void;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class VoidAdapter extends RecyclerView.Adapter<VoidAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<TransTemp> voidList = null;
    private View.OnClickListener onClickListener = null;

    public VoidAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_void_list,parent,false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        holder.traceLabel.setText(voidList.get(position).getTraceNo());
        holder.cardNoLabel.setText(voidList.get(position).getCardNo());
        holder.amountLabel.setText(decimalFormat.format(Float.valueOf(voidList.get(position).getAmount())));
        holder.typeLabel.setText(voidList.get(position).getTransStat());
        holder.timeLabel.setText(voidList.get(position).getTransTime());
        holder.voidLinearLayout.setTag(position);
    }

    @Override
    public int getItemCount() {
        return voidList != null ? voidList.size() : 0;
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

    public void setOnClickListener(View.OnClickListener onClickListener ) {
        this.onClickListener = onClickListener;
    }

    public TransTemp getItem(int position){
        return voidList.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout voidLinearLayout;
        private TextView traceLabel;
        private TextView cardNoLabel;
        private TextView amountLabel;
        private TextView typeLabel;
        private TextView timeLabel;
        public ViewHolder(View itemView) {
            super(itemView);
            traceLabel = itemView.findViewById(R.id.traceLabel);
            cardNoLabel = itemView.findViewById(R.id.cardNoLabel);
            amountLabel = itemView.findViewById(R.id.amountLabel);
            typeLabel = itemView.findViewById(R.id.typeLabel);
            timeLabel = itemView.findViewById(R.id.timeLabel);
            voidLinearLayout = itemView.findViewById(R.id.voidLinearLayout);
            voidLinearLayout.setOnClickListener(onClickListener);
        }
    }
}
