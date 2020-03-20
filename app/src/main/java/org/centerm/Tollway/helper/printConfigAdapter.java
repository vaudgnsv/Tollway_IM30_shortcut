package org.centerm.Tollway.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.centerm.Tollway.R;

import java.util.ArrayList;
import java.util.List;

public class printConfigAdapter extends RecyclerView.Adapter<printConfigAdapter.ViewHolder> {

    private List<String> nameMenu;
    private Context context = null;
    private View.OnClickListener onClickListener = null;

    private TextView L1;

    public printConfigAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate( R.layout.item_config, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Label.setText(nameMenu.get(position).toString());
    }


    @Override
    public int getItemCount() {
        return nameMenu != null ? nameMenu.size() : 0;
    }




    public void clear() {
        if (nameMenu != null) {
            nameMenu.clear();
            notifyDataSetChanged();
        }
    }

    public void setItem(List<String> item) {
        if (nameMenu == null) {
            nameMenu = new ArrayList<>();
        }
        nameMenu = item;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView Label;

        public ViewHolder(View itemView) {
            super(itemView);

            Label = itemView.findViewById( R.id.L1);
        }
    }
}



