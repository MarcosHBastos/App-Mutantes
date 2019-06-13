package com.example.wsmutantes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.wsmutantes.helpers.CustomJSONObjectRequest;
import com.example.wsmutantes.helpers.CustomVolleyRequestQueue;
import com.example.wsmutantes.helpers.MutanteUtils;
import com.example.wsmutantes.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Response.Listener,
        Response.ErrorListener {
    public static final String REQUEST_TAG = "Autenticação";
    private EditText usr;
    private EditText pwd;
    private Button btn;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usr = findViewById(R.id.username);
        pwd = findViewById(R.id.password);
        btn = findViewById(R.id.login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                .getRequestQueue();
        final String url = "http:ip:3000/users/login";
        JSONObject j = new JSONObject();
        try {
            j.put("email", usr.getText().toString());
            j.put("password", pwd.getText().toString());
        } catch (JSONException e) {
            Log.e("Parse", "JSON parse Exception");
        }
        final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.POST,
                url, j, this, this);
        jsonRequest.setTag(REQUEST_TAG);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Trecho para pular a verificação de login
                Intent intent = new Intent(MainActivity.this, LandingActivity.class);
                startActivity(intent);
                finish();*/
                if ("".equals(usr.getText().toString()) || "".equals(pwd.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Campo(s) em branco!", Toast.LENGTH_LONG).show();
                } else {
                    mQueue.add(jsonRequest);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_TAG);
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        MutanteUtils.popAlertDialog("Erro ao autenticar: " + error.getMessage(),
                MainActivity.this);
    }

    @Override
    public void onResponse(Object response) {
        try {
            String result = ((JSONObject) response).getString("message");
            if ("200".equals(result)) {
                Toast.makeText(MainActivity.this, "Autenticado com sucesso!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, LandingActivity.class);
                startActivity(intent);
                finish();
            } else {
                MutanteUtils.popAlertDialog("Login ou senha incorretos",
                        MainActivity.this);
            }
        } catch (JSONException e) {
            Log.e("Auth", "Erro ao autenticar");
        }
    }
}
