package hcmute.spkt.group20.foody_20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hcmute.spkt.group20.foody_20.adapter.ShopAdapter;
import hcmute.spkt.group20.foody_20.model.Meal;
import hcmute.spkt.group20.foody_20.model.Shop;
import hcmute.spkt.group20.foody_20.model.ShopChain;

public class ShopChainActivity extends AppCompatActivity {

    RecyclerView rv_shops;
    TextView tv_shops_amount, tv_description, tv_name;
    ShopChain shop_chain;
    ImageView iv_shop_chain;
    ShopAdapter shopAdapter;
    List<Shop> shops;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_chain);
        shop_chain = (ShopChain) getIntent().getSerializableExtra("shop_chain");
        load();
        initUI();
    }

    public void initUI() {
        iv_shop_chain = findViewById(R.id.iv_shop_chain);

        rv_shops = findViewById(R.id.rv_shops);
        rv_shops.setLayoutManager(new GridLayoutManager(this, 2));
        shops = new ArrayList<>();
        shop_chain.setShops(shops);
        shopAdapter = new ShopAdapter(this, shops);
        rv_shops.setAdapter(shopAdapter);

        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(shop_chain.getName());

        tv_shops_amount = findViewById(R.id.tv_shops_amount);
        tv_shops_amount.setText(String.valueOf(shop_chain.getShop_count()));

        tv_description = findViewById(R.id.tv_description);
        tv_description.setText(shop_chain.getDescription());
    }

    public void load() {
        StorageReference reference = FirebaseStorage.getInstance().getReference();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reference.child(shop_chain.getImage_src())
                .getBytes(1024 * 1024)
                .addOnCompleteListener(new OnCompleteListener<byte[]>() {
                    @Override
                    public void onComplete(@NonNull Task<byte[]> task) {
                        if (task.isSuccessful()) {
                            shop_chain.setImage(task.getResult());
                            new Thread(() -> {
                                try {
                                    Bitmap bm = Picasso.get()
                                            .load("http://10.0.140.232/foody/" + shop_chain.getImage_src())
                                            .get();
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bm.compress(Bitmap.CompressFormat.JPEG,80,stream);
                                    byte[] byteArray = stream.toByteArray();
                                    shop_chain.setImage(byteArray);
                                    runOnUiThread(() -> {
                                        iv_shop_chain.setImageBitmap(Support.convertBitmap(shop_chain.getImage()));
                                    });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }).start();
                        }
                    }
                });
        db.collection("shops").whereEqualTo("shop_chain_id", shop_chain.getShop_chain_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                new Thread(() -> {
                                    try {
                                        Log.d("rrrrr", snapshot.get("image_src").toString());
                                        Bitmap bm = Picasso.get()
                                                .load("http://10.0.140.232/foody/" + snapshot.get("image_src").toString())
                                                .get();
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bm.compress(Bitmap.CompressFormat.JPEG,80,stream);
                                        byte[] byteArray = stream.toByteArray();
                                        Shop s = snapshot.toObject(Shop.class);
                                        s.setImage(byteArray);
                                        runOnUiThread(() -> {
                                            iv_shop_chain.setImageBitmap(Support.convertBitmap(s.getImage()));
                                        });
                                        db.collection("shops").document(s.getShop_id())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            s.setShop_chain(task.getResult().toObject(ShopChain.class));
                                                            shops.add(s);
                                                            runOnUiThread(() -> {
                                                                tv_shops_amount.setText(String.valueOf(shop_chain.getShop_count()));
                                                            });
                                                            shopAdapter.notifyItemInserted(shops.size() - 1);
                                                        }
                                                    }
                                                });
                                    } catch (IOException e) {
                                        Log.d("rrrxxr", snapshot.get("image_src").toString());
                                        e.printStackTrace();
                                    }

                                }).start();
//                                reference.child(s.getImage_src())
//                                        .getBytes(1024 * 1024)
//                                        .addOnCompleteListener(new OnCompleteListener<byte[]>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<byte[]> task) {
//                                                if (task.isSuccessful()) {
//                                                    s.setImage(task.getResult());
//                                                    shops.add(s);
//                                                    runOnUiThread(() -> {
//                                                        iv_shop_chain.setImageBitmap(Support.convertBitmap(task.getResult()));
//                                                        tv_shops_amount.setText(String.valueOf(shop_chain.getShop_count()));
//                                                    });
//                                                    shopAdapter.notifyItemInserted(shops.size() - 1);
//                                                }
//                                            }
//                                        });
                            }
                        }
                    }
                });
    }
}