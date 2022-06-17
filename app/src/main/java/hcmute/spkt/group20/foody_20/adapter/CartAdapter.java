package hcmute.spkt.group20.foody_20.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import hcmute.spkt.group20.foody_20.OrderActivity;
import hcmute.spkt.group20.foody_20.R;
import hcmute.spkt.group20.foody_20.Support;
import hcmute.spkt.group20.foody_20.model.Cart;

//OK

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {
    List<Cart> carts;
    Context context;

    public CartAdapter(Context context, List<Cart> carts) {
        this.carts = carts;
        this.context = context;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_item_cart, parent, false);
        return new CartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        holder.tv_distance.setText(carts.get(position).getShop().getDistance());
        holder.tv_total_price.setText(Support.toCurrency(carts.get(position).getTotal_price()));
        holder.tv_shop_name.setText(carts.get(position).getShop().getName(25));
        holder.rv_cart_item.setAdapter(new CartItemAdapter(context, carts.get(position).getCart_items()));
        holder.rv_cart_item.setLayoutManager(new LinearLayoutManager(context));
        holder.btn_buy_now.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderActivity.class);
            intent.putExtra("cart", carts.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    protected class CartHolder extends RecyclerView.ViewHolder {
        TextView tv_shop_name, tv_distance, tv_total_price;
        RecyclerView rv_cart_item;
        Button btn_buy_now;
        public CartHolder(@NonNull View itemView) {
            super(itemView);
            tv_total_price = itemView.findViewById(R.id.tv_total_price);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_shop_name = itemView.findViewById(R.id.tv_shop_name);
            rv_cart_item = itemView.findViewById(R.id.rv_cart_item);
            btn_buy_now = itemView.findViewById(R.id.btn_buy_now);
        }
    }
}