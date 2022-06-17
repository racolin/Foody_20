package hcmute.spkt.group20.foody_20;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hcmute.spkt.group20.foody_20.adapter.SliderAdapter;
import hcmute.spkt.group20.foody_20.model.Cart;
import hcmute.spkt.group20.foody_20.model.Meal;
import hcmute.spkt.group20.foody_20.model.Notification;
import hcmute.spkt.group20.foody_20.model.Order;
import hcmute.spkt.group20.foody_20.model.RatingClass;
import hcmute.spkt.group20.foody_20.model.Shop;
import hcmute.spkt.group20.foody_20.model.Slider;
import hcmute.spkt.group20.foody_20.state_fragment.HomeStateFragment;

public class Support {

    public static List<RatingClass> rating = new ArrayList<>();

    public static boolean checkTypeLogin(String type) {
        boolean result = false;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo info : user.getProviderData()) {
                result = result || info.getProviderId().equals(type);
            }
        }
        return result;
    }
    public static boolean checkGoogle() {
        return Support.checkTypeLogin("google.com");
    }
    public static boolean checkFacebook() {
        return Support.checkTypeLogin("facebook.com");
    }
    public static boolean checkPhone() {
        return Support.checkTypeLogin("phone");
    }
    public static boolean checkLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user == null ? false : user.getProviderData().size() > 1;
    }

    public static String toCurrency(int value) {
        String input = String.valueOf(value);
        StringBuilder builder = new StringBuilder();
        int len = input.length();
        for (int i = len - 1; i > 0; i--) {
            builder.append(input.charAt(i));
            if ((len - i) % 3 == 0) {
                builder.append(",");
                builder.toString();
            }
        }
        return builder.append(input.charAt(0)).reverse().toString() + "đ";
    }

    public static String toDateString(Date date, String fm) {
        SimpleDateFormat format = new SimpleDateFormat(fm);
        return format.format(date);
    }

    public static String toDateString(Calendar calendar, String fm) {
        SimpleDateFormat format = new SimpleDateFormat(fm);
        return format.format(calendar.getTime());
    }

    public static Bitmap convertBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static void getRatingClass() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("rating_class")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            rating = task.getResult().toObjects(RatingClass.class);
                        } else {
                            rating.add(new RatingClass("Ngon tuyệt!", 5, 4));
                            rating.add(new RatingClass("Khá ổn!!", 4, 3));
                            rating.add(new RatingClass("Bình thường!", 3, 2));
                            rating.add(new RatingClass("Không ngon!", 2, 1));
                            rating.add(new RatingClass("Dở tệ!", 1, 0));
                        }
                    }
                });
    }

    public static String getRatingType(float rate) {
        for (RatingClass rc : rating) {
            if (rc.isThisType(rate)) {
                return rc.getType();
            }
        }
        return "Ngon tuyệt!";
    }

    public static List<Order> getAllOrder() {
        List<Order> orders = new ArrayList<>();
        return orders;
    }

    public static List<Meal> getRelatedMeal(Meal meal) {
        List<Meal> meals = new ArrayList<>();
        return  meals;
    }


    public static List<Shop> getRelatedShops(Shop shop) {
        List<Shop> shops = new ArrayList<>();
        return  shops;
    }

    public static List<Notification> getNotifications() {
        List<Notification> notifications = new ArrayList<>();
        return notifications;
    }


    public static List<Meal> getSavedMeals() {
        List<Meal> meals = new ArrayList<>();
        return meals;
    }

    public static List<Shop> getSavedShops() {
        List<Shop> shops = new ArrayList<>();
        return shops;
    }

    public static List<Cart> getCarts() {
        List<Cart> carts = new ArrayList<>();
        return carts;
    }

    public static List<Byte[]> getAllShopsImage() {
        List<Byte[]> images = new ArrayList<>();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("shops")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentSnapshot snapshot;
                                String id = document.getReference().getId();
                                StorageReference image = reference.child("shops/" + id + ".png");
                                image.getBytes(1024 * 1024)
                                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                images.add(Support.toByteArray(bytes));
                                            }
                                        });
                            }
                        } else {

                        }
                    }
                });
        return images;
    }

    public static Byte[] toByteArray(byte[] bytes) {
        int len = bytes.length;
        Byte[] result = new Byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = bytes[i];
        }
        return result;
    }
}

