package darshil.dev.androidbarberbooking.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import darshil.dev.androidbarberbooking.R;

public class BookingStep4Fragment extends Fragment {

    static BookingStep4Fragment instance;

    public static BookingStep4Fragment getInstance() {
        if(instance == null)
            instance = new BookingStep4Fragment();
        return instance;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_booking_step_four,container, false);

    }
}
