package hcmute.spkt.group20.foody_20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import hcmute.spkt.group20.foody_20.fragment.CartFragment;
import hcmute.spkt.group20.foody_20.fragment.HomeFragment;
import hcmute.spkt.group20.foody_20.fragment.NotificationFragment;
import hcmute.spkt.group20.foody_20.fragment.OrderFragment;
import hcmute.spkt.group20.foody_20.fragment.SavedFragment;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView nb_navigation;
    DrawerLayout dl_user;
    NavigationView nv_user;
    MovableFloatingActionButton fab_user;
    FirebaseAuth auth;
    public static final String TAG = "rrr2";

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            loginUpdateUI();
        } else {
            logoutUpdateUI();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();

        mapping();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_content, new HomeFragment());
        transaction.commit();

    }

    public void mapping() {
        nb_navigation = findViewById(R.id.nb_navigation);
        categoriesEvent();

        dl_user = findViewById(R.id.dl_user);

        nv_user = findViewById(R.id.nv_user);
        navigationEvent();
        fab_user = findViewById(R.id.fab_user);

        fab_user.setOnClickListener(v -> {
            if (!dl_user.isOpen()) {
                dl_user.openDrawer(GravityCompat.START);
            }
        });    }

    public void navigationEvent() {
        nv_user.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nv_log_out:
                        dl_user.closeDrawer(GravityCompat.START);
                        logout();
                        break;
                    case R.id.nv_log_in:
                        dl_user.closeDrawer(GravityCompat.START);
                        login();
//                        Intent i1 = new Intent(HomeActivity.this, LoginActivity.class);
//                        loginLauncher.launch(i1);
                        break;
                    case R.id.nv_info:
                        dl_user.closeDrawer(GravityCompat.START);
                        Intent i2 = new Intent(HomeActivity.this, UserInfoActivity.class);
                        startActivity(i2);
                        break;
                    case R.id.nv_share:
                        dl_user.closeDrawer(GravityCompat.START);
                        Toast.makeText(HomeActivity.this, "Share cho bạn bè xem!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nv_rule:
                        dl_user.closeDrawer(GravityCompat.START);
                        Intent i3 = new Intent(HomeActivity.this, RuleActivity.class);
                        startActivity(i3);
                        break;
                    case R.id.nv_home:
                        dl_user.closeDrawer(GravityCompat.START);
                        nb_navigation.setSelectedItemId(R.id.action_home);
                        break;
                    case R.id.nv_notification:
                        dl_user.closeDrawer(GravityCompat.START);
                        nb_navigation.setSelectedItemId(R.id.action_notification);
                        break;
                    case R.id.nv_saved:
                        dl_user.closeDrawer(GravityCompat.START);
                        nb_navigation.setSelectedItemId(R.id.action_saved);
                        break;
                    case R.id.nv_cart:
                        dl_user.closeDrawer(GravityCompat.START);
                        nb_navigation.setSelectedItemId(R.id.action_cart);
                        break;
                    case R.id.nv_order:
                        dl_user.closeDrawer(GravityCompat.START);
                        nb_navigation.setSelectedItemId(R.id.action_order);
                        break;
                }
                return true;
            }
        });
    }

    private void loginUpdateUI() {
        nv_user.getMenu().getItem(4).setEnabled(true);
        nv_user.getMenu().getItem(0).setEnabled(false);
    }

    private void logoutUpdateUI() {
        nv_user.getMenu().getItem(0).setEnabled(true);
        nv_user.getMenu().getItem(4).setEnabled(false);
    }

    private void login() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        loginUpdateUI();
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(this, "Bạn đã đăng xuất!", Toast.LENGTH_SHORT).show();
        logoutUpdateUI();
    }

    public void categoriesEvent() {
        nb_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fab_user.show();
                        transaction.replace(R.id.fl_content, new HomeFragment());
                        break;
                    case R.id.action_notification:
                        transaction.replace(R.id.fl_content, new NotificationFragment());
                        break;
                    case R.id.action_saved:
                        transaction.replace(R.id.fl_content, new SavedFragment());
                        break;
                    case R.id.action_cart:
                        transaction.replace(R.id.fl_content, new CartFragment());
                        break;
                    case R.id.action_order:
                        transaction.replace(R.id.fl_content, new OrderFragment());
                        break;
                }
                transaction.commit();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (dl_user.isOpen()) {
            dl_user.closeDrawer(GravityCompat.START);
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentById(R.id.fl_content) instanceof HomeFragment) {
            finish();
        } else {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fl_content, new HomeFragment());
            transaction.commit();
            nb_navigation.setSelectedItemId(R.id.action_home);
            return;
        }
        super.onBackPressed();
    }
}