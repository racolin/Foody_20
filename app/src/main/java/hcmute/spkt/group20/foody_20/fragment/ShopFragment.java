package hcmute.spkt.group20.foody_20.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import hcmute.spkt.group20.foody_20.R;
import hcmute.spkt.group20.foody_20.adapter.ShopAdapter;
import hcmute.spkt.group20.foody_20.model.Meal;
import hcmute.spkt.group20.foody_20.model.Shop;
import hcmute.spkt.group20.foody_20.model.ShopChain;

public class ShopFragment extends Fragment {
    List<Shop> shops;
    ShopAdapter adapter;
    RecyclerView rv_2_items;
    FirebaseFirestore db;
    StorageReference reference;

    public ShopFragment() {
        shops = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
    }

    public ShopFragment(List<Shop> shops) {
        this.shops = shops;
        db = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shops, container, false);
        adapter = new ShopAdapter(getContext(), shops);
        rv_2_items = view.findViewById(R.id.rv_2_items);
        rv_2_items.setAdapter(adapter);
        rv_2_items.setLayoutManager(new GridLayoutManager(getContext(), 2));
        getSavedShops();
        return view;
    }
    public void getSavedShops() {
        String uid = FirebaseAuth.getInstance().getUid();
        db.collection("shops").whereArrayContains("user_save", uid).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
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
                                        db.collection("shop_chain").document(s.getShop_chain_id())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            s.setShop_chain(task.getResult().toObject(ShopChain.class));
                                                            Log.d("rrrr", s.getShop_chain().getShop_chain_id());
                                                            shops.add(s);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                });
                                    } catch (IOException e) {
                                        Log.d("rrrxxr", snapshot.get("image_src").toString());
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
//                                                    meals.add(m);
//                                                    adapter.notifyDataSetChanged();
//                                                }
//                                            }
//                                        });
                            }
                        }
                    }
                });
    }
}
