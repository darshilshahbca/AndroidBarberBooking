package darshil.dev.androidbarberbooking.Interface;

import java.util.List;

import darshil.dev.androidbarberbooking.Database.CartItem;

public interface ICartItemLoadListener {
    void onGetAllItemFromCartSuccess(List<CartItem> cartItemList);
}
