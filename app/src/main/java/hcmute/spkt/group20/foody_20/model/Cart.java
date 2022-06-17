package hcmute.spkt.group20.foody_20.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.List;

/*OK*/

public class Cart implements Serializable {
    private List<CartItem> cart_items;
    private String uid;
    private String shop_id;
    private Shop shop;//

    public Cart() {

    }

    public Cart(List<CartItem> cart_items, String uid) {
        this.cart_items = cart_items;
        this.uid = uid;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    @Exclude
    public Shop getShop() {
        return shop;
    }

    @Exclude
    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Exclude
    public int getTotal_price() {
        if (cart_items != null) {
            int r = 0;
            for (CartItem item : cart_items) {
                r += item.getMeal() == null ? 0 : item.getMeal().getPrice() * item.getAmount();
            }
            return r;
        }
        return 0;
    }
@Exclude
    @Override
    public String toString() {
        return "Cart{" +
                "cart_items=" + cart_items +
                ", uid='" + uid + '\'' +
                ", shop_id='" + shop_id + '\'' +
                ", shop=" + shop +
                '}';
    }

    public List<CartItem> getCart_items() {
        return cart_items;
    }

    public void setCart_items(List<CartItem> cart_items) {
        this.cart_items = cart_items;
    }
}

