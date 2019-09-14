package darshil.dev.androidbarberbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.shuhart.stepview.StepView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import darshil.dev.androidbarberbooking.Adapter.MyViewPagerAdapter;
import darshil.dev.androidbarberbooking.Common.Common;
import darshil.dev.androidbarberbooking.Common.NonSwipeViewPager;

public class BookingActivity extends AppCompatActivity {

    LocalBroadcastManager localBroadcastManager;

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
            viewPager.setCurrentItem(Common.step);
        }


//        Toast.makeText(this, ""+Common.currentSalon.getSalonId(), Toast.LENGTH_SHORT).show();
    }

    private void loadBarberBySalon(String salonId) {

    }

    //BroadCast Receiver
    private BroadcastReceiver buttonNextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Common.currentSalon = intent.getParcelableExtra(Common.KEY_SALON_STORE);
            btn_next_step.setEnabled(true);
            setColorButton();
        }
    };

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(buttonNextReceiver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        ButterKnife.bind(BookingActivity.this);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(buttonNextReceiver, new IntentFilter(Common.KEY_ENALBE_BUTTON_NEXT));

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
                if(i == 0)
                    btn_previous_step.setEnabled(false);
                else
                    btn_previous_step.setEnabled(true);

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
