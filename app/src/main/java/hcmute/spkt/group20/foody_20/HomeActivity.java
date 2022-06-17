package hcmute.spkt.group20.foody_20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

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
    public static final String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null && !auth.getCurrentUser().isAnonymous()) {
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
        if (auth.getCurrentUser() == null) {
            becomeAnonymous();
        }

        initUI();

        initListener();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fl_content, new HomeFragment());
        transaction.addToBackStack(HomeFragment.class.getSimpleName());
        transaction.commit();
    }

    private void initUI() {

        nb_navigation = findViewById(R.id.nb_navigation);

        View vv = LayoutInflater.from(this).inflate(R.layout.nofitication_number, (BottomNavigationView) nb_navigation.getChildAt(2), false);

        nb_navigation.addView(vv);

        dl_user = findViewById(R.id.dl_user);

        nv_user = findViewById(R.id.nv_user);

        fab_user = findViewById(R.id.fab_user);
    }

    private void initListener() {

        categoriesEvent();

        navigationEvent();

        fab_user.setOnClickListener(v -> {
            if (!dl_user.isOpen()) {
                dl_user.openDrawer(GravityCompat.START);
            }
        });
    }

    private void becomeAnonymous() {

        auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInAnonymously:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                        }
                    }
                });
    }

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
                        break;
                    case R.id.nv_info:
                        dl_user.closeDrawer(GravityCompat.START);
                        if (Support.checkLogin()) {
                            Intent i2 = new Intent(HomeActivity.this, UserInfoActivity.class);
                            startActivity(i2);
                        } else {
                            Intent i2 = new Intent(HomeActivity.this, LoginActivity.class);
                            startActivity(i2);
                            Toast.makeText(getApplicationContext(), R.string.login_need, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nv_share:
                        dl_user.closeDrawer(GravityCompat.START);
                        if (Support.checkFacebook()) {
//                            Thực hiện share
                        } else {
//                            Gợi ý phải vào facebook để share
//                            Nếu đã đăng nhập nhưng không có facebook thì cho trang chủ để link
//                            Nếu chưa đăng nhập thì cho về trang login
                            Toast.makeText(getApplicationContext(),
                                    R.string.facebook_login_need,
                                    Toast.LENGTH_SHORT).show();
                        }
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
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        auth.signOut();
//        Đăng xuất khỏi facebook vì facebook cần phải login thêm
        LoginManager.getInstance().logOut();

        Toast.makeText(this, R.string.logout_toast, Toast.LENGTH_SHORT).show();
        becomeAnonymous();
        logoutUpdateUI();
    }

    public void categoriesEvent() {
        nb_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager manager = getSupportFragmentManager();
//                manager.pop/
                FragmentTransaction transaction = manager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.action_home:
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
//            manager.popBackStack();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fl_content, new HomeFragment());
            transaction.commit();
            nb_navigation.setSelectedItemId(R.id.action_home);
            return;
        }
        super.onBackPressed();
    }
}