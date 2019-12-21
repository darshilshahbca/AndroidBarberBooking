package darshil.dev.androidbarberbooking.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import darshil.dev.androidbarberbooking.Database.CartDataSource;
import darshil.dev.androidbarberbooking.Database.CartDatabase;
import darshil.dev.androidbarberbooking.Database.CartItem;
import darshil.dev.androidbarberbooking.Database.LocalCartDataSource;
import darshil.dev.androidbarberbooking.R;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder>{

    Context context;
    List<CartItem> cartItemList;
    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable;
//    ICartItemUpdateListener iCartItemUpdateListener;

    public void onDestroy()
    {
        compositeDisposable.clear();
    }

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
        this.compositeDisposable = new CompositeDisposable();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(
                R.layout.layout_cart_item, viewGroup, false
        );
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Picasso.get().load(cartItemList.get(i).getProductImage()).into(myViewHolder.img_product);
        myViewHolder.txt_cart_name.setText(new StringBuilder(cartItemList.get(i).getProductName()));
        myViewHolder.txt_cart_price.setText(new StringBuilder("$").append(cartItemList.get(i).getProductPrice()));
        myViewHolder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(i).getProductQuantity())));

        //Event
        myViewHolder.setListener(new IImageButtonListener() {
            @Override
            public void onImageButtonClick(View view, int pos, boolean isDecrease) {
                if(isDecrease)
                {
                    if(cartItemList.get(pos).getProductQuantity() > 1)
                    {
                        cartItemList.get(pos)
                                .setProductQuantity(cartItemList.get(pos).getProductQuantity() - 1);

                        cartDataSource.update(cartItemList.get(pos))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {
                                        //When Update Success, Set Quantity
                                        myViewHolder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(i).getProductQuantity())));
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
//                        DatabaseUtils.updateCart(cartDatabase, cartItemList.get(pos));

                    }
                    else if(cartItemList.get(pos).getProductQuantity() == 1){ //If User Click, Decrease when Item =1, Delete

                        cartDataSource.delete(cartItemList.get(pos))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(Integer integer) {
                                        cartItemList.remove(pos);
                                        notifyItemRemoved(pos);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

//                        DatabaseUtils.deleteCart(cartDatabase, cartItemList.get(pos));

                    }

                }
                else{

                    if(cartItemList.get(pos).getProductQuantity() < 99)
                        cartItemList.get(pos)
                                .setProductQuantity(cartItemList.get(pos).getProductQuantity() + 1);

                    cartDataSource.update(cartItemList.get(pos))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Integer>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onSuccess(Integer integer) {
                                    myViewHolder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(i).getProductQuantity())));
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


//                    DatabaseUtils.updateCart(cartDatabase, cartItemList.get(pos));


                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    interface IImageButtonListener{
        void onImageButtonClick(View view, int pos, boolean isDecrease);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txt_cart_name, txt_cart_price, txt_quantity;
        ImageView img_decrease, img_increase, img_product;

        IImageButtonListener listener;

        public void setListener(IImageButtonListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_cart_name = (TextView)itemView.findViewById(R.id.txt_cart_name);
            txt_cart_price = (TextView)itemView.findViewById(R.id.txt_cart_price);
            txt_quantity = (TextView)itemView.findViewById(R.id.txt_cart_quantity);

            img_decrease = (ImageView) itemView.findViewById(R.id.img_decrease);
            img_increase = (ImageView) itemView.findViewById(R.id.img_increase);
            img_product = (ImageView) itemView.findViewById(R.id.cart_img);

            img_decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onImageButtonClick(view, getAdapterPosition(), true);

                }
            });

            img_increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onImageButtonClick(view, getAdapterPosition(), false);
                }
            });

        }
    }
}
