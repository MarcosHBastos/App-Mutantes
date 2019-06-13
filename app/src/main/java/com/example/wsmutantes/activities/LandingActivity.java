package com.example.wsmutantes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.wsmutantes.R;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
    }

    public void novo(View view) {
        Intent intent = new Intent(this, CadastrarActivity.class);
        intent.putExtra("option", 1);
        startActivity(intent);
    }

    public void listar(View view) {
        Intent intent = new Intent(this, ListarActivity.class);
        intent.putExtra("option", 1);
        startActivity(intent);
    }

    public void procurar(View view) {
        Intent intent = new Intent(this, ListarActivity.class);
        intent.putExtra("option", 2);
        startActivity(intent);
    }

    public void sair(View view) {
        //this.finishAffinity();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
