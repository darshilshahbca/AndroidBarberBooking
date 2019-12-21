package darshil.dev.androidbarberbooking.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDAO {

    @Query("SELECT SUM(productPrice*productQuantity) from Cart where userPhone=:userPhone ")
    Single<Long> sumPrice(String userPhone);

    @Query("SELECT * FROM Cart WHERE userPhone=:userPhone")
    Flowable<List<CartItem>> getAllItemFromCart(String userPhone);

    @Query("SELECT COUNT(*) FROM Cart WHERE userPhone=:userPhone")
    Single<Integer> countItemInCart(String userPhone);

    @Query("SELECT * FROM Cart WHERE productId=:productId and userPhone=:userPhone")
    Flowable<CartItem> getProductInCart(String productId, String userPhone);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    Completable insert(CartItem...carts);

    @Update(onConflict = OnConflictStrategy.FAIL)
    Single<Integer> update(CartItem cart);

    @Delete
    Single<Integer> delete(CartItem cartItem);

    @Query("DELETE FROM Cart WHERE userPhone=:userPhone")
    Single<Integer> clearCart(String userPhone);


}
