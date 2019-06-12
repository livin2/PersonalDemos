package com.dhu777.picm.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dhu777.picm.R;
import com.dhu777.picm.data.LoginDataSource;
import com.dhu777.picm.data.entity.UserToken;
import com.dhu777.picm.mock.Injection;

public class LoginActivity extends AppCompatActivity {
    static final String MSG_LOGIN_SUC = "登录成功";
    static final String MSG_LOGIN_FAIL = "登录失败";
    static final String MSG_SIGNUP_SUC = "注册成功";
    static final String MSG_SIGNUP_FAIL = "注册失败";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = (Button) findViewById(R.id.sign_in_button);
        final Button signUpButton = (Button) findViewById(R.id.sign_up_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usin = usernameEditText.getText().toString();
                String psin = passwordEditText.getText().toString();

                Injection.provideLoginRepositrory(LoginActivity.this)
                        .login(usin, psin, new LoginDataSource.LoginCallback() {
                            @Override
                            public void onSuccess(UserToken Token) {
                                Log.d("loginButton", "onClick: login success");
                                Toast.makeText(LoginActivity.this, MSG_LOGIN_SUC, Toast.LENGTH_SHORT).show();
                                LoginActivity.this.finish();
                            }

                            @Override
                            public void onFail(Throwable e) {
                                Log.d("loginButton", "onClick: login failed");
                                Toast.makeText(LoginActivity.this, MSG_LOGIN_FAIL, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usin = usernameEditText.getText().toString();
                String psin = passwordEditText.getText().toString();

                Injection.provideLoginRepositrory(LoginActivity.this)
                        .signUp(usin, psin, new LoginDataSource.LoginCallback() {
                            @Override
                            public void onSuccess(UserToken Token) {
                                Log.d("signUpButton", "onClick: signup success");
                                Toast.makeText(LoginActivity.this, MSG_SIGNUP_SUC, Toast.LENGTH_SHORT).show();
                                LoginActivity.this.finish();
                            }

                            @Override
                            public void onFail(Throwable e) {
                                Log.d("signUpButton", "onClick: signup failed");
                                Toast.makeText(LoginActivity.this, MSG_SIGNUP_FAIL, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
