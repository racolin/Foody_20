package hcmute.spkt.group20.foody_20;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.number.Scale;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.Date;

import hcmute.spkt.group20.foody_20.adapter.CartItemAdapter;
import hcmute.spkt.group20.foody_20.model.Cart;

public class OrderActivity extends AppCompatActivity {

    SupportMapFragment mf_order;

    TextView tv_shop_name, tv_distance, tv_total_price;
    RecyclerView rv_cart_item;
    Button btn_buy_now;

    ImageView iv_meal;
    TextView tv_price, tv_amount, tv_time;
    ImageButton ib_delete, ib_dec, ib_inc;

    ImageButton ib_choose_time;
    EditText et_phone, et_name;

    Cart cart;

    int hour, minute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        cart = (Cart) getIntent().getSerializableExtra("cart");

        initUI();
        initListener();
        
        mf_order = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fm_order);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fm_order, mapFragment)
                .commit();
    }
    
    public void initUI() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        et_name = findViewById(R.id.et_name);
        et_name.setText(auth.getCurrentUser().getDisplayName());
        et_phone = findViewById(R.id.et_phone);
        et_phone.setText(auth.getCurrentUser().getPhoneNumber());

        tv_price = findViewById(R.id.tv_price);

        tv_amount = findViewById(R.id.tv_amount);
        iv_meal = findViewById(R.id.iv_meal);
        ib_delete = findViewById(R.id.ib_delete);
        ib_dec = findViewById(R.id.ib_dec);
        ib_inc = findViewById(R.id.ib_inc);
        
        tv_total_price = findViewById(R.id.tv_total_price);
        tv_distance = findViewById(R.id.tv_distance);
        tv_shop_name = findViewById(R.id.tv_shop_name);
        rv_cart_item = findViewById(R.id.rv_cart_item);
        btn_buy_now = findViewById(R.id.btn_buy_now);

        tv_distance.setText(cart.getShop().getDistance());
        tv_total_price.setText(Support.toCurrency(cart.getTotal_price()));
        tv_shop_name.setText(cart.getShop().getName());
        rv_cart_item.setAdapter(new CartItemAdapter(this, cart.getCart_items()));

        rv_cart_item.setLayoutManager(new LinearLayoutManager(this));
        btn_buy_now.setVisibility(View.GONE);

        ib_choose_time = findViewById(R.id.ib_choose_time);
        tv_time = findViewById(R.id.tv_time);
    }
    
    public void initListener() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);
        ib_choose_time.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(OrderActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            Calendar pick = Calendar.getInstance();
                            pick.set(Calendar.YEAR, i);
                            pick.set(Calendar.MONTH, i1);
                            pick.set(Calendar.DATE, i2);
                            TimePickerDialog timePickerDialog = new TimePickerDialog(OrderActivity.this,
                                    new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                            pick.set(Calendar.HOUR, i);
                                            pick.set(Calendar.MINUTE, i1);
                                            if (calendar.compareTo(pick) > 0) {
                                                pick.set(Calendar.MINUTE, i1);
                                                pick.set(Calendar.HOUR, i);
                                                pick.set(Calendar.DATE, calendar.get(Calendar.DATE));
                                                pick.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                                                pick.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                                            }
                                            tv_time.setText(Support.toDateString(pick, "hh:mm dd/MM/yyyy"));
                                        }
                                    }, hour, minute, true);
                            timePickerDialog.show();
                        }
                    }, 2022, 5, 8);
            DatePicker datePicker = datePickerDialog.getDatePicker();
            datePicker.setMinDate(calendar.getTimeInMillis());
            datePicker.setMaxDate(calendar.getTimeInMillis() + 86400000*3);
            datePickerDialog.show();
        });
    }
}