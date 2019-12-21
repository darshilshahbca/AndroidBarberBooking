package darshil.dev.androidbarberbooking;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import darshil.dev.androidbarberbooking.Adapter.MyCartAdapter;
import darshil.dev.androidbarberbooking.Common.Common;
import darshil.dev.androidbarberbooking.Database.CartDataSource;
import darshil.dev.androidbarberbooking.Database.CartDatabase;
import darshil.dev.androidbarberbooking.Database.CartItem;
import darshil.dev.androidbarberbooking.Database.LocalCartDataSource;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CartActivity extends AppCompatActivity {

    MyCartAdapter adapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CartDataSource cartDataSource;

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.btn_clear_cart)
    Button btn_clear_cart;

    @OnClick(R.id.btn_clear_cart)
    void clearCart(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Clear Cart")
                .setMessage("Do you really want to clear Cart ? ")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("CLEAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //DatabaseUtils.clearCart(cartDatabase);
                        cartDataSource.clearCart(Common.currentUser.getPhoneNumber())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {

                                        Toast.makeText(CartActivity.this, "Cart has been Clear!", Toast.LENGTH_SHORT).show();
                                        //After Done, Just Sum
                                        
                                        //We need to Load All Cart, once It gets Cleared

                                        compositeDisposable.add(cartDataSource.getAllItemFromCart(Common.currentUser.getPhoneNumber())
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Consumer<List<CartItem>>() {
                                                    @Override
                                                    public void accept(List<CartItem> cartItems) throws Exception {
                                                        cartDataSource.sumPrice(Common.currentUser.getPhoneNumber())
                                                                .subscribeOn(Schedulers.io())
                                                                .observeOn(AndroidSchedulers.mainThread())
                                                                .subscribe(updatePrice());
                                                    }
                                                }, new Consumer<Throwable>() {
                                                    @Override
                                                    public void accept(Throwable throwable) throws Exception {
                                                        Toast.makeText(CartActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                        );

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(CartActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
//
//        //Update Cart
//        DatabaseUtils.getAllCart(cartDatabase,this);
                        getAllCart();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    CartDatabase cartDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ButterKnife.bind(CartActivity.this);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

//        DatabaseUtils.getAllCart(cartDatabase,this);
        getAllCart();

        //View
        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_cart.setLayoutManager(linearLayoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
    }

    private void getAllCart() {
        compositeDisposable.add(cartDataSource.getAllItemFromCart(Common.currentUser.getPhoneNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CartItem>>() {
                    @Override
                    public void accept(List<CartItem> cartItems) throws Exception {

                        adapter = new MyCartAdapter(CartActivity.this, cartItems);
                        recycler_cart.setAdapter(adapter);

                        //Update Price
                        cartDataSource.sumPrice(Common.currentUser.getPhoneNumber())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(updatePrice());

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(CartActivity.this, ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    private SingleObserver<? super Long> updatePrice() {
        return  new SingleObserver<Long>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(Long aLong) {
                txt_total_price.setText(new StringBuilder("$").append(aLong));
            }

            @Override
            public void onError(Throwable e) {
                if(e.getMessage().contains("Query returned empty"))
                    txt_total_price.setText("");
                else
                    Toast.makeText(CartActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                finish();
            }
        };
    }


    @Override
    protected void onDestroy() {
        if(adapter!=null)
            adapter.onDestroy();
        compositeDisposable.clear();
        super.onDestroy();
    }
}
