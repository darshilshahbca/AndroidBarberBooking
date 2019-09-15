package darshil.dev.androidbarberbooking.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Salon implements Parcelable {

    private String name,address,salonId,website, phone, openHours;

    public Salon() {
    }

    protected Salon(Parcel in) {
        name = in.readString();
        address = in.readString();
        salonId = in.readString();
        website = in.readString();
        phone = in.readString();
        openHours = in.readString();
    }

    public static final Creator<Salon> CREATOR = new Creator<Salon>() {
        @Override
        public Salon createFromParcel(Parcel in) {
            return new Salon(in);
        }

        @Override
        public Salon[] newArray(int size) {
            return new Salon[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSalonId() {
        return salonId;
    }

    public void setSalonId(String salonId) {
        this.salonId = salonId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpenHours() {
        return openHours;
    }

    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeString(salonId);
        parcel.writeString(website);
        parcel.writeString(phone);
        parcel.writeString(openHours);
    }
}
