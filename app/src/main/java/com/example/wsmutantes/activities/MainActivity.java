package com.example.wsmutantes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
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
    }

    public void login(View view){
        Intent intent = new Intent(MainActivity.this, LandingActivity.class);
        startActivity(intent);
        finish();

        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                .getRequestQueue();

        final String url = "http://10.0.2.2:3000/users/login";

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

        if ("".equals(usr.getText().toString()) || "".equals(pwd.getText().toString())) {
            Toast.makeText(MainActivity.this, "Campo(s) em branco!", Toast.LENGTH_LONG).show();
        } else {
            mQueue.add(jsonRequest);
        }

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

        MutanteUtils.popAlertDialog("Credenciais inválidas",
                MainActivity.this);

        String body;
        if(error.networkResponse.data!=null) {
            try {
                body = new String(error.networkResponse.data,"UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResponse(Object response) {

        try {
            String result = ((JSONObject) response).getString("message");

            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, LandingActivity.class);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Erro ao autenticar", Toast.LENGTH_LONG).show();
            Log.e("Auth", "Erro ao autenticar");
        }
    }
}
