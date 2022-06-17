package hcmute.spkt.group20.foody_20.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import hcmute.spkt.group20.foody_20.Support;
import hcmute.spkt.group20.foody_20.adapter.CartAdapter;
import hcmute.spkt.group20.foody_20.model.Cart;
import hcmute.spkt.group20.foody_20.model.CartItem;
import hcmute.spkt.group20.foody_20.model.Meal;
import hcmute.spkt.group20.foody_20.model.Shop;

public class CartFragment extends Fragment {
    RecyclerView rv_cart;
    CartAdapter adapter;
    List<Cart> carts;
    FirebaseFirestore db;
    StorageReference reference;
    Handler handler;

    public CartFragment() {
        db = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();

        handler = new Handler();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = null;
        if (FirebaseAuth.getInstance().getCurrentUser().isAnonymous()) {
            v = inflater.inflate(R.layout.none_login, container, false);
        } else {
            v = inflater.inflate(R.layout.fragment_cart, container, false);
            rv_cart = v.findViewById(R.id.rv_cart);

            rv_cart.setLayoutManager(new LinearLayoutManager(getContext()));

            carts = new ArrayList<>();

            adapter = new CartAdapter(getContext(), carts);
            getAllCarts();
            rv_cart.setAdapter(adapter);
        }

        return v;
    }

    public void getAllCarts() {
        String uid = FirebaseAuth.getInstance().getUid();
        db.collection("carts").whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Cart cart = snapshot.toObject(Cart.class);

                                db.collection("shops").document(cart.getShop_id())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    cart.setShop(task.getResult().toObject(Shop.class));

                                                    for (CartItem cartItem : cart.getCart_items()) {
                                                        db.collection("meals").document(cartItem.getMeal_id())
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        Log.d("rrrradad", "dasdasd");
                                                                        if (task.isSuccessful()) {
                                                                            new Thread(() -> {
                                                                                cartItem.setMeal(task.getResult().toObject(Meal.class));
                                                                                try {
                                                                                    Log.d("rrrrxxsdsd", cartItem.getMeal().getImage_src());
                                                                                    Bitmap bm = Picasso.get()
                                                                                            .load("http://10.0.140.232/foody/" + cartItem.getMeal().getImage_src())
                                                                                            .get();
                                                                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                                                    bm.compress(Bitmap.CompressFormat.JPEG,80,stream);
                                                                                    byte[] byteArray = stream.toByteArray();
                                                                                    cartItem.getMeal().setImage(byteArray);
                                                                                    carts.add(cart);
                                                                                    handler.post(() -> {
                                                                                        adapter.notifyDataSetChanged();
                                                                                    });

                                                                                } catch (IOException e) {
                                                                                    e.printStackTrace();
                                                                                }

                                                                            }).start();
                                                                        }
                                                                    }
                                                                });
                                                    }

                                                }
                                            }
                                        });
//                                snapshot.getReference().collection("cart_items").get()
//                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                if (task.isSuccessful()) {
//                                                    List<CartItem> cartItems = new ArrayList<>();
//                                                    cart.setCart_items(cartItems);
//                                                    for (QueryDocumentSnapshot snapshot1 : task.getResult()) {
//                                                        CartItem cartItem = snapshot1.toObject(CartItem.class);
//                                                        cartItems.add(cartItem);
//                                                        db.collection("meals").document(cartItem.getMeal_id())
//                                                                .get()
//                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                                    @Override
//                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                        Log.d("rrrradad", "dasdasd");
//                                                                        if (task.isSuccessful()) {
//                                                                            new Thread(() -> {
//                                                                                cartItem.setMeal(task.getResult().toObject(Meal.class));
//                                                                                try {
//                                                                                    Log.d("rrrrxxsdsd", cartItem.getMeal().getImage_src());
//                                                                                    Bitmap bm = Picasso.get()
//                                                                                            .load("http://10.0.140.232/foody/" + cartItem.getMeal().getImage_src())
//                                                                                            .get();
//                                                                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                                                                    bm.compress(Bitmap.CompressFormat.JPEG,80,stream);
//                                                                                    byte[] byteArray = stream.toByteArray();
//                                                                                    cartItem.getMeal().setImage(byteArray);
//                                                                                    handler.post(() -> {
//                                                                                        adapter.notifyDataSetChanged();
//                                                                                    });
//
//                                                                                } catch (IOException e) {
//                                                                                    e.printStackTrace();
//                                                                                }
//
//                                                                            }).start();
//                                                                        }
//                                                                    }
//                                                                });
//                                                    }
//
//                                                }
//                                            }
//                                        });
                            }
                        }
                    }
                });
    }
}

