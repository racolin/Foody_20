package hcmute.spkt.group20.foody_20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hcmute.spkt.group20.foody_20.adapter.CommentAdapter;
import hcmute.spkt.group20.foody_20.adapter.MealAdapter;
import hcmute.spkt.group20.foody_20.model.Cart;
import hcmute.spkt.group20.foody_20.model.CartItem;
import hcmute.spkt.group20.foody_20.model.Comment;
import hcmute.spkt.group20.foody_20.model.Meal;
import hcmute.spkt.group20.foody_20.model.Shop;
import hcmute.spkt.group20.foody_20.model.ShopChain;
import hcmute.spkt.group20.foody_20.model.User;

public class MealActivity extends AppCompatActivity {

    RecyclerView rv_meals, rv_comments;
    TextView tv_save, tv_share;
    TextView tv_name, tv_shop_name, tv_address, tv_distance, tv_price,
            tv_origin_price, tv_description, tv_time_open, tv_comment_count,
            tv_saved_count, tv_shared_count;
    RatingBar rb_rated;
    Button btn_order_now;
    Button btn_contact, btn_submit;
    ImageButton iv_add_cart;
    ImageView iv_meal;
    Meal meal;
    List<Comment> comments;
    MealAdapter mealAdapter;
    CommentAdapter commentAdapter;
    List<Meal> meals;
//    SlidrInterface slidr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        meal = (Meal) getIntent().getSerializableExtra("meal");

        loadShop();
        initUI();
        initListener();
    }

    private void initUI() {
        tv_shop_name = findViewById(R.id.tv_shop_name);
        tv_shop_name.setText(meal.getShop().getName(30));

        iv_meal = findViewById(R.id.iv_meal);
        iv_meal.setImageBitmap(Support.convertBitmap(meal.getImage()));

        tv_name = findViewById(R.id.tv_name);
        tv_name.setText(meal.getName());

        tv_address = findViewById(R.id.tv_address);
        tv_address.setText(meal.getShop().getAddress());

        tv_distance = findViewById(R.id.tv_distance);
        tv_distance.setText(meal.getShop().getDistance());

        tv_price = findViewById(R.id.tv_price);
        tv_price.setText(Support.toCurrency(meal.getPrice()));

        tv_origin_price = findViewById(R.id.tv_origin_price);
        tv_origin_price.setText(Support.toCurrency(meal.getOrigin_price()));

        if (meal.getPrice() == meal.getOrigin_price()) {
            tv_origin_price.setVisibility(View.GONE);
        }

        tv_description = findViewById(R.id.tv_description);
        tv_description.setText(meal.getDescription());

        tv_time_open = findViewById(R.id.tv_time_open);
        tv_time_open.setText(meal.getShop().getTime_open());

        tv_comment_count = findViewById(R.id.tv_comment_count);
        tv_comment_count.setText(String.valueOf(meal.getComment_count()));

        tv_saved_count = findViewById(R.id.tv_saved_count);
        tv_saved_count.setText(String.valueOf(meal.getSaved_count()));

        tv_shared_count = findViewById(R.id.tv_shared_count);
        tv_shared_count.setText(String.valueOf(meal.getShared_count()));

        tv_save = findViewById(R.id.tv_save);
        tv_share = findViewById(R.id.tv_share);

        rb_rated = findViewById(R.id.rb_rated);
        rb_rated.setRating(meal.getRated());


        iv_add_cart = findViewById(R.id.iv_add_cart);
        btn_contact = findViewById(R.id.btn_contact);
        btn_submit = findViewById(R.id.btn_submit);
        btn_order_now = findViewById(R.id.btn_order_now);

        rv_comments = findViewById(R.id.rv_shops);
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments);
        rv_comments.setAdapter(commentAdapter);
        loadComment();
        rv_comments.setLayoutManager(new LinearLayoutManager(this));

        rv_meals = findViewById(R.id.rv_meals);
        meals = new ArrayList<>();
        mealAdapter = new MealAdapter(this, meals);
        rv_meals.setAdapter(mealAdapter);
        loadMeals();
        rv_meals.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void initListener() {

        btn_contact.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactDialog.class);
            intent.putExtra("shop", meal.getShop());
            startActivity(intent);
        });

        btn_order_now.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderActivity.class);
            startActivity(intent);
        });

        iv_add_cart.setOnClickListener(v -> {
            addCart();
        });

        btn_submit.setOnClickListener(v -> {
            Toast.makeText(this, "Xử lý sự kiện đăng bình luận", Toast.LENGTH_SHORT).show();
        });

        tv_share.setOnClickListener(v -> {
            Toast.makeText(this, "Xử lý sự kiện share", Toast.LENGTH_SHORT).show();
        });

        tv_save.setOnClickListener(v -> {
            Toast.makeText(this, "Xử lý sự kiện lưu", Toast.LENGTH_SHORT).show();
        });
    }

    public void addCart() {
        String uid = FirebaseAuth.getInstance().getUid();
        String shop_id = meal.getShop_id();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("carts").whereEqualTo("uid", uid).whereEqualTo("shop_id", shop_id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // nếu đã có giỏ cho các món cùng shop
                            if (task.getResult().size() == 1) {
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    Cart cart = snapshot.toObject(Cart.class);
                                    db.collection("carts").document(snapshot.getId()).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        boolean check = true;
                                                        Log.d("rrrrraaarrr", String.valueOf(cart.getCart_items().size()));
                                                        for (CartItem item : cart.getCart_items()) {
                                                            if (item.getMeal_id().equals(meal.getMeal_id())) {
                                                                Log.d("rrrrraaarrr", cart.getCart_items().get(0).getMeal_id());
                                                                check = false;
                                                                item.setAmount(item.getAmount() + 1);
                                                            }
                                                        }
                                                        if (check) {
                                                            cart.getCart_items().add(new CartItem(meal.getMeal_id(), 1));
                                                        }
                                                        Toast.makeText(MealActivity.this, "Thêm vào giỏ",Toast.LENGTH_SHORT).show();
                                                        db.collection("carts").document(snapshot.getId()).set(cart);
//                                                        task.getResult().getReference().set(cart.getCart_items());
//                                                        List<CartItem>task.getResult().getData().get("cart_items");
                                                    }
                                                }
                                            });
//                                    snapshot.getReference().collection("cart_items")
//                                            .whereEqualTo("meal_id", meal.getMeal_id()).get()
//                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                    if (task.isSuccessful()) {
//                                                        if (task.getResult().size() == 1) {
//                                                            task.getResult().getDocuments().get(0).getReference()
//                                                                    .update("amount", FieldValue.increment(1));
//                                                            Toast.makeText(MealActivity.this, "Vào giỏ hàng xem số lượng muốn đặt", Toast.LENGTH_SHORT).show();
//                                                        } else {
//                                                            CartItem item = new CartItem();
//                                                            item.setMeal_id(meal.getMeal_id());
//                                                            item.setAmount(1);
//                                                            snapshot.getReference().collection("cart_items")
//                                                                    .add(item);
//                                                            Toast.makeText(MealActivity.this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
//
//                                                        }
//                                                    }
//                                                }
//                                            });
                                }
                            } // chưa có giỏ nào của các món cùng shop. tạo giỏ mới
                            else if (task.getResult().size() == 0) {
                                Cart cart = new Cart();
                                cart.setShop_id(meal.getShop_id());
                                cart.setUid(uid);
                                CartItem item = new CartItem();
                                item.setAmount(1);
                                item.setMeal_id(meal.getMeal_id());
                                List<CartItem> items = new ArrayList<>();
                                items.add(item);
                                cart.setCart_items(items);
                                String id = db.collection("carts").document().getId();

                                db.collection("carts").document(id).set(cart);
//                                String id1 = db.collection("carts").document(id).collection("cart_items")
//                                .document().getId();
//                                db.collection("carts").document(id).collection("cart_items")
//                                        .document(id1).set(cart.getCart_items());
                                Toast.makeText(MealActivity.this, "Đã tạo giỏ mới", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Log.d("rrrrr", "error");
                        }
                    }
                });
    }

    public void loadComment() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("meals").document(meal.getMeal_id()).collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Comment> cs = task.getResult().toObjects(Comment.class);
                            runOnUiThread(() -> {
                                tv_comment_count.setText(String.valueOf(cs.size()));
                                rb_rated.setRating(meal.getRated());
                            });
                            for (Comment c : cs) {
                                db.collection("users").document(c.getUid()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                User u = task.getResult().toObject(User.class);
                                                c.setUser(u);
                                                new Thread(() -> {
                                                    try {
                                                        Bitmap bm = Picasso.get()
                                                                .load("http://10.0.140.232/foody/" + u.getImage_src())
                                                                .get();
                                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                        bm.compress(Bitmap.CompressFormat.JPEG,80,stream);
                                                        byte[] byteArray = stream.toByteArray();
                                                        u.setImage(byteArray);

                                                        comments.add(c);
                                                        runOnUiThread(() -> {
                                                            commentAdapter.notifyDataSetChanged();
                                                        });
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }

                                                }).start();
                                            }
                                        });
                            }
                        }
                    }
                });

//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            meal.getShop().setShop_chain(task.getResult().toObject(ShopChain.class));
//                            runOnUiThread(() -> {
//                                tv_shop_name.setOnClickListener(v -> {
//                                    Intent intent = new Intent(MealActivity.this, ShopActivity.class);
//                                    intent.putExtra("shop", meal.getShop());
//                                    startActivity(intent);
//                                });
//                            });
//                        }
//                    }
//                });
    }

    public void loadMeals() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("meals").whereEqualTo("shop_id", meal.getShop_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                Log.d("rrr", snapshot.getId());
                                Meal m = snapshot.toObject(Meal.class);
                                new Thread(() -> {
                                    try {
                                        Bitmap bm = Picasso.get()
                                                .load("http://10.0.140.232/foody/" + m.getImage_src())
                                                .get();
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bm.compress(Bitmap.CompressFormat.JPEG,80,stream);
                                        byte[] byteArray = stream.toByteArray();
                                        m.setImage(byteArray);
                                        db.collection("shops").document(m.getShop_id())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            m.setShop(task.getResult().toObject(Shop.class));
                                                            meals.add(m);
                                                            runOnUiThread(() -> {
                                                                mealAdapter.notifyDataSetChanged();
                                                            });
                                                        }
                                                    }
                                                });
                                    } catch (IOException e) {
                                        Log.d("rrrxxr", snapshot.get("image_src").toString());
                                        e.printStackTrace();
                                    }

                                }).start();
                            }
                        }
                    }
                });
    }

    public void loadShop() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("shop_chain").document(meal.getShop().getShop_chain_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            meal.getShop().setShop_chain(task.getResult().toObject(ShopChain.class));
                            runOnUiThread(() -> {
                                tv_shop_name.setOnClickListener(v -> {
                                    Intent intent = new Intent(MealActivity.this, ShopActivity.class);
                                    intent.putExtra("shop", meal.getShop());
                                    startActivity(intent);
                                });
                            });
                        }
                    }
                });
    }
}