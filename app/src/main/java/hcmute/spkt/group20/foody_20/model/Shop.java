package hcmute.spkt.group20.foody_20.model;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/*OK*/

public class Shop implements Serializable {
    private String shop_id;
    private String name;
    private ShopChain shop_chain; //
    private String shop_chain_id;
    private String time_open;
    private String description;
    private String phone_number;
    private String address;
    private String image_src;
    private List<String> user_save;
    private byte[] image; //
    private int shared_count;
    private List<Meal> meals; //
    private List<Shop> related_shops; //

    public Shop() {

    }

    public Shop(String shop_id, String name, ShopChain shop_chain, String shop_chain_id, String time_open,
                String description, String phone_number, String address, String image_src,
                byte[] image, int shared_count, List<String> user_save, List<Meal> meals, List<Shop> related_shops) {
        this.name = name;
        this.shop_id = shop_id;
        this.shop_chain = shop_chain;
        this.shop_chain_id = shop_chain_id;
        this.time_open = time_open;
        this.description = description;
        this.phone_number = phone_number;
        this.address = address;
        this.image_src = image_src;
        this.image = image;
        this.user_save = user_save;
        this.shared_count = shared_count;
        this.meals = meals;
        this.related_shops = related_shops;
    }

    public List<String> getUser_save() {
        return user_save;
    }

    public void setUser_save(List<String> user_save) {
        this.user_save = user_save;
    }

    public int getShared_count() {
        return shared_count;
    }

    public void setShared_count(int shared_count) {
        this.shared_count = shared_count;
    }

    @Exclude
    public int getSaved_count() {
        return user_save != null ? user_save.size() : 0;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getShop_chain_id() {
        return shop_chain_id;
    }

    public void setShop_chain_id(String shop_chain_id) {
        this.shop_chain_id = shop_chain_id;
    }

    public String getImage_src() {
        return image_src;
    }

    public void setImage_src(String image_src) {
        this.image_src = image_src;
    }

    @Exclude
    public String getDistance() {
        Random rd = new Random();
        return String.valueOf(((int) (rd.nextFloat() * 10)) )+ "km";
    }

    @Exclude
    public String getName(int limit) {
        return name.length() <= limit ? name : name.substring(0, limit - 3) + "...";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public ShopChain getShop_chain() {
        return shop_chain;
    }

    @Exclude
    public void setShop_chain(ShopChain shop_chain) {
        this.shop_chain = shop_chain;
    }

    public String getTime_open() {
        return time_open;
    }

    public void setTime_open(String time_open) {
        this.time_open = time_open;
    }

    public String getDescription() {
        return description;
    }

    @Exclude
    public String getDescription(int limit) {
        return name.length() <= limit ? name : name.substring(0, limit - 3) + "...";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Exclude
    public byte[] getImage() {
        return image;
    }

    @Exclude
    public void setImage(byte[] image) {
        this.image = image;
    }

    @Exclude
    public List<Meal> getMeals() {
        return meals;
    }

    @Exclude
    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

    @Exclude
    public List<Shop> getRelated_shops() {
        return related_shops;
    }

    @Exclude
    public void setRelated_shops(List<Shop> related_shops) {
        this.related_shops = related_shops;
    }
}
