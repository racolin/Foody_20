package hcmute.spkt.group20.foody_20.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hcmute.spkt.group20.foody_20.HomeActivity;
import hcmute.spkt.group20.foody_20.MealActivity;
import hcmute.spkt.group20.foody_20.R;
import hcmute.spkt.group20.foody_20.ShopActivity;
import hcmute.spkt.group20.foody_20.ShopChainActivity;
import hcmute.spkt.group20.foody_20.Support;
import hcmute.spkt.group20.foody_20.model.Shop;
import hcmute.spkt.group20.foody_20.model.ShopChain;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopHolder> {
    List<Shop> shops;
    Context context;

    public ShopAdapter(Context context, List<Shop> shops) {
        this.context = context;
        this.shops = shops;
    }

    @NonNull
    @Override
    public ShopHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shop_item_2, parent, false);
        return new ShopHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopHolder holder, int position) {
        holder.iv_shop.setImageBitmap(Support.convertBitmap(shops.get(position).getImage()));
        holder.tv_name.setText(shops.get(position).getName());
        holder.tv_description.setText(shops.get(position).getDescription(30));
        holder.tv_distance.setText(shops.get(position).getDistance());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShopActivity.class);
            shops.get(position).getShop_chain().setImage(null);
            intent.putExtra("shop", shops.get(position));
            (context).startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return shops == null ? 0 : shops.size();
    }


    protected class ShopHolder extends RecyclerView.ViewHolder {
        ImageView iv_shop;
        TextView tv_name, tv_description, tv_distance;

        public ShopHolder(@NonNull View itemView) {
            super(itemView);
            iv_shop = itemView.findViewById(R.id.iv_shop);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_description = itemView.findViewById(R.id.tv_description);
            tv_distance = itemView.findViewById(R.id.tv_distance);

            DisplayMetrics metrics = new DisplayMetrics();
            ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int width = metrics.widthPixels;
            itemView.setLayoutParams(new RecyclerView.LayoutParams(width / 2, RecyclerView.LayoutParams.WRAP_CONTENT));
        }
    }
}
