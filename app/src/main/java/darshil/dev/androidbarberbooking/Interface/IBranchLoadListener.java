package darshil.dev.androidbarberbooking.Interface;

import java.util.List;

import darshil.dev.androidbarberbooking.Model.Salon;

public interface IBranchLoadListener {

    void onBranchLoadSuccess(List<Salon> salonList);
    void onBranchLoadFailed(String message);
}
