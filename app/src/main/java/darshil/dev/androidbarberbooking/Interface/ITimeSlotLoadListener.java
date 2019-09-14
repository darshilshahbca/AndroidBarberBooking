package darshil.dev.androidbarberbooking.Interface;

import java.util.List;

import darshil.dev.androidbarberbooking.Model.Banner;
import darshil.dev.androidbarberbooking.Model.TimeSlot;

public interface ITimeSlotLoadListener {

    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList);
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty();
}
