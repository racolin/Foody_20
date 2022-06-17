package hcmute.spkt.group20.foody_20;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.Login;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginFragment;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    Button fb_login, gg_login;
    TextView pn_login;

    CallbackManager callbackManager;

    GoogleSignInClient googleSignInClient;

    FirebaseAuth auth;

    public static final String TAG = LoginActivity.class.getSimpleName();


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

    ActivityResultLauncher<Intent> verifyLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        finish();
                    }
                }
            }
    );

    @Override
    protected void onStart() {
        super.onStart();
        checkLogin();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        initUI();
        initListener();
    }

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

    private void initUI() {
        //  login google
        gg_login = findViewById(R.id.gg_login);
        //  login google
        //  login facebook
        fb_login = findViewById(R.id.fb_login);
        //  login facebook
        //  login phone
        pn_login = findViewById(R.id.pn_login);
        //  login phone
    }

    private void initListener() {
        //  login facebook
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        String token = loginResult.getAccessToken().getToken();
                        Log.d(TAG, "handleFacebookAccessToken:" + token);
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
        fb_login.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        });
        //  login facebook

        //  login google
        gg_login.setOnClickListener(v -> {
            Intent intent = googleSignInClient.getSignInIntent();
            ggLauncher.launch(intent);
        });
        //  login google

        pn_login.setOnClickListener(v -> {
            Intent intent = new Intent(this, VerifyOTPActivity.class);
            verifyLauncher.launch(intent);
        });
    }

    //  login facebook
    private void handleCredential(AuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            loginSuccess();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //  login facebook

    private void checkLogin() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && !user.isAnonymous()) {
            loginSuccess();
        }
    }

    private void loginSuccess() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}