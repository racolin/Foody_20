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
import com.google.firebase.auth.FirebaseUser;
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
import hcmute.spkt.group20.foody_20.adapter.MealAdapter;
import hcmute.spkt.group20.foody_20.model.Meal;
import hcmute.spkt.group20.foody_20.model.Shop;
import hcmute.spkt.group20.foody_20.model.User;

public class MealFragment extends Fragment {
    List<Meal> meals;
    MealAdapter adapter;
    RecyclerView rv_2_items;
    int type = -1;
    String key;
    FirebaseFirestore db;
    StorageReference reference;

    public MealFragment() {
        meals = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
    }

    public MealFragment(int type, String key) {
        db = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
        this.type = type;
        this.key = key;
        meals = new ArrayList<>();
    }

    public MealFragment(int type) {
        db = FirebaseFirestore.getInstance();
        reference = FirebaseStorage.getInstance().getReference();
        this.type = type;
        meals = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meals, container, false);
        adapter = new MealAdapter(getContext(), meals);

        rv_2_items = view.findViewById(R.id.rv_2_items);
        rv_2_items.setAdapter(adapter);
        rv_2_items.setLayoutManager(new GridLayoutManager(getContext(), 2));

        if (type == 0) {
            getOutstandingMeals();
        } if (type == 1) {
            getNearMeals();
        } if (type == 2) {
            getSavedMeals();
        }

        return view;
    }

    public void getSavedMeals() {
        String uid = FirebaseAuth.getInstance().getUid();
        db.collection("meals").whereArrayContains("user_save", uid).get()
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
                                        Meal m = snapshot.toObject(Meal.class);
                                        m.setImage(byteArray);
                                        db.collection("shops").document(m.getShop_id())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            m.setShop(task.getResult().toObject(Shop.class));
                                                            meals.add(m);
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

    public void getOutstandingMeals() {
        db.collection("meals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                                new Thread(() -> {
                                    try {
                                        Bitmap bm = Picasso.get()
                                                .load("http://10.0.140.232/foody/" + snapshot.get("image_src").toString())
                                                    .get();
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bm.compress(Bitmap.CompressFormat.JPEG,80,stream);
                                        byte[] byteArray = stream.toByteArray();
                                        Meal m = snapshot.toObject(Meal.class);
                                        m.setImage(byteArray);
                                        db.collection("shops").document(m.getShop_id())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            m.setShop(task.getResult().toObject(Shop.class));
                                                            meals.add(m);
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
//
//                                                if (task.isSuccessful()) {
//
//                                                    Meal m = snapshot.toObject(Meal.class);
//                                                    m.setImage(task.getResult());
//                                                    db.collection("shops").document(m.getShop_id())
//                                                            .get()
//                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                                @Override
//                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                    if (task.isSuccessful()) {
//                                                                        m.setShop(task.getResult().toObject(Shop.class));
//                                                                        meals.add(m);
//                                                                        adapter.notifyDataSetChanged();
//                                                                    }
//                                                                }
//                                                            });
//                                                } else {
//                                                    Log.d("rrr", "fdsfsdf");
//
//                                                }
//                                            }
//                                        });
                            }
                        }
                    }
                });
    }

    public void getNearMeals() {
        db.collection("meals")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                                new Thread(() -> {
                                    try {
                                        Bitmap bm = Picasso.get()
                                                .load("http://10.0.140.232/foody/" + snapshot.get("image_src").toString())
                                                .get();
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bm.compress(Bitmap.CompressFormat.JPEG,80,stream);
                                        byte[] byteArray = stream.toByteArray();
                                        Meal m = snapshot.toObject(Meal.class);
                                        m.setImage(byteArray);
                                        db.collection("shops").document(m.getShop_id())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            m.setShop(task.getResult().toObject(Shop.class));
                                                            meals.add(m);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    }
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
//                                                    db.collection("shops").document(m.getShop_id())
//                                                            .get()
//                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                                @Override
//                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                    if (task.isSuccessful()) {
//                                                                        m.setShop(task.getResult().toObject(Shop.class));
//                                                                        meals.add(m);
//                                                                        adapter.notifyDataSetChanged();
//                                                                    }
//                                                                }
//                                                            });
//                                                }
//                                            }
//                                        });
                            }
                        }
                    }
                });
    }
}

