package com.example.wsmutantes.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.wsmutantes.helpers.CustomJSONObjectRequest;
import com.example.wsmutantes.helpers.CustomVolleyRequestQueue;
import com.example.wsmutantes.model.Mutante;
import com.example.wsmutantes.helpers.MutanteUtils;
import com.example.wsmutantes.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CadastrarActivity extends AppCompatActivity implements Response.Listener,
        Response.ErrorListener {

    private int IMAGE_REQUEST_CODE;
    private String REQUEST_TAG = "MutantCrud";
    private List<String> hab_list;
    private ListView list;
    private Mutante mutante;
    private ImageView imgView;
    private EditText nomeView;
    private RequestQueue mQueue;
    private String message;
    private String url;
    private int requestmethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);


        imgView = findViewById(R.id.imgPreview);
        nomeView = findViewById(R.id.nome_mut);
        list = findViewById(R.id.list);

        Intent intent = getIntent();
        int opt = intent.getExtras().getInt("option");
        switch (opt) {
            case 1: //new
                mutante = new Mutante();
                hab_list = new ArrayList<>();
                findViewById(R.id.delBtn).setVisibility(View.GONE);
                message = "cadastra";
                url = "localhost:3000/mutants";
                requestmethod = Request.Method.POST;
                break;
            case 2: //alt
                mutante = (Mutante) intent.getExtras().getSerializable("mutante");
                imgView.setImageBitmap(mutante.getImgbm());
                findViewById(R.id.delBtn).setVisibility(View.VISIBLE);
                Button btn = findViewById(R.id.savBtn);
                btn.setText("Alterar");
                hab_list = mutante.getSkills();
                nomeView.setText(mutante.getNome());
                message = "altera";
                url = "localhost:3000/mutants/1";
                requestmethod = Request.Method.PATCH;
                break;
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, hab_list);
        list.setAdapter(adapter);
    }

    public void submit(View view) {
        EditText n = findViewById(R.id.nome_mut);
        String nome = n.getText().toString();

        if ("".equals(nome)) {
            Toast.makeText(CadastrarActivity.this, "Preencha o nome!",
                    Toast.LENGTH_SHORT).show();
        } else if (hab_list.size() < 1) {
            Toast.makeText(CadastrarActivity.this, "Informe pelo menos uma habilidade!",
                    Toast.LENGTH_SHORT).show();
        } else if (imgView.getDrawable() == null || "".equals(mutante.getImage())) {
            Toast.makeText(CadastrarActivity.this, "Forneça uma imagem!",
                    Toast.LENGTH_SHORT).show();
        } else {
            mutante.setSkills(hab_list);
            mutante.setNome(nome);
            queueStarter(mutante);
        }
    }

    public void delete(View view) {
        url = "localhost:3000/mutants/1";
        message = "exclui";
        requestmethod = Request.Method.DELETE;
        queueStarter(mutante);
    }

    public void getImageFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Escolha uma imagem"), IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String encodedImage = "";
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            final Uri imageUri = data.getData();
            final InputStream imageStream;
            if (imageUri != null) {
                try {
                    imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    mutante.setImgbm(selectedImage);
                    encodedImage = MutanteUtils.encodeImage(selectedImage);
                    imgView.setImageURI(imageUri);
                    mutante.setImage(encodedImage);
                } catch (FileNotFoundException e) {
                    Log.e("Img", "Problema ao converter imagem informada");
                }
            }
        }
    }

    public void addHabil(View view) {
        ArrayAdapter adapter = (ArrayAdapter) list.getAdapter();
        if (list.getAdapter().getCount() < 3) {
            EditText hab = findViewById(R.id.hab_mut);
            String h = hab.getText().toString().trim();
            h = h.substring(0, 1).toUpperCase() + h.substring(1).toLowerCase();
            if ("".equals(h)) {
                Toast.makeText(CadastrarActivity.this, "Habilidade não pode estar em branco!",
                        Toast.LENGTH_SHORT).show();
            } else if (hab_list.contains(h)) {
                Toast.makeText(CadastrarActivity.this, "Habilidade já informada!",
                        Toast.LENGTH_SHORT).show();
            } else {
                adapter.add(h);
            }
        } else {
            Toast.makeText(CadastrarActivity.this, "Máximo de Habilidades: 3", Toast.LENGTH_SHORT).show();
        }
    }

    public void delHabil(View view) {
        ArrayAdapter adapter = (ArrayAdapter) list.getAdapter();
        if (list.getAdapter().getCount() > 0) {
            adapter.remove(list.getAdapter().getItem(0));
        }
    }

    public void queueStarter(Mutante m) {
        JSONObject jsonMutante = MutanteUtils.buildJSONMutant(m);

        mQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();
        final CustomJSONObjectRequest jsonRequest = new CustomJSONObjectRequest(requestmethod,
                url, jsonMutante, this, this);
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
                CadastrarActivity.this);
    }

    @Override
    public void onResponse(Object response) {
        try {
            String result = ((JSONObject) response).getString("message");
            if ("200".equals(result)) {
                Toast.makeText(CadastrarActivity.this, "Mutante " + message + "do com sucesso!",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CadastrarActivity.this, LandingActivity.class);
                startActivity(intent);
                finish();
            } else if ("500".equals(result)) {
                Toast.makeText(CadastrarActivity.this, "Já existe um mutante com esse nome na base de dados!",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CadastrarActivity.this, "Falha ao " + message + "r mutante",
                        Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Log.e("Save", "Erro de parse no result");
        }
    }
}