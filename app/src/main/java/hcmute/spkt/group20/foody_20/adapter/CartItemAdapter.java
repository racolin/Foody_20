package hcmute.spkt.group20.foody_20.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hcmute.spkt.group20.foody_20.R;
import hcmute.spkt.group20.foody_20.Support;
import hcmute.spkt.group20.foody_20.model.CartItem;
import hcmute.spkt.group20.foody_20.model.OrderItem;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemHolder> {
    Context context;
    List<CartItem> items;

    public CartItemAdapter(Context context, List<CartItem> items) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public CartItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_meal_in_cart, parent, false);
        return new CartItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemHolder holder, int position) {
        holder.iv_meal.setImageBitmap(Support.convertBitmap(items.get(position).getMeal().getImage()));
        holder.tv_name.setText(items.get(position).getMeal().getName(40));
        holder.tv_price.setText(Support.toCurrency(items.get(position).getMeal().getPrice()));
        holder.tv_amount.setText(String.valueOf(items.get(position).getAmount()));
        holder.ib_inc.setOnClickListener(v -> {
            holder.tv_amount.setText(
                    String.valueOf(Integer.valueOf(holder.tv_amount.getText().toString()) + 1)
            );
        });
        holder.ib_dec.setOnClickListener(v -> {
            int r = Integer.valueOf(holder.tv_amount.getText().toString());
            holder.tv_amount.setText(
                    String.valueOf( r == 0 ? 0 : r - 1)
            );
        });
        holder.ib_delete.setOnClickListener(v -> {
            items.remove(position);
//            a.checkChange();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class CartItemHolder extends RecyclerView.ViewHolder {

        ImageView iv_meal;
        TextView tv_name, tv_price, tv_amount;
        ImageButton ib_delete, ib_dec, ib_inc;

        public CartItemHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_price = itemView.findViewById(R.id.tv_price);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            iv_meal = itemView.findViewById(R.id.iv_meal);
            ib_delete = itemView.findViewById(R.id.ib_delete);
            ib_dec = itemView.findViewById(R.id.ib_dec);
            ib_inc = itemView.findViewById(R.id.ib_inc);
        }
    }


}
