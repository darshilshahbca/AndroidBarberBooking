package darshil.dev.androidbarberbooking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import darshil.dev.androidbarberbooking.Common.Common;
import darshil.dev.androidbarberbooking.Interface.IRecyclerItemSelectedListener;
import darshil.dev.androidbarberbooking.Model.Barber;
import darshil.dev.androidbarberbooking.Model.TimeSlot;
import darshil.dev.androidbarberbooking.R;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.MyViewHolder> {

    Context context;
    List<TimeSlot> timeSlotList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        this.timeSlotList = new ArrayList<>();
        cardViewList =  new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        cardViewList =  new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_time_slot, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_time_slot.setText(new StringBuilder(Common.convertTimeSlotToString(i)).toString());
        if(timeSlotList.size() == 0 ) //If all position is available, just show List
        {
            myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            myViewHolder.txt_time_slot_description.setText("Available");
            myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.black));
            myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.black));

        }
        else //If have position is FULL(Booked)
        {
            for(TimeSlot slotValue: timeSlotList)
            {
                //LOOP All time from Server and Set Different color
                int slot = Integer.parseInt(slotValue.getSlot().toString());
                if(slot == i)
                {
                    //We will set tag for all time slot is full
                    //So, Base on TAG, We can set all remain card background without change full time slot
                    myViewHolder.card_time_slot.setTag(Common.DISABLE_TAG);
                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));

                    myViewHolder.txt_time_slot_description.setText("Full");
                    myViewHolder.txt_time_slot_description.setTextColor(context.getResources().getColor(android.R.color.white));
                    myViewHolder.txt_time_slot.setTextColor(context.getResources().getColor(android.R.color.white));
                }
            }
        }
        //Add all card to list (20 Card because we are having 20 slots)
        //No Card Add already in cardViewList
        if(!cardViewList.contains(myViewHolder.card_time_slot))
            cardViewList.add(myViewHolder.card_time_slot);

        //Check if card time slot is available

            myViewHolder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                @Override
                public void onItemSelectedListener(View view, int position) {
                    //Loop all Card in Card List
                    for(CardView cardView : cardViewList)
                    {
                        if(cardView.getTag() == null) //Only available Card time slot be change
                            cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
                    }

                    //Original Code Commented
                    //Our selected card will be change color
                    myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
//
                    //After that, Send Broadcast to enable button Next
                    Intent intent = new Intent(Common.KEY_ENALBE_BUTTON_NEXT);
                    intent.putExtra(Common.KEY_TIME_SLOT, i); //Put Index of Time Slot We have selected
                    intent.putExtra(Common.KEY_STEP, 3);
                    localBroadcastManager.sendBroadcast(intent);

//                    //Code Added by Dashil
//                    if(myViewHolder.card_time_slot.getTag() == null) {
//                        myViewHolder.card_time_slot.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
//
//                        //After that, Send Broadcast to enable button Next
//                        Intent intent = new Intent(Common.KEY_ENALBE_BUTTON_NEXT);
//                        intent.putExtra(Common.KEY_TIME_SLOT, i); //Put Index of Time Slot We have selected
//                        intent.putExtra(Common.KEY_STEP, 3);
//                        localBroadcastManager.sendBroadcast(intent);
//                    } else {
//                        Intent intent = new Intent(Common.KEY_DISABLE_BUTTON_NEXT);
//                        localBroadcastManager.sendBroadcast(intent);
//                    }
//                    //Code Added by Darshil

                }
            });
        }


    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txt_time_slot, txt_time_slot_description;
        CardView card_time_slot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_time_slot = (CardView)itemView.findViewById(R.id.card_time_slot);
            txt_time_slot = (TextView)itemView.findViewById(R.id.txt_time_slot);
            txt_time_slot_description = (TextView)itemView.findViewById(R.id.txt_time_slot_description);


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}
