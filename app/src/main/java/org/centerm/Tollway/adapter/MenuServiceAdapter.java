package org.centerm.Tollway.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.centerm.Tollway.R;
import org.centerm.Tollway.utility.Preference;

import java.util.ArrayList;

public class MenuServiceAdapter extends RecyclerView.Adapter<MenuServiceAdapter.ViewHolder> {
    private Context context = null;
    private ArrayList<String> nameMenuList = null;
    private View.OnClickListener onClickListener = null;

    private int inCntItem;

    public MenuServiceAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.item_menu_service_list, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        if (nameMenuList.get(position).equals("พิมพ์ซ้ำ")) {
        if (nameMenuList.get(position).equals("พิมพ์\nสำเนาสลิป")) {
            holder.img_IconMenu.setImageResource(R.drawable.ic_reprint2);
        }
        if (nameMenuList.get(position).equals("คิวอาร์โค้ด")) {
            holder.img_IconMenu.setImageResource(R.drawable.ic_qr);
        }
//        if (nameMenuList.get(position).equals("รายการขาย")) {
        if (nameMenuList.get(position).equals("ชำระค่าทางด่วน")) {
            holder.img_IconMenu.setImageResource(R.drawable.ic_credit2);
        }
        if (nameMenuList.get(position).equals("พิมพ์\nรายงาน")) {
            holder.img_IconMenu.setImageResource(R.drawable.ic_print);
        }
        if (nameMenuList.get(position).equals("ใช้สิทธิ์\nรักษาพยาบาล")) {
            holder.img_IconMenu.setImageResource(R.drawable.ic_plus);
        }
        if (nameMenuList.get(position).equals("สรุปยอด\nประจำวัน")) {
            holder.img_IconMenu.setImageResource(R.drawable.ic_transfer_copy);
        }
        if (nameMenuList.get(position).equals("ยกเลิก\nรายการ")) {
            holder.img_IconMenu.setImageResource(R.drawable.ic_void_copy);
        }
        if (nameMenuList.get(position).equals("ทำรายการ\nออฟไลน์")) {
            holder.img_IconMenu.setImageResource(R.drawable.ic_offline);
        }
        if (nameMenuList.get(position).equals("ทดสอบ\nโฮซ์ท")) {
            holder.img_IconMenu.setImageResource(R.drawable.ic_host);
        }
        if (nameMenuList.get(position).equals("ตรวจสอบ\nบัตรประชาชน")) {
            holder.img_IconMenu.setImageResource(R.drawable.ic_card_id);
        }
        if (nameMenuList.get(position).equals("ตั้งค่า")) {
            holder.img_IconMenu.setImageResource(R.drawable.ic_setting);
        }
//        if (nameMenuList.get(position).equals("")) {
        if (nameMenuList.get(position).equals("")) {
//            holder.img_IconMenu.setVisibility(View.INVISIBLE);
            holder.img_IconMenu.setImageResource(R.color.color_clear);//K.GAME 18097 ทำให้เป็นสีขาว
//            holder.frameLayoutMenuServiceList.setVisibility(View.INVISIBLE); ทำให้มองไม่เห็น
            holder.frameLayoutMenuServiceList.setVisibility(View.INVISIBLE);
        }
        if (!nameMenuList.get(position).equals("")) {

            if (Preference.getInstance(context).getValueString(Preference.KEY_APP_ENABLE).substring(2, 3).equalsIgnoreCase("1")) {//K.GAME 180920 ถ้าเปิด GHC เข้าไปตั่งตัวเลข
                if (position == 0) {
                    holder.nameMenuLabel.setText("1) " + nameMenuList.get(position));
                }
                if (position == 1) {
                    holder.nameMenuLabel.setText("4) " + nameMenuList.get(position));
                }
                if (position == 2) {
                    holder.nameMenuLabel.setText("7) " + nameMenuList.get(position));
                }
                if (position == 3) {
                    holder.nameMenuLabel.setText("2) " + nameMenuList.get(position));
                }
                if (position == 4) {
                    holder.nameMenuLabel.setText("5) " + nameMenuList.get(position));
                }
                if (position == 5) {
                    holder.nameMenuLabel.setText("8) " + nameMenuList.get(position));
                }
                if (position == 6) {
                    holder.nameMenuLabel.setText("3) " + nameMenuList.get(position));
                }
                if (position == 7) {
                    holder.nameMenuLabel.setText("6) " + nameMenuList.get(position));
                }
                if (position == 8) {
                    holder.nameMenuLabel.setText("9) " + nameMenuList.get(position));
                }

                /////////////////////////////
                if (position == 9) {
                    holder.nameMenuLabel.setText("10) " + nameMenuList.get(position));
                }
                if (position == 10) {
                    holder.nameMenuLabel.setText("13) " + nameMenuList.get(position));
                }
                if (position == 11) {
                    holder.nameMenuLabel.setText("16) " + nameMenuList.get(position));
                }
                if (position == 12) {
                    holder.nameMenuLabel.setText("11) " + nameMenuList.get(position));
                }
                if (position == 13) {
                    holder.nameMenuLabel.setText("14) " + nameMenuList.get(position));
                }
                if (position == 14) {
                    holder.nameMenuLabel.setText("17) " + nameMenuList.get(position));
                }
                if (position == 15) {
                    holder.nameMenuLabel.setText("12) " + nameMenuList.get(position));
                }
                if (position == 16) {
                    holder.nameMenuLabel.setText("15) " + nameMenuList.get(position));
                }
                if (position == 17) {
                    holder.nameMenuLabel.setText("18) " + nameMenuList.get(position));
                }
            } else {
                holder.nameMenuLabel.setText(nameMenuList.get(position)); //K.GAME 180920 เวลาไม่เปิด GHC ให้ไม่ตั่งตัวเลข
            }
        } else {
            holder.nameMenuLabel.setText(" " + nameMenuList.get(position));
        }
//        inCntItem++;
//        holder.nameMenuLabel.setText(String.valueOf(inCntItem) + ") " + nameMenuList.get(position));
//        holder.nameMenuLabel.setText(nameMenuList.get(position));
        ////K.GAMR แก้ขัดไปก่อน เปิด 1 4 7
        if (position == 0)
            holder.frameLayoutMenuServiceList.setVisibility(View.VISIBLE);
        if (position == 1)
            holder.frameLayoutMenuServiceList.setVisibility(View.VISIBLE);
        if (position == 2)
            holder.frameLayoutMenuServiceList.setVisibility(View.VISIBLE);
        ////END K.GAMR แก้ขัดไปก่อน เปิด 1 4 7

        holder.frameLayoutMenuServiceList.setTag(position);
    }


    @Override
    public int getItemCount() {
        return nameMenuList != null ? nameMenuList.size() : 0;
    }

    public void setItem(ArrayList<String> item) {
        inCntItem = 0;
        nameMenuList = item;
    }

    public void clear() {
        if (nameMenuList != null) {
            nameMenuList.clear();
            notifyDataSetChanged();
        }
    }


    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_IconMenu; //K.GAME Change Menu
        private TextView nameMenuLabel = null;
        private FrameLayout frameLayoutMenuServiceList = null;

        public ViewHolder(View itemView) {
            super(itemView);
            img_IconMenu = itemView.findViewById(R.id.img_MenuIcon);//K.GAME Change Menu
            nameMenuLabel = itemView.findViewById(R.id.nameMenuLabel);
            frameLayoutMenuServiceList = itemView.findViewById(R.id.frameLayoutMenuServiceList);
            frameLayoutMenuServiceList.setOnClickListener(onClickListener);
        }
    }
}
