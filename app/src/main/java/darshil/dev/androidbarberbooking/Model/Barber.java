package darshil.dev.androidbarberbooking.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Barber implements Parcelable {
    private String name, username, password, barberId;
    private double rating;
    private long ratingTimes;

    public Barber() {
    }

    protected Barber(Parcel in) {
        name = in.readString();
        username = in.readString();
        password = in.readString();
        barberId = in.readString();
        rating = in.readDouble();
        ratingTimes = in.readLong();

//        if(in.readByte() == 0)
//        {
//            rating = null;
//        } else{
//            rating = in.readDouble();
//            ratingTimes = in.readLong();
//        }

    }

    public static final Creator<Barber> CREATOR = new Creator<Barber>() {
        @Override
        public Barber createFromParcel(Parcel in) {
            return new Barber(in);
        }

        @Override
        public Barber[] newArray(int size) {
            return new Barber[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public long getRatingTimes() {
        return ratingTimes;
    }

    public void setRatingTimes(long ratingTimes) {
        this.ratingTimes = ratingTimes;
    }

    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(barberId);
        parcel.writeDouble(rating);
        parcel.writeLong(ratingTimes);
    }
}
