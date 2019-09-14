package darshil.dev.androidbarberbooking.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import darshil.dev.androidbarberbooking.Fragments.BookingStep1Fragment;
import darshil.dev.androidbarberbooking.Fragments.BookingStep2Fragment;
import darshil.dev.androidbarberbooking.Fragments.BookingStep3Fragment;
import darshil.dev.androidbarberbooking.Fragments.BookingStep4Fragment;

public class MyViewPagerAdapter extends FragmentPagerAdapter {
    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0:
                return BookingStep1Fragment.getInstance() ;
            case 1:
                return BookingStep2Fragment.getInstance();
            case 2:
                return BookingStep3Fragment.getInstance();
            case 3:
                return BookingStep4Fragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
