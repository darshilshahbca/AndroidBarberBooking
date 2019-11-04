package darshil.dev.androidbarberbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import darshil.dev.androidbarberbooking.Adapter.MyCartAdapter;
import darshil.dev.androidbarberbooking.Database.CartDatabase;
import darshil.dev.androidbarberbooking.Database.CartItem;
import darshil.dev.androidbarberbooking.Database.DatabaseUtils;
import darshil.dev.androidbarberbooking.Interface.ICartItemLoadListener;
import darshil.dev.androidbarberbooking.Interface.ICartItemUpdateListener;
import darshil.dev.androidbarberbooking.Interface.ISumCartListener;

public class CartActivity extends AppCompatActivity implements ICartItemLoadListener, ICartItemUpdateListener, ISumCartListener {

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    @BindView(R.id.txt_total_price)
    TextView txt_total_price;
    @BindView(R.id.btn_submit_cart)
    Button btn_submit_cart;

    CartDatabase cartDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ButterKnife.bind(CartActivity.this);

        cartDatabase = CartDatabase.getInstance(this);

        DatabaseUtils.getAllCart(cartDatabase,this);

        //View
        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_cart.setLayoutManager(linearLayoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));
    }

    @Override
    public void onGetAllItemFromCartSuccess(List<CartItem> cartItemList) {
        //Here, After We get all Cart item from DB
        //We will display by Recycler View
        MyCartAdapter  adapter = new MyCartAdapter(this, cartItemList, this);
        recycler_cart.setAdapter(adapter);

    }

    @Override
    public void onCartItemUpdateSuccess() {
        DatabaseUtils.sumCart(cartDatabase, this);
    }

    @Override
    public void onSumCartSuccess(Long value) {
        txt_total_price.setText(new StringBuilder("$").append(value));
    }
}
