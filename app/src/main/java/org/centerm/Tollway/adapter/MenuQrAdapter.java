package org.centerm.Tollway.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.model.MenuQr;

import java.util.ArrayList;
import java.util.List;

public class MenuQrAdapter extends RecyclerView.Adapter<MenuQrAdapter.ViewHolder> {

    private Context context ;
    private List<MenuQr> menuName;
    private View.OnClickListener onClickListener;

    public MenuQrAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_menu_qr,parent,false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.iconQrImage.setImageResource(menuName.get(position).getImage());
        holder.nameMenuLabel.setText(menuName.get(position).getName());
        holder.menuCardView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return menuName != null ? menuName.size() : 0;
    }

    public void clear() {
        if (menuName != null) {
            menuName.clear();
            notifyDataSetChanged();
        }
    }

    public void addItem(List<MenuQr> item) {
        if (menuName == null) {
            menuName = new ArrayList<>();
        }
        menuName = item;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconQrImage;
        private TextView nameMenuLabel;
        private CardView menuCardView;
        public ViewHolder(View itemView) {
            super(itemView);
            iconQrImage = itemView.findViewById(R.id.iconQrImage);
            nameMenuLabel = itemView.findViewById(R.id.nameMenuLabel);
            menuCardView = itemView.findViewById(R.id.menuCardView);
            menuCardView.setOnClickListener(onClickListener);
        }
    }
}
