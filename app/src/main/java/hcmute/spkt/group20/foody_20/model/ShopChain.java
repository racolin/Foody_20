package hcmute.spkt.group20.foody_20.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/*OK*/
public class ShopChain implements Serializable {
    private String shop_chain_id;
    private String name;
    private String description;
    private List<Shop> shops; //
    private byte[] image;
    private String image_src;

    public ShopChain() {

    }

    public ShopChain(String shop_chain_id, String name, String description, List<Shop> shops, byte[] image, String image_src) {
        this.shop_chain_id = shop_chain_id;
        this.name = name;
        this.description = description;
        this.shops = shops;
        this.image = image;
        this.image_src = image_src;
    }

    public String getShop_chain_id() {
        return shop_chain_id;
    }

    public void setShop_chain_id(String shop_chain_id) {
        this.shop_chain_id = shop_chain_id;
    }

    @Exclude
    public byte[] getImage() {
        return image;
    }

    @Exclude
    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImage_src() {
        return image_src;
    }

    public void setImage_src(String image_src) {
        this.image_src = image_src;
    }

    public String getName() {
        return name;
    }

    @Exclude
    public String getName(int limit) {
        return name.length() <= limit ? name : name.substring(0, limit - 3) + "...";
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public int getShop_count() {
        return shops == null ? 0 : shops.size();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Exclude
    public List<Shop> getShops() {
        return shops;
    }

    @Exclude
    public void setShops(List<Shop> shops) {
        this.shops = shops;
    }
}
