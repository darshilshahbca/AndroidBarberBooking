package darshil.dev.androidbarberbooking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import darshil.dev.androidbarberbooking.Adapter.MyViewPagerAdapter;
import darshil.dev.androidbarberbooking.Common.Common;
import darshil.dev.androidbarberbooking.Common.NonSwipeViewPager;
import darshil.dev.androidbarberbooking.Model.Barber;
import dmax.dialog.SpotsDialog;

public class BookingActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;
    AlertDialog dialog;
    CollectionReference barberRef;

    @BindView(R.id.step_view)
    StepView stepView;
    @BindView(R.id.view_pager)
    NonSwipeViewPager viewPager;
    @BindView(R.id.btn_previous_step)
    Button btn_previous_step;
    @BindView(R.id.btn_next_step)
    Button btn_next_step;

    //EVENT
    @OnClick(R.id.btn_previous_step)
    void PreviousStep(){
            if(Common.step == 3 || Common.step > 0 )
            {
                Common.step--;
                viewPager.setCurrentItem(Common.step);
                if(Common.step < 3) //Always Enable Next when step <  3
                {
                    btn_next_step.setEnabled(true);
                    setColorButton();
                }


            }
    }

    @OnClick(R.id.btn_next_step)
    void nextClick(){

        if(Common.step < 3 || Common.step == 0)
        {
            Common.step++; //Increase

            if (Common.step == 1 )//After Choose Salon
            {
                if(Common.currentSalon!=null)
                    loadBarberBySalon(Common.currentSalon.getSalonId());
            }
            else if(Common.step == 2 ) //Pick Time Slot
            {
                if(Common.currentBarber!=null)
                    loadTimeSlotOfBarber(Common.currentBarber.getBarberId());

//                        Toast.makeText(this, ""+Common.currentBarber.getBarberId(), Toast.LENGTH_SHORT).show();

            }
            else if(Common.step == 3 ) //Confirm
            {
                if(Common.currentTimeSlot!=-1)
                    confirmBooking();

            }

            viewPager.setCurrentItem(Common.step);
        }


//        Toast.makeText(this, ""+Common.currentSalon.getSalonId(), Toast.LENGTH_SHORT).show();
    }

    private void confirmBooking() {
        //Send Broadcast to Fragment four
        Intent intent = new Intent(Common.KEY_CONFIRM_BOOKING);
        localBroadcastManager.sendBroadcast(intent);

    }

    private void loadTimeSlotOfBarber(String barberId) {
        //Send LocalBroadcast to Segment Step 3
        Intent intent = new Intent(Common.KEY_DISPLAY_TIME_SLOT);
        localBroadcastManager.sendBroadcast(intent);

    }

    private void loadBarberBySalon(String salonId) {
        dialog.show();
        //Now, Select all Barbor of Salon
        ///AlalSalon/NewYork/Branch/q4Uw4qSsI64PxcP3Szdf/Barbers
        if(!TextUtils.isEmpty(Common.city))
        {
        barberRef = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.city)
                .collection("Branch")
                .document(salonId)
                .collection("Barbers");

        barberRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Barber> barbers  = new ArrayList<>();
                        for(QueryDocumentSnapshot barberSnapshot : task.getResult())
                        {
                            Barber barber = barberSnapshot.toObject(Barber.class);
                            barber.setPassword(""); //Remove Password
                            barber.setBarberId(barberSnapshot.getId());

                            barbers.add(barber);
                        }

                        //Send Broadcast to BookingStep2Fragment to Load Recycler
                        Intent intent = new Intent(Common.KEY_BARBER_LOAD_DONE);
                        intent.putParcelableArrayListExtra(Common.KEY_BARBER_LOAD_DONE, barbers);
                        localBroadcastManager.sendBroadcast(intent);

                        dialog.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                }
        });

        }


    }

    //BroadCast Receiver
    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int step = intent.getIntExtra(Common.KEY_STEP, '0');

            if(step == 1)
                Common.currentSalon = intent.getParcelableExtra(Common.KEY_SALON_STORE);
            else if(step == 2)
                Common.currentBarber = intent.getParcelableExtra(Common.KEY_BARBER_SELECTED);
            else if(step == 3)
                Common.currentTimeSlot = intent.getIntExtra(Common.KEY_TIME_SLOT, -1);


            btn_next_step.setEnabled(true);
            setColorButton();
        }
    };

//    //Code Added - Darshil
//    //BroadCast Receiver
//    private BroadcastReceiver disableNextReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            btn_next_step.setEnabled(false);
//            setColorButton();
//        }
//    };
//    //Code Added - Darshil


    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
//        localBroadcastManager.unregisterReceiver(disableNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);

        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENALBE_BUTTON_NEXT));

//        //Code Added - Darshil
//        //Code Added to Disable (in case Enable when SLOT is FULL)
//        localBroadcastManager.registerReceiver(disableNextReceiver, new IntentFilter(Common.KEY_DISABLE_BUTTON_NEXT));
//        //Code Added - Darshil

        setupStepView();
        setColorButton();

        //View
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(4); //4 Fragment with 4 Page
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {

                //Show Step
                stepView.go(i, true);

                if(i == 0)
                    btn_previous_step.setEnabled(false);
                else
                    btn_previous_step.setEnabled(true);

                //Set Disable button Next Here
                btn_next_step.setEnabled(false);
                setColorButton();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setColorButton() {
        if(btn_next_step.isEnabled())
        {
            btn_next_step.setBackgroundResource(R.color.colorButton);
        }
        else
        {
            btn_next_step.setBackgroundResource(android.R.color.darker_gray);
        }

        if(btn_previous_step.isEnabled())
        {
            btn_previous_step.setBackgroundResource(R.color.colorButton);
        }
        else
        {
            btn_previous_step.setBackgroundResource(android.R.color.darker_gray);
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList();
        stepList.add("Salon");
        stepList.add("Barber");
        stepList.add("Time");
        stepList.add("Confirm");
        stepView.setSteps(stepList);
    }
}
