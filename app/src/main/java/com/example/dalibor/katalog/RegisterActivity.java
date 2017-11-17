package com.example.dalibor.katalog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dalibor.katalog.model.User;
import com.example.dalibor.katalog.remote.ApiService;
import com.example.dalibor.katalog.remote.ApiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.emailr)
    AutoCompleteTextView mEmailView;
    @BindView(R.id.passwordr)
    EditText mPasswordView;
    @BindView(R.id.login_formr)
    View mLoginFormView;
    @BindView(R.id.email_register_buttonr) Button mEmailRegisterButton;
    @BindView(R.id.confirmpasswordr) EditText mConfirmPassword;
    ApiService apiServiceRegister;
    private static final String TAG = "RegisterActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        apiServiceRegister = ApiUtils.getApiService();

        mEmailRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmailView.getText().toString().trim();
                String password = mPasswordView.getText().toString().trim();
                String confirmPassword = mConfirmPassword.getText().toString().trim();

                if(!TextUtils.isEmpty(password)&& !isPasswordValid(password)) {
                    mPasswordView.setError(getString(R.string.error_invalid_password));
                }
                if(TextUtils.isEmpty(email)) {
                    mEmailView.setError(getString(R.string.error_field_required));
                }
                else if(!isEmailValid(email)) {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                }
                else {
                    User user = new User (email,password,confirmPassword);
                   registerUser(user);
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

    public void registerUser (User user) {

        apiServiceRegister.registerUser(user).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.isSuccessful()) {
                    Log.i(TAG, "post submitted to API - " + response.body());
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Uspesno" + response.body(),Toast.LENGTH_SHORT ).show();
                }
                if(!response.isSuccessful()){
                    Log.e(TAG, "post not submitted to API. - " + response.code() + " " + response.message());
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(),"Neuspesno - " + response.code() + " " + response.message(),Toast.LENGTH_SHORT ).show();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"Neuspesno - " + t.getMessage(),Toast.LENGTH_SHORT ).show();
            }
        });
    }
}
