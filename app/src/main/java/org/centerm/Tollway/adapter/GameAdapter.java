package org.centerm.Tollway.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.centerm.Tollway.R;

import java.util.ArrayList;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<String> voidList = null;
    private View.OnClickListener onClickListener = null;

    public GameAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_game_list, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.traceLabel.setText("555");

        holder.reprint_any_transname.setText("คิวอาร์โค้ด");    // Paul_20190205
//        holder.reprint_any_transname.setText("คิวอาร์โค๊ด");
        holder.reprint_any_icon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_qr));

        holder.reprint_any_transname.setText("สิทธิรักษาพยาบาล");
        holder.reprint_any_icon.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_plus));

        holder.reprint_any_date.setText("XX");
        holder.voidLinearLayout.setTag(position);
    }

    @Override
    public int getItemCount() {
        return voidList != null ? voidList.size() : 0;
    }

    public void setItem(ArrayList<String> item) {
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout voidLinearLayout;
        private TextView traceLabel;
        private TextView amountLabel;
        private ImageView reprint_any_icon;
        private TextView reprint_any_transname;
        private TextView reprint_any_date;

        public ViewHolder(View itemView) {
            super(itemView);
            traceLabel = itemView.findViewById(R.id.traceLabel);
            amountLabel = itemView.findViewById(R.id.amountLabel);
            voidLinearLayout = itemView.findViewById(R.id.voidLinearLayout);
            voidLinearLayout.setOnClickListener(onClickListener);

            reprint_any_icon = itemView.findViewById(R.id.reprint_any_icon);
            reprint_any_transname = itemView.findViewById(R.id.reprint_any_transname);
            reprint_any_date = itemView.findViewById(R.id.reprint_any_date);
        }
    }
}
