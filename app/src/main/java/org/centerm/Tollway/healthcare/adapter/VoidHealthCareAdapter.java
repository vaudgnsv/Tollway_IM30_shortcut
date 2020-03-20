package org.centerm.Tollway.healthcare.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.healthcare.database.HealthCareDB;

import java.util.ArrayList;

public class VoidHealthCareAdapter extends RecyclerView.Adapter<VoidHealthCareAdapter.ViewHolder> {

    private View.OnClickListener onClickListener;
    private Context context = null;
    private ArrayList<HealthCareDB> healthCareDBS;

    public VoidHealthCareAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_void_list, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.traceLabel.setText(healthCareDBS.get(position).getTraceNo());
        holder.cardNoLabel.setText(healthCareDBS.get(position).getCardNumber());
        holder.amountLabel.setText(healthCareDBS.get(position).getAmount());
        if (healthCareDBS.get(position).getStatusVoid().equalsIgnoreCase("N"))
            holder.typeLabel.setText("SALE");
        else
            holder.typeLabel.setText("VOID");
        holder.timeLabel.setText(healthCareDBS.get(position).getDate() + healthCareDBS.get(position).getTime());
        holder.voidLinearLayout.setTag(position);
    }

    @Override
    public int getItemCount() {
        return healthCareDBS != null ? healthCareDBS.size() : 0;
    }

    public void clear() {
        if (healthCareDBS != null) {
            healthCareDBS.clear();
        }
        notifyDataSetChanged();
    }

    public HealthCareDB getItem(int position) {
        return healthCareDBS.get(position);
    }

    public void setItem(ArrayList<HealthCareDB> item) {
        this.healthCareDBS = item;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
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
