package darshil.dev.androidbarberbooking.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource {

    private CartDAO cartDAO;

    public LocalCartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    @Override
    public Single<Long> sumPrice(String userPhone) {
        return cartDAO.sumPrice(userPhone);
    }

    @Override
    public Flowable<List<CartItem>> getAllItemFromCart(String userPhone) {
        return cartDAO.getAllItemFromCart(userPhone);
    }

    @Override
    public Single<Integer> countItemInCart(String userPhone) {
        return cartDAO.countItemInCart(userPhone);
    }

    @Override
    public Flowable<CartItem> getProductInCart(String productId, String userPhone) {
        return cartDAO.getProductInCart(productId,userPhone);
    }

    @Override
    public Completable insert(CartItem... carts) {
        return cartDAO.insert(carts);
    }

    @Override
    public Single<Integer> update(CartItem cart) {
        return cartDAO.update(cart);
    }

    @Override
    public Single<Integer> delete(CartItem cartItem) {
        return cartDAO.delete(cartItem);
    }

    @Override
    public Single<Integer> clearCart(String userPhone) {
        return cartDAO.clearCart(userPhone);
    }
}
