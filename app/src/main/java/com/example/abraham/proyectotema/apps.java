package com.example.abraham.proyectotema;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abraham.proyectotema.com.example.abraham.proyectotema.model.appElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class apps extends AppCompatActivity{
    List<appElement> elementos,displayElems;
    ArrayAdapter<appElement> adapter;
    EditText txtSearch;
    Float brightness;
    ListView lv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Selecciona tu app de "+getIntent().getStringExtra("name"));

        if(getIntent().getFloatExtra("brightness",-1)!=-1){
            brightness=getIntent().getFloatExtra("brightness",-1);
        }else{
            brightness=0.5f;
        }
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;
        getWindow().setAttributes(lp);

        //Se define el ListView
        lv = (ListView) findViewById(R.id.appsLV);
        //Se define la lista de objetos de la clase appElement
        elementos = new ArrayList<appElement>();
        getApps ga = new getApps();
        ga.execute(new String[]{""});

        txtSearch = (EditText) findViewById(R.id.txtSearch);
        final SharedPreferences ps = getSharedPreferences("CONFIGS", Context.MODE_PRIVATE);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences.Editor editor = ps.edit();
                String packageName = ((appElement)displayElems.get(i)).getPackageName();
                String name = ((appElement)displayElems.get(i)).getName();
                editor.putString(getIntent().getStringExtra("category"),packageName);
                editor.putString(getIntent().getStringExtra("category")+"Name",name);
                editor.commit();
                Intent intent = new Intent(apps.this,config.class);
                intent.putExtra("brightness",brightness);
                startActivity(intent);
            }
        });
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               displayElems = filterApps();
                //Se crea el adapter con la clase CustomAdapter creada abajo y se le añade al listView
                adapter = new CustomAdapter(apps.this,R.layout.apps_info, displayElems);
                lv.setAdapter(adapter);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }
    public List<appElement> filterApps(){
        List<appElement> filtered = new ArrayList<appElement>();
        String filter = txtSearch.getText().toString().toLowerCase();
        for(appElement elem : elementos){
            if(elem.getName().toLowerCase().contains(filter)){
                filtered.add(elem);
            }
        }
        return filtered;
    }

    //Se crea la clase interna del custom adapter para añadirle el layout prediseñado
    class CustomAdapter extends ArrayAdapter {
        //Constructor solicita un contexto, un layout y los elementos
        public CustomAdapter(Context context, int resource, List<appElement> objects) {
            super(context, resource, objects);
        }
        //Se override el metodo getView
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Se define el item
            View item = convertView;

            if(item == null){
                //Se le da el layout personalizado al item
                item = getLayoutInflater().inflate(R.layout.apps_info, null);
            }
            //Se obtienen los views del custom layout
            ImageView img = (ImageView) item.findViewById(R.id.imgApp);
            TextView name = (TextView) item.findViewById(R.id.txtName);
            //Se obtiene el objeto pizzaElement
            appElement elemento = (appElement) getItem(position);
            //Se cambia el contenido de los views con el del objeto pizzaElement
            name.setText(elemento.getName());
            img.setBackground(elemento.getImg());

            return item;
        }
    }
    public List<appElement> getInstalledApps (){
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<appElement> elements = new ArrayList<appElement>();
        for(ApplicationInfo packageInfo : packages){
            if(pm.getLaunchIntentForPackage(packageInfo.packageName) != null){
                Drawable icon = packageInfo.loadIcon(pm);
                elements.add(new appElement(pm.getApplicationLabel(packageInfo).toString(),packageInfo.packageName,icon));
            }
        }
        return elements;
    }

    private class getApps extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog = new ProgressDialog(apps.this);

        protected void onPreExecute(){
            dialog.setMessage("Leyendo datos de las aplicaciones instaladas");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... x) {
            elementos = getInstalledApps();
            return true;
        }

        protected void onPostExecute(Boolean result){
            if(result){
                //Se crea el adapter con la clase CustomAdapter creada abajo y se le añade al listView
                displayElems = elementos;
                adapter = new CustomAdapter(apps.this,R.layout.apps_info, displayElems);
                lv.setAdapter(adapter);
            }
            dialog.dismiss();
        }
    }
}
