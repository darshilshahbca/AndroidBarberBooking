package darshil.dev.androidbarberbooking.Interface;

import darshil.dev.androidbarberbooking.Model.BookingInformation;

public interface IBookingInfoLoadListener {
    void onBookingInfoLoadEmpty();
    void onBookingInfoLoadSuccess(BookingInformation bookingInformation);
    void onBookingInfoLoadFailed(String message);
}
