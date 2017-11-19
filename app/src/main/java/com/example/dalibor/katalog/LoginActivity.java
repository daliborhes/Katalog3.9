package com.example.dalibor.katalog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dalibor.katalog.model.AccessToken;
import com.example.dalibor.katalog.remote.ApiService;
import com.example.dalibor.katalog.remote.ApiUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.email) AutoCompleteTextView mEmailView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.login_form) View mLoginFormView;
    @BindView(R.id.email_sign_in_button) Button mEmailSignInButton;
    @BindView(R.id.email_register_button) Button mEmailRegisterButton;
    private String email;
    private String password;
    private static final String TAG = "LoginActivity";
    private AccessToken accessToken;
    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

//        mPasswordView = (EditText) findViewById(R.id.password);
//        mEmailSignInButton = findViewById(R.id.email_sign_in_button);

//        mEmailView.setText("asad@gmail.com");
//        mPasswordView.setText("1Ss!@*");

        mEmailView.setText("kosa@gmail.com");
        mPasswordView.setText("Aa!12345");

        mEmailRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmailView.getText().toString();
                password = mPasswordView.getText().toString();
                if(!TextUtils.isEmpty(password)&&! isPasswordValid(password)) {

                    mPasswordView.setError(getString(R.string.error_invalid_password));
                }
                else if(TextUtils.isEmpty(email)) {
                    mEmailView.setError(getString(R.string.error_field_required));
                }
                else if(!isEmailValid(email)) {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                }
                else {
                    //logovanje
                    getAccessToken(email, password);

                }
            }
        });
}
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5 && password.matches(".*\\d+.*");
    }


    public void getAccessToken(String username, String password){

        String text = "grant_type=password&username="+email+"&password="+password;
        RequestBody body =
                RequestBody.create(MediaType.parse("text/plain"), text);

        apiService = ApiUtils.getApiService();
        apiService.getAccesToken(body).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                if (response.isSuccessful()){
                    accessToken = response.body();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("at", accessToken.getAccessToken());
                    editor.putString("rt", accessToken.getRefreshToken());
                    editor.commit();

                    Log.i(TAG, "post submitted to API." + response.code() + " " + response.message());
                    Toast.makeText(getApplicationContext(),"Uspesno - " + response.code() + " " + response.message(),Toast.LENGTH_SHORT ).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                else if(response.code()==400){
                    Log.e(TAG, "post not submitted to API. - " + response.code() + " " + response.message());
                    Toast.makeText(getApplicationContext(),"Neuspesno - Pogresan user ili password " + response.message(),Toast.LENGTH_SHORT ).show();
                }
                else {
                    Log.e(TAG, "post not submitted to API. - " + response.code() + " " + response.message());
                    Toast.makeText(getApplicationContext(),"Neuspesno "+ response.code() + " " + response.message(),Toast.LENGTH_SHORT ).show();
                }
            }
            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.e(TAG, "post not submitted to API: " + t.getMessage());
                Toast.makeText(getApplicationContext(),"Neuspesno - " + t.getMessage(),Toast.LENGTH_SHORT ).show();
            }
        });
    }



}


