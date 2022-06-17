package hcmute.spkt.group20.foody_20.model;

/*OK*/

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    private String uid;
    private String full_name;
    private boolean gender;
    private Date dob;
    private String address;

    private List<Cart> carts; //
    private List<String> carts_id;

    private List<Order> orders; //
    private List<String> orders_id;

    private List<Discount> person_discounts;
    private List<Notification> person_notification;

    private List<Shop> shops_saved;//
    private List<String> shops_saved_id;

    private List<Meal> meals_saved;//
    private List<String> meals_saved_id;

    private String image_src;

    private byte[] image;//

    public User() {

    }

    public User(String uid, String full_name, boolean gender, Date dob, String address, List<Cart> carts,
                List<String> carts_id, List<Order> orders, List<String> orders_id, List<Discount> person_discounts,
                List<Notification> person_notification, List<Shop> shops_saved, List<String> shops_saved_id,
                List<Meal> meals_saved, List<String> meals_saved_id, String image_src, byte[] image) {
        this.uid = uid;
        this.full_name = full_name;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.carts = carts;
        this.carts_id = carts_id;
        this.orders = orders;
        this.orders_id = orders_id;
        this.person_discounts = person_discounts;
        this.person_notification = person_notification;
        this.shops_saved = shops_saved;
        this.shops_saved_id = shops_saved_id;
        this.meals_saved = meals_saved;
        this.meals_saved_id = meals_saved_id;
        this.image_src = image_src;
        this.image = image;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getImage_src() {
        return image_src;
    }

    public void setImage_src(String image_src) {
        this.image_src = image_src;
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
    public List<Cart> getCarts() {
        return carts;
    }

    @Exclude
    public void setCarts(List<Cart> carts) {
        this.carts = carts;
    }

    public List<String> getCarts_id() {
        return carts_id;
    }

    public void setCarts_id(List<String> carts_id) {
        this.carts_id = carts_id;
    }

    @Exclude
    public List<Order> getOrders() {
        return orders;
    }

    @Exclude
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<String> getOrders_id() {
        return orders_id;
    }

    public void setOrders_id(List<String> orders_id) {
        this.orders_id = orders_id;
    }

    public List<Discount> getPerson_discounts() {
        return person_discounts;
    }

    public void setPerson_discounts(List<Discount> person_discounts) {
        this.person_discounts = person_discounts;
    }

    public List<Notification> getPerson_notification() {
        return person_notification;
    }

    public void setPerson_notification(List<Notification> person_notification) {
        this.person_notification = person_notification;
    }

    @Exclude
    public List<Shop> getShops_saved() {
        return shops_saved;
    }

    @Exclude
    public void setShops_saved(List<Shop> shops_saved) {
        this.shops_saved = shops_saved;
    }

    public List<String> getShops_saved_id() {
        return shops_saved_id;
    }

    public void setShops_saved_id(List<String> shops_saved_id) {
//        Load shop
        this.shops_saved_id = shops_saved_id;
    }

    @Exclude
    public List<Meal> getMeals_saved() {
        return meals_saved;
    }

    @Exclude
    public void setMeals_saved(List<Meal> meals_saved) {
        this.meals_saved = meals_saved;
    }

    public List<String> getMeals_saved_id() {
        return meals_saved_id;
    }

    public void setMeals_saved_id(List<String> meals_saved_id) {
        this.meals_saved_id = meals_saved_id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
