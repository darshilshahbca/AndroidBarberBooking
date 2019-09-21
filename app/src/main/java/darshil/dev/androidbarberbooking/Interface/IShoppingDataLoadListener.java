package darshil.dev.androidbarberbooking.Interface;

import java.util.List;

import darshil.dev.androidbarberbooking.Model.ShoppingItem;

public interface IShoppingDataLoadListener {
    void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList);
    void onShoppingDataLoadFailed(String message);


}
