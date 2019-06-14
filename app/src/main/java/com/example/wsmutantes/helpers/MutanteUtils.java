package com.example.wsmutantes.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.wsmutantes.model.Mutante;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MutanteUtils {

    public static List<Mutante> getTestList() {
        List<Mutante> mutList = new ArrayList<>();
        Mutante m1 = new Mutante();
        Mutante m2 = new Mutante();
        Mutante m3 = new Mutante();

        m1.setId(23);
        m1.setNome("Homem-Aranha");
        List<String> skills1 = new ArrayList<>();
        skills1.add("Super-força");
        skills1.add("Subir pelas paredes");
        skills1.add("Sensor aranha");
        m1.setSkills(skills1);

        m2.setId(34);
        m2.setNome("Tempestade");
        List<String> skills2 = new ArrayList<>();
        skills2.add("Vôo");
        skills2.add("Controle sobre os elementos");
        m2.setSkills(skills2);

        m3.setId(45);
        m3.setNome("Noturno");
        List<String> skills3 = new ArrayList<>();
        skills3.add("Teletransporte");
        skills3.add("Rabo");
        m3.setSkills(skills3);

        mutList.add(m1);
        mutList.add(m2);
        mutList.add(m3);

        return mutList;
    }

    public static void popAlertDialog(String message, Context ctx) {
        AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Alerta");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    public static JSONObject buildJSONMutant(Mutante mutante) {
        JSONObject mjo = new JSONObject();
        try {
            mjo.put("id", mutante.getId());
            mjo.put("name", mutante.getNome());

            JSONArray ja = new JSONArray();
            for (String s : mutante.getSkills()) {
                JSONObject jo = new JSONObject();
                jo.put("name", s);
                ja.put(jo);
            }
            mjo.put("skills", ja);
            mjo.put("image", mutante.getImage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mjo;
    }

    public static JSONArray buildMutantArray(List<Mutante> mutantes) {
        JSONArray mja = new JSONArray();
        for (Mutante m : mutantes) {
            mja.put(buildJSONMutant(m));
        }
        return mja;
    }

    public static Mutante parseJSONMutante(JSONObject jm, Context context) {
        Mutante m = new Mutante();
        try {
            m.setId(jm.getInt("id"));
            m.setNome(jm.getString("name"));

            JSONArray sa = jm.getJSONArray("skills");
            List<String> skills = new ArrayList<>();
            for (int i = 0; i < sa.length(); i++) {
                skills.add(sa.getJSONObject(i).getString("name"));
            }
            m.setSkills(skills);

            String image = jm.getString("image");
            saveImage(context, m, image);

            return m;
        } catch (JSONException e) {
            Log.e("Parse", "Erro no método de cast JSON -> Mutante");
        }
        return null;
    }

    public static List<Mutante> parseJSONMutanteList(JSONArray jma, Context context) {
        List<Mutante> mutanteList = new ArrayList<>();
        for (int i = 0; i < jma.length(); i++) {
            try {
                mutanteList.add(parseJSONMutante(jma.getJSONObject(i), context));
            } catch (Exception e) {
                Log.e("Parse", "Erro no método de cast JSONArray -> List<Mutante>");
            }
        }
        return mutanteList;
    }

    public static void saveImage(Context context, Mutante mutant, String content) {
        String filename = "file_" + mutant.getId();

        try {
            FileOutputStream outputStream;
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();

            mutant.setImageFileName(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImageBitMap(Context context, Mutante mutant) {
        String path = context.getFilesDir() + "/" + mutant.getImageFileName();

        File file = new File(path);

        int length = (int) file.length();
        byte[] bytes = new byte[length];

        try {
            FileInputStream in = null;
            in = new FileInputStream(file);
            in.read(bytes);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String contents = new String(bytes);

        Bitmap bp = null;

        if (contents != null && !contents.isEmpty()) {
            byte[] imageAsBytes = Base64.decode(contents, Base64.DEFAULT);
            bp = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);

            mutant.setImage(contents);
        }
        return bp;
    }
}
