package darshil.dev.androidbarberbooking.Interface;

import java.util.List;

import darshil.dev.androidbarberbooking.Model.Banner;

public interface IBannerLoadListener {

    void onBannerLoadSuccess(List<Banner> banners);
    void onBannerLoadFailed(String message);
}
