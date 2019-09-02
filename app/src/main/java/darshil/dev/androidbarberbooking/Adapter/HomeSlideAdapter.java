package darshil.dev.androidbarberbooking.Adapter;

import java.util.List;

import darshil.dev.androidbarberbooking.Model.Banner;
import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class HomeSlideAdapter extends SliderAdapter {

    List<Banner> bannerList;

    public HomeSlideAdapter(List<Banner> bannerList) {
        this.bannerList = bannerList;
    }


    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder imageSlideViewHolder) {
        imageSlideViewHolder.bindImageSlide(bannerList.get(position).getImage());
    }
}
