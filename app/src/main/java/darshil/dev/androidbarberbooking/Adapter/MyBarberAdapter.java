package darshil.dev.androidbarberbooking.Adapter;

import android.content.Context;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import darshil.dev.androidbarberbooking.Model.Barber;
import darshil.dev.androidbarberbooking.R;

public class MyBarberAdapter extends RecyclerView.Adapter<MyBarberAdapter.MyViewHolder>{

    Context context;
    List<Barber> barberList;

    public MyBarberAdapter(Context context, List<Barber> barberList) {
        this.context = context;
        this.barberList = barberList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_barber, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        myViewHolder.txt_barber_name.setText(barberList.get(i).getName());
        myViewHolder.ratingBar.setRating((float)barberList.get(i).getRating());
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txt_barber_name;
        RatingBar ratingBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_barber_name = (TextView)itemView.findViewById(R.id.txt_barber_name);
            ratingBar = (RatingBar)itemView.findViewById(R.id.rtb_barber);
        }
    }
}
