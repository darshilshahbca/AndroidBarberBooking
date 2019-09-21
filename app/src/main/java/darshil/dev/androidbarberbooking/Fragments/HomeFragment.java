package darshil.dev.androidbarberbooking.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CalendarContract;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.AccountKit;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.io.LineReader;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import darshil.dev.androidbarberbooking.Adapter.HomeSlideAdapter;
import darshil.dev.androidbarberbooking.Adapter.LookbookAdapter;
import darshil.dev.androidbarberbooking.BookingActivity;
import darshil.dev.androidbarberbooking.Common.Common;
import darshil.dev.androidbarberbooking.Interface.IBannerLoadListener;
import darshil.dev.androidbarberbooking.Interface.IBookingInfoLoadListener;
import darshil.dev.androidbarberbooking.Interface.IBookingInformationChangeListener;
import darshil.dev.androidbarberbooking.Interface.ILookbookLoadListener;
import darshil.dev.androidbarberbooking.Model.Banner;
import darshil.dev.androidbarberbooking.Model.BookingInformation;
import darshil.dev.androidbarberbooking.R;
import darshil.dev.androidbarberbooking.Service.PicassoImageLoadingService;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import ss.com.bannerslider.Slider;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements ILookbookLoadListener, IBannerLoadListener, IBookingInfoLoadListener, IBookingInformationChangeListener {

    private Unbinder unbinder;

    AlertDialog dialog;

    @BindView(R.id.layout_user_information)
    LinearLayout layout_user_information;

    @BindView(R.id.txt_user_name)
    TextView txt_user_name;

    @BindView(R.id.banner_slider)
    Slider banner_slide;

    @BindView(R.id.recycler_look_book)
    RecyclerView recycler_look_book;

    @BindView(R.id.card_booking_info)
    CardView card_booking_info;
    @BindView(R.id.txt_salon_address)
    TextView txt_salon_address;
    @BindView(R.id.txt_salon_barber)
    TextView txt_salon_barber;
    @BindView(R.id.txt_time)
    TextView txt_time;
    @BindView(R.id.txt_time_remain)
    TextView txt_time_remain;

    @OnClick(R.id.btn_change_booking)
    void changeBooking()
    {
        changeBookingFromUser();
    }

    private void changeBookingFromUser() {
        //Show Dialog
        androidx.appcompat.app.AlertDialog.Builder confirmDialog = new androidx.appcompat.app.AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("Hey!")
                .setMessage("Do you really want to change booking information?\nBecause We will delete your old booking information\nJust Confirm")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            deleteBookingFromBarber(true);
                    }
                });

        confirmDialog.show();
    }


    @OnClick(R.id.btn_delete_booking)
    void deleteBooking()
    {
        deleteBookingFromBarber(false);
    }

    private void deleteBookingFromBarber(boolean isChange) {

        dialog.show();

        //TO Delete Booking, First We need to delete from Barber Collection
        //After that, Delete from User Booking collections
        //And Final, Delete Event

        //We need Load Common.currentBooking because We need some data from BookingInformation
        if(Common.currentBooking != null)
        {
            //Get Booking Information
            DocumentReference barberBookingInfo = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.currentBooking.getCityBook())
                    .collection("Branch")
                    .document(Common.currentBooking.getSalonId())
                    .collection("Barbers")
                    .document(Common.currentBooking.getBarberId())
                    .collection(Common.convertTimeSlotToStringKey(Common.currentBooking.getTimestamp()))
                    .document(String.valueOf(Common.currentBooking.getSlot()));

            barberBookingInfo.delete().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //After Delete from Barber done //We will start delete from User
                    deleteBookingFromUser(isChange);
                }
            });


        }
        else
        {
            Toast.makeText(getContext(), "Current Booking must no be null", Toast.LENGTH_SHORT).show();
        }


    }

    private void deleteBookingFromUser(boolean isChange) {
        //First, We need to get Information from user Object
        if(!TextUtils.isEmpty(Common.currentBookingId))
        {
            DocumentReference userBookingInfo = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking")
                    .document(Common.currentBookingId);

            //Delete
            userBookingInfo.delete()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //After, Delete from User, Delete it from Calendar
                    Paper.init(getActivity());
                    Uri eventUri = Uri.parse(Paper.book().read(Common.EVENT_URI_CACHE).toString());
                    getActivity().getContentResolver().delete(eventUri, null, null);

                    Toast.makeText(getActivity(), "Success delete booking !", Toast.LENGTH_SHORT).show();

                    //Refresh
                    loadUserBooking();

                    //Check if Change -> Call from change Button, we will finred interface
                    if(isChange)
                        iBookingInformationChangeListener.onBookingInformationChange();

                    if(dialog.isShowing())
                        dialog.dismiss();
                }
            });
        }
        else
        {
            if(dialog.isShowing())
                dialog.dismiss();
            Toast.makeText(getContext(), "Booking Information ID must not be empty", Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.card_view_booking)
    void booking()
    {
        startActivity(new Intent(getActivity(), BookingActivity.class));

    }


    //FireStore
    CollectionReference bannerRef, lookbookref;

    //Interface
    IBannerLoadListener iBannerLoadListener;
    ILookbookLoadListener iLookbookLoadListener;
    IBookingInfoLoadListener iBookingInfoLoadListener;
    IBookingInformationChangeListener iBookingInformationChangeListener;

    public HomeFragment() {

        bannerRef = FirebaseFirestore.getInstance().collection("Banner");
        lookbookref = FirebaseFirestore.getInstance().collection("Lookbook");


    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
    }

    private void loadUserBooking() {
        CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        //Get Current Date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);

        Timestamp toDayTimeStamp = new Timestamp(calendar.getTime());

        //Select Booking Information from Firebase with done="false" and timestamp greater today
        userBooking
                .whereGreaterThanOrEqualTo("timestamp", toDayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1) //Only take 1
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if(!task.getResult().isEmpty())
                            {
                                for(QueryDocumentSnapshot queryDocumentSnapshot: task.getResult())
                                {
                                    BookingInformation bookingInformation = queryDocumentSnapshot.toObject(BookingInformation.class);
                                    iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation, queryDocumentSnapshot.getId());
                                    break; //Exit Loop
                                }
                            }
                            else
                            {
                                iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    iBookingInfoLoadListener.onBookingInfoLoadFailed(e.getMessage());
            }
        });



    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);


        //Init
        //Init
        Slider.init(new PicassoImageLoadingService());
        iBannerLoadListener = this;
        iLookbookLoadListener = this;
        iBookingInfoLoadListener = this;
        iBookingInformationChangeListener = this;


        //Check is Logged
        if (AccountKit.getCurrentAccessToken() != null)
        {
            setUserInformation();
            loadBanner();
            loadLookBook();
            loadUserBooking();
        }
        /*//Added by Darshil
        else{
            loadBanner();
            loadLookBook();
        }
        //Added by Darshil*/

        return view;
    }

    private void loadLookBook() {
        lookbookref.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> lookbooks = new ArrayList<>();
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot lookbookSnapShot: task.getResult())
                            {
                                Banner lookbook = lookbookSnapShot.toObject(Banner.class);
                                lookbooks.add(lookbook);
                            }
                            iLookbookLoadListener.onLookbookLoadSuccess(lookbooks);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iLookbookLoadListener.onLookbookLoadFailed(e.getMessage());
            }
        });
    }

    private void loadBanner() {
        bannerRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Banner> banners = new ArrayList<>();
                        if(task.isSuccessful())
                        {
                            for(QueryDocumentSnapshot bannerSnapShot : task.getResult())
                            {
                                Banner banner = bannerSnapShot.toObject(Banner.class);
                                banners.add(banner);
                            }
                            iBannerLoadListener.onBannerLoadSuccess(banners);
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iBannerLoadListener.onBannerLoadFailed(e.getMessage());
            }
        });
    }

    private void setUserInformation() {
        layout_user_information.setVisibility(View.VISIBLE);
        txt_user_name.setText(Common.currentUser.getName());
    }

    @Override
    public void onLookbookLoadSuccess(List<Banner> banners) {
        recycler_look_book.setHasFixedSize(true);
        recycler_look_book.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_look_book.setAdapter(new LookbookAdapter(getActivity(),banners));
    }

    @Override
    public void onLookbookLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBannerLoadSuccess(List<Banner> banners) {
        banner_slide.setAdapter(new HomeSlideAdapter(banners));

    }

    @Override
    public void onBannerLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInfoLoadEmpty() {
        card_booking_info.setVisibility(View.GONE);
//        if(dialog.isShowing())
//            dialog.dismiss();
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation, String bookingId) {

        Common.currentBooking = bookingInformation;
        Common.currentBookingId = bookingId;

        txt_salon_address.setText(bookingInformation.getSalonAddress());
        txt_salon_barber.setText(bookingInformation.getBarberName());
        txt_time.setText(bookingInformation.getTime());
        String dateRemain = DateUtils.getRelativeTimeSpanString(
          Long.valueOf(bookingInformation.getTimestamp().toDate().getTime()),
                Calendar.getInstance().getTimeInMillis(), 0).toString();

        txt_time_remain.setText(dateRemain);

        card_booking_info.setVisibility(View.VISIBLE);

        if(dialog.isShowing())
            dialog.dismiss();
    }

    @Override
    public void onBookingInfoLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInformationChange() {
        //Here, We will start Activity
        startActivity(new Intent(getActivity(),BookingActivity.class));
    }
}
