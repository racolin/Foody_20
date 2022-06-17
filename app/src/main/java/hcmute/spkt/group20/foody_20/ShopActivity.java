package hcmute.spkt.group20.foody_20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hcmute.spkt.group20.foody_20.adapter.MealAdapter;
import hcmute.spkt.group20.foody_20.adapter.ShopAdapter;
import hcmute.spkt.group20.foody_20.model.Meal;
import hcmute.spkt.group20.foody_20.model.Shop;
import hcmute.spkt.group20.foody_20.model.ShopChain;

public class ShopActivity extends AppCompatActivity {

    RecyclerView rv_shops, rv_meals;
    TextView tv_shop_chain, tv_name, tv_distance, tv_description, tv_time_open, tv_save, tv_share;
    Button btn_contact;
    ImageView iv_shop;
    Shop shop;
    MealAdapter mealAdapter;
    ShopAdapter shopAdapter;
    List<Meal> meals;
    List<Shop> shops;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        shop = (Shop) getIntent().getSerializableExtra("shop");

        loadShopChain();

        initUI();

        initListener();
    }

    private void initUI() {

        tv_shop_chain = findViewById(R.id.tv_shop_chain);
        iv_shop = findViewById(R.id.iv_shop);

        tv_save = findViewById(R.id.tv_save);
        tv_share = findViewById(R.id.tv_share);

        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(shop.getName());

        tv_distance = findViewById(R.id.tv_distance);
        tv_distance.setText(shop.getDistance());
        tv_description = findViewById(R.id.tv_description);
        tv_description.setText(shop.getDescription());
        tv_time_open = findViewById(R.id.tv_time_open);
        tv_time_open.setText(shop.getTime_open());

        meals = new ArrayList<>();
        shops = new ArrayList<>();

        btn_contact = findViewById(R.id.btn_contact);

        rv_shops = findViewById(R.id.rv_shops);
        shopAdapter = new ShopAdapter(this, shops);
        rv_shops.setAdapter(shopAdapter);
        rv_shops.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rv_meals = findViewById(R.id.rv_meals);
        mealAdapter = new MealAdapter(this, meals);
        rv_meals.setAdapter(mealAdapter);
        rv_meals.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void initListener() {

        btn_contact.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactDialog.class);
            intent.putExtra("shop", shop);
            startActivity(intent);
        });

        tv_share.setOnClickListener(v -> {
            Toast.makeText(this, "Xử lý sự kiện share", Toast.LENGTH_SHORT).show();
        });

        tv_save.setOnClickListener(v -> {
            Toast.makeText(this, "Xử lý sự kiện lưu", Toast.LENGTH_SHORT).show();
        });
    }

    public void loadShopChain() {
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("shop_chain").document(shop.getShop_chain_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            shop.setShop_chain(task.getResult().toObject(ShopChain.class));
                            runOnUiThread(() -> {
                                tv_shop_chain.setText(shop.getShop_chain().getName(30));
                                tv_shop_chain.setOnClickListener(v -> {
                                    Intent intent = new Intent(ShopActivity.this, ShopChainActivity.class);
                                    intent.putExtra("shop_chain", shop.getShop_chain());
                                    startActivity(intent);
                                });
                            });
                        }
                    }
                });
//        reference.child(shop.getImage_src()).getBytes(1024 * 1024)
//                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
//                    @Override
//                    public void onComplete(@NonNull Task<byte[]> task) {
//                        if (task.isSuccessful()) {
//                            shop.setImage(task.getResult());
//                            runOnUiThread(() -> {
//                                iv_shop.setImageBitmap(Support.convertBitmap(task.getResult()));
//                            });
//                        } else {
//                        }
//                    }
//                });

        new Thread(() -> {
            try {
                Bitmap bm = Picasso.get()
                        .load("http://10.0.140.232/foody/" + shop.getImage_src())
                        .get();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG,80,stream);
                byte[] byteArray = stream.toByteArray();
                shop.setImage(byteArray);
                runOnUiThread(() -> {
                    iv_shop.setImageBitmap(Support.convertBitmap(shop.getImage()));
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        db.collection("meals").whereEqualTo("shop_id", shop.getShop_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                ShopChain sc = snapshot.toObject(ShopChain.class);
                                new Thread(() -> {
                                    try {
                                        Log.d("rrrrr", snapshot.get("image_src").toString());
                                        Bitmap bm = Picasso.get()
                                                .load("http://10.0.140.232/foody/" + snapshot.get("image_src").toString())
                                                .get();
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bm.compress(Bitmap.CompressFormat.JPEG,80,stream);
                                        byte[] byteArray = stream.toByteArray();
                                        Meal m = snapshot.toObject(Meal.class);
                                        m.setImage(byteArray);
                                        m.setShop(shop);
                                        meals.add(m);
                                        runOnUiThread(() -> {
                                            mealAdapter.notifyDataSetChanged();
                                        });

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }).start();
//                                reference.child(snapshot.get("image_src").toString())
//                                        .getBytes(1024 * 1024)
//                                        .addOnCompleteListener(new OnCompleteListener<byte[]>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<byte[]> task) {
//                                                if (task.isSuccessful()) {
//                                                    Meal m = snapshot.toObject(Meal.class);
//                                                    m.setImage(task.getResult());
//                                                    m.setShop(shop);
//                                                    meals.add(m);
//                                                    mealAdapter.notifyItemInserted(meals.size() - 1);
//                                                }
//                                            }
//                                        });
                            }
                        }
                    }
                });
    }
}