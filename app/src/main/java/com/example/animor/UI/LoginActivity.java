package com.example.animor.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.animor.R;

public class LoginActivity extends AppCompatActivity {
EditText email;
EditText password;
TextView rememberPassword;
TextView enterWithoutUser;
Button enter;
Button registry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        enterWithoutUser = findViewById(R.id.textEnterWithoutUser);
        enter = findViewById(R.id.buttonEntrar);
        enter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                handleLogin();
            }
        });
        registry = findViewById(R.id.buttonRegistro);
        registry.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View v){
                startActivity(new Intent(LoginActivity.this, RegistryActivity.class));
            }
        });
        rememberPassword = findViewById(R.id.textForgotPassword);
        rememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(LoginActivity.this, RestorePasswordActivity.class));
            }
        });
    }
    private void handleLogin() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }else{
//            if(){
//
//            }else{
//                startActivity(new Intent(LoginActivity.this, InicioActivity.class));
//            }
        }

        // Aquí iría la lógica de autenticación
        // Por ahora solo mostramos un mensaje
        Toast.makeText(this, "Iniciando sesión...", Toast.LENGTH_SHORT).show();

        // Ejemplo de redirección después de login exitoso
        // Intent intent = new Intent(this, MainActivity.class);
        // startActivity(intent);
        // finish();
    }
}