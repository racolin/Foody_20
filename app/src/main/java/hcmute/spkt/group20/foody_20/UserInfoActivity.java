package hcmute.spkt.group20.foody_20;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import hcmute.spkt.group20.foody_20.model.User;

public class UserInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = "UserInfoActivity";
    Spinner sp_genders;
    ImageButton ib_dob;
    TextView tv_dob, tv_phone, tv_gmail, tv_facebook;
    Geocoder search;
    Address address;
    SupportMapFragment mapFragment;
    Handler handler;
    ImageView iv_phone, iv_facebook, iv_google;

    CallbackManager callbackManager;

    GoogleSignInClient googleSignInClient;

    FirebaseAuth auth;

    ArrayAdapter<String> gendersAdapter;
    List<String> genders;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    ActivityResultLauncher<Intent> verifyLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Toast.makeText(getApplicationContext(),
                                R.string.login_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                R.string.login_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    ActivityResultLauncher<Intent> ggLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        handleSignInResult(task);
                    }
                }
            }
    );

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        GoogleSignInAccount account = null;
        try {
            account = task.getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            handleCredential(credential);
        } catch (ApiException e) {
            Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void handleCredential(AuthCredential credential) {
//        auth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            Toast.makeText(UserInfoActivity.this, R.string.login_success,
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(UserInfoActivity.this, R.string.login_failed,
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
        auth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), R.string.link_success,
                                    Toast.LENGTH_SHORT).show();
                            for (UserInfo info : auth.getCurrentUser().getProviderData()) {
                                Log.d("rrr111", info.getProviderId());
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.link_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        auth = FirebaseAuth.getInstance();
        // login google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        // login google
        //  login facebook
        callbackManager = CallbackManager.Factory.create();
        //  login facebook
        mapping();
    }

    public User getUser() {
        User user = new User();
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        for (UserInfo info : u.getProviderData()) {
            user.setFull_name(u.getDisplayName());
            user.setUid(u.getUid());

//            if (info.getProviderId().equals(getString(R.string.google))) {
//                user.setGmail(info.getEmail());
//            }
//            if (info.getProviderId().equals(getString(R.string.facebook))) {
//                user.setFacebook(Profile.getCurrentProfile().getId());
//            }
//            if (info.getProviderId().equals(getString(R.string.phone))) {
//                user.setPhone(info.getPhoneNumber());
//            }
        }
        return user;
    }

    public void mapping() {
        iv_phone = findViewById(R.id.iv_phone);
        iv_facebook = findViewById(R.id.iv_facebook);
        iv_google = findViewById(R.id.iv_google);

        //  login facebook
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String token = loginResult.getAccessToken().getToken();
                        AuthCredential credential = FacebookAuthProvider.getCredential(token);
                        handleCredential(credential);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
                    }
                });
        //  login facebook

        iv_phone.setOnClickListener(v -> {
            if (!Support.checkPhone()) {
                Intent intent = new Intent(this, VerifyOTPActivity.class);
                verifyLauncher.launch(intent);
            }
        });
        iv_facebook.setOnClickListener(v -> {
            if (!Support.checkFacebook()) {
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
            }
        });
        iv_google.setOnClickListener(v -> {
            if (!Support.checkGoogle()) {
                Intent intent = googleSignInClient.getSignInIntent();
                ggLauncher.launch(intent);
            }
        });


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fm_map);
        sp_genders = findViewById(R.id.sp_gender);
        createGenders();
        sp_genders.setSelection(genders.indexOf(getString(R.string.male)));

        Calendar date = new GregorianCalendar();

        try {
            date.setTime(format.parse(getResources().getString(R.string.date_default)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        tv_dob = findViewById(R.id.tv_dob);

        DatePickerDialog picker = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, i);
                        calendar.set(Calendar.MONTH, i1);
                        calendar.set(Calendar.DATE, i2);
                        tv_dob.setText(format.format(new Date(calendar.getTimeInMillis())));
                    }
                }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));

        ib_dob = findViewById(R.id.ib_choose_dob);
        ib_dob.setOnClickListener(v -> {
            picker.show();
        });

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fm_map, mapFragment)
                .commit();

    }

    public void createGenders() {
        genders = Arrays.asList(getResources().getStringArray(R.array.genders));
        gendersAdapter = new ArrayAdapter<String>(this, R.layout.gender_layout, genders);
        sp_genders.setAdapter(gendersAdapter);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng sydney = new LatLng(-33.852, 151.211);
//        Geocoder geocoder = Geocoder
        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}