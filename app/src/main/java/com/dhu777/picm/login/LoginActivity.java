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
import com.dhu777.picm.mock.Injection;

public class LoginActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = (Button)findViewById(R.id.sign_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usin = usernameEditText.getText().toString();
                String psin = passwordEditText.getText().toString();

                Boolean res = (Boolean) Injection.provideLoginRepositrory().login(usin,psin);

                if (res){
                    Log.d("loginButton", "onClick: login success");
                    Toast.makeText(LoginActivity.this,"loginin", Toast.LENGTH_SHORT).show();
                }else{
                    Log.d("loginButton", "onClick: login failed");
                    Toast.makeText(LoginActivity.this,"failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
