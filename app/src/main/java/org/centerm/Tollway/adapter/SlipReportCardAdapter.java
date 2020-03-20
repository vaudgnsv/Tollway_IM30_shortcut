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

import java.text.DecimalFormat;
import java.util.List;

public class SlipReportCardAdapter extends RecyclerView.Adapter<SlipReportCardAdapter.ViewHolder> {

    private Context context;
    private List<TransTemp> voidList = null;
//    private ArrayList<String> nameMenuList = null;
    private String CardTypeTemp=null;
    private int AllTotalCount = 0;
    private int TotalCount = 0;
    private Double TotalAmount = 0.00;

    public SlipReportCardAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate( R.layout.item_report_card_detail, parent, false);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        AllTotalCount++;
        DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
        System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000000 TotalCount = %d , voidList.size() = %d \n",TotalCount,voidList.size());
        if(!voidList.get( position ).getCardTypeHolder().equalsIgnoreCase(CardTypeTemp) && (CardTypeTemp != null)) {

//            holder.cardTypeLabel.setText(CardTypeTemp);
//            holder.cntLabel.setText( TotalCount + "");
//            holder.amtLabel.setText( decimalFormat.format(TotalAmount) );

            System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000005 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);

//            holder.amtLabel.setVisibility( View.VISIBLE );
//            holder.cardTypeLabel.setVisibility( View.VISIBLE );
//            holder.cntLabel.setVisibility( View.VISIBLE );

//            TotalCount = 1;
            TotalCount++;
            CardTypeTemp = voidList.get( position).getCardTypeHolder();
            if (!voidList.get( position ).getVoidFlag().equals( "Y" )) {
                TotalAmount = Double.valueOf(voidList.get(position).getAmount());
            }
//            else
//            {
//                TotalAmount = 0.00;
//            }
            holder.cardTypeLabel.setText(CardTypeTemp);
            holder.cntLabel.setText( TotalCount + "");
            holder.amtLabel.setText( decimalFormat.format(TotalAmount) );

            System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000006 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
            if(voidList.size() != AllTotalCount) {
// Paul_20181213
                System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000015 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                if(!voidList.get( position+1 ).getCardTypeHolder().equalsIgnoreCase(CardTypeTemp))
                {
                    System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000016 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                    holder.amtLabel.setVisibility( View.VISIBLE );
                    holder.cardTypeLabel.setVisibility( View.VISIBLE );
                    holder.cntLabel.setVisibility( View.VISIBLE );
                    TotalCount = 0;
                    TotalAmount = 0.00;
                }
                else {
                    System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000017 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);

                    holder.amtLabel.setVisibility( View.GONE );
                    holder.cardTypeLabel.setVisibility( View.GONE );
                    holder.cntLabel.setVisibility( View.GONE );
                }



//                holder.amtLabel.setVisibility( View.GONE );
//                holder.cardTypeLabel.setVisibility( View.GONE );
//                holder.cntLabel.setVisibility( View.GONE );
            }
            else
            {
                System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000010 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                holder.amtLabel.setVisibility( View.VISIBLE );
                holder.cardTypeLabel.setVisibility( View.VISIBLE );
                holder.cntLabel.setVisibility( View.VISIBLE );
                TotalCount = 0;
                TotalAmount = 0.00;
            }
        }
        else
        {
            TotalCount++;
            if (!voidList.get( position ).getVoidFlag().equals( "Y" )) {
                TotalAmount += Double.valueOf(voidList.get(position).getAmount());
            }

            CardTypeTemp = voidList.get( position).getCardTypeHolder();
            holder.cardTypeLabel.setText(CardTypeTemp);
            holder.cntLabel.setText( TotalCount + "");
            holder.amtLabel.setText( decimalFormat.format(TotalAmount) );


            System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000007 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
            if(voidList.size() != AllTotalCount) {
                System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000012 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                if(!voidList.get( position+1 ).getCardTypeHolder().equalsIgnoreCase(CardTypeTemp))
                {
                    System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000013 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                    holder.amtLabel.setVisibility( View.VISIBLE );
                    holder.cardTypeLabel.setVisibility( View.VISIBLE );
                    holder.cntLabel.setVisibility( View.VISIBLE );
                    TotalCount = 0;
                    TotalAmount = 0.00;
                }
                else {
                    holder.amtLabel.setVisibility( View.GONE );
                    holder.cardTypeLabel.setVisibility( View.GONE );
                    holder.cntLabel.setVisibility( View.GONE );
                }
            }
            else
            {
                System.out.printf("utility:: SlipReportCardAdapter onBindViewHolder 00000000011 CardTypeTemp = %s,TotalAmount = %s,Count = %d \n",CardTypeTemp,decimalFormat.format(TotalAmount),TotalCount);
                holder.amtLabel.setVisibility( View.VISIBLE );
                holder.cardTypeLabel.setVisibility( View.VISIBLE );
                holder.cntLabel.setVisibility( View.VISIBLE );
                TotalCount = 0;
                TotalAmount = 0.00;
            }
        }
        //////END GAME
    }

    @Override
    public int getItemCount() {
        return voidList != null ? voidList.size() : 0;
    }

//    public void setItem(ArrayList<String> item)
//    {
//        nameMenuList = item;
//    }
    public void setItem(List<TransTemp> item) {
        voidList = item;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cardTypeLabel;
        private TextView cntLabel;
        private TextView amtLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            cardTypeLabel = itemView.findViewById( R.id.cardTypeReportLabel);
            cntLabel = itemView.findViewById( R.id.countReportLabel);
            amtLabel = itemView.findViewById( R.id.amountReportLabel);
        }
    }
}
