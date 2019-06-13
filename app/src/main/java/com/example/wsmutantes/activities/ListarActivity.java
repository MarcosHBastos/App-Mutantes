package com.example.wsmutantes.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.wsmutantes.R;
import com.example.wsmutantes.helpers.CustomJSONObjectRequest;
import com.example.wsmutantes.helpers.CustomVolleyRequestQueue;
import com.example.wsmutantes.helpers.MutanteUtils;
import com.example.wsmutantes.model.Mutante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListarActivity extends ListActivity implements Response.Listener,
        Response.ErrorListener {

    private String REQUEST_TAG = "MutantList";
    private ListView list;
    private EditText filter;
    private List<Mutante> mutanteList;
    private List<String> mutanteNameList;
    private RequestQueue mQueue;
    private final String url = "localhost:3000/mutants";
    private JSONObject j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        mutanteNameList = new ArrayList<>();
        list = findViewById(android.R.id.list);
        filter = findViewById(R.id.filter);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mutanteNameList);
        list.setAdapter(adapter);

        Intent intent = getIntent();
        int opt = intent.getExtras().getInt("option");
        switch (opt) {
            case 1:
                j = new JSONObject();
                findViewById(R.id.listBtn).setVisibility(View.GONE);
                findViewById(R.id.filter).setVisibility(View.GONE);
                queueStarter();
                /* Trecho para teste sem conexão, necessário comentar 50 e 51
                mutanteList = MutanteUtils.getTestList();
                for(Mutante m : mutanteList) {
                    mutanteNameList.add(m.getNome());
                }
                ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mutanteNameList);
                list.setAdapter(adapter);
                break;*/
            case 2:
                filter = findViewById(R.id.filter);
                filter.setVisibility(View.VISIBLE);
                findViewById(R.id.listBtn);
                break;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l,v,position, id);
        Intent it = new Intent(this, CadastrarActivity.class);
        it.putExtra("mutante", mutanteList.get(position));
        it.putExtra("option", 2);
        startActivity(it);
    }

    public void buscar(View view) {
        if ("".equals(filter)) {
            Toast.makeText(ListarActivity.this, "Preencha o filtro!",
                    Toast.LENGTH_LONG).show();
        } else {
            try {
                j.put("filter", filter.getText().toString());
            } catch (JSONException e) {
                Log.e("List", "Falha no put do filtro");
            }
            queueStarter();
        }
    }

    public void queueStarter() {

        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();
        final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(Request.Method.GET,
                url, j, this, this);
        jsonRequest.setTag(REQUEST_TAG);
        mQueue.add(jsonRequest);
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
        MutanteUtils.popAlertDialog("Erro de comunicação com o servidor: " + error.getMessage(),
                ListarActivity.this);
    }

    @Override
    public void onResponse(Object response) {
        if (response != null) {
            mutanteList = MutanteUtils.parseJSONMutanteList((JSONArray) response);
        } else {
            Toast.makeText(ListarActivity.this, "Não existe nenhum mutante com o filtro de habilidade indicado!",
                    Toast.LENGTH_LONG).show();
        }
        ArrayAdapter adapter = (ArrayAdapter) list.getAdapter();
        for(Mutante m : mutanteList) {
            adapter.add(m.getNome());
        }
    }
}
