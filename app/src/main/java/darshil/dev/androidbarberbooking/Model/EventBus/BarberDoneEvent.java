package darshil.dev.androidbarberbooking.Model.EventBus;

import java.util.ArrayList;
import java.util.List;

import darshil.dev.androidbarberbooking.Model.Barber;

public class BarberDoneEvent {
    private List<Barber> barberList;

    public BarberDoneEvent(List<Barber> barberList) {
        this.barberList = barberList;
    }

    public List<Barber> getBarberList() {
        return barberList;
    }

    public void setBarberList(List<Barber> barberList) {
        this.barberList = barberList;
    }
}
