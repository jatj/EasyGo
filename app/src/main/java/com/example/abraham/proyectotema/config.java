package com.example.abraham.proyectotema;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class config extends AppCompatActivity implements View.OnClickListener{
    String music,maps,home;
    Button btnMusic, btnMaps, btnSave, btnHome;
    TextView txtMusic, txtMaps, txtHome;
    SharedPreferences ps;
    Float brightness;

    private static final int REQUEST_CONTACT_NUMBER = 1234;

    /** Pops the "select phone number" window */
    public void onBrowseForNumbersButtonClicked() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //Si el usuario no ha autorizado los permisos, pedir que los dé
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.CALL_PHONE},1);
        }else{
            //Si el usuario ya autorizó, hace el intent
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(contactPickerIntent, REQUEST_CONTACT_NUMBER);
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if(data != null && requestCode == REQUEST_CONTACT_NUMBER) {
                Uri uriOfPhoneNumberRecord = data.getData();
                String idOfPhoneRecord = uriOfPhoneNumberRecord.getLastPathSegment();
                Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone._ID + "=?", new String[]{idOfPhoneRecord}, null);
                if(cursor != null) {
                    if(cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        String formattedPhoneNumber = cursor.getString( cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) );
                        Log.d("TestActivity", String.format("The selected phone number is: %s", formattedPhoneNumber));
                        SharedPreferences.Editor editor = ps.edit();
                        editor.putString("home",formattedPhoneNumber);
                        editor.commit();
                        txtHome.setText("Casa: "+ps.getString("home","#").replace(" ",""));
                        btnHome.setText("Cambiar");
                    }
                    cursor.close();
                }
            }
            else {
                Log.w("TestActivity", "WARNING: Corrupted request response");
            }
        }
        else if (resultCode == RESULT_CANCELED) {
            Log.i("TestActivity", "Popup canceled by user.");
        }
        else {
            Log.w("TestActivity", "WARNING: Unknown resultCode");
        }
    }
    //Permissions code for android 6+
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED&& grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED||
                            ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED||
                            ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                        startActivityForResult(contactPickerIntent, REQUEST_CONTACT_NUMBER);
                    } else {
                        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.CALL_PHONE},1);
                    }
                } else {
                        Toast.makeText(this, "No se autorizó el acceso.", Toast.LENGTH_LONG).show();
                }
                break;

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getIntent().getFloatExtra("brightness",-1)!=-1){
            brightness=getIntent().getFloatExtra("brightness",-1);
        }else{
            brightness=0.5f;
        }
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;
        getWindow().setAttributes(lp);

        txtMaps = (TextView) findViewById(R.id.txtSelectMaps);
        btnMaps = (Button) findViewById(R.id.btnSlctMaps);
        btnMaps.setOnClickListener(this);

        txtMusic = (TextView) findViewById(R.id.txtSelectMusic);
        btnMusic = (Button) findViewById(R.id.btnSlctMusic);
        btnMusic.setOnClickListener(this);

        txtHome = (TextView) findViewById(R.id.txtSelectHouse);
        btnHome = (Button) findViewById(R.id.btnSlctHouse);
        btnHome.setOnClickListener(this);

        btnSave = (Button) findViewById(R.id.btnSavePreferences);
        btnSave.setOnClickListener(this);

        ps = getSharedPreferences("CONFIGS", Context.MODE_PRIVATE);
        music = ps.getString("music","#");
        maps = ps.getString("maps","#");
        home = ps.getString("home","#");
        if(!music.equals("#")){
            txtMusic.setText("Música: "+ps.getString("musicName","#"));
            btnMusic.setText("Cambiar");
        }else{
            txtMusic.setText("Música: No asignado");
            btnMusic.setText("Asignar");
        }
        if(!maps.equals("#")){
            txtMaps.setText("Mapas: "+ps.getString("mapsName","#"));
            btnMaps.setText("Cambiar");
        }else {
            txtMaps.setText("Mapas: No asignado");
            btnMaps.setText("Asignar");
        }
        if(!home.equals("#")){
            txtHome.setText("Casa: "+ps.getString("home","#").replace(" ",""));
            btnHome.setText("Cambiar");
        }else {
            txtHome.setText("Casa: No asignado");
            btnHome.setText("Asignar");
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnSlctMusic:
                Intent iMusic = new Intent(config.this, apps.class);
                iMusic.putExtra("category","music");
                iMusic.putExtra("name","música");
                iMusic.putExtra("brightness",brightness);
                startActivity(iMusic);
                break;
            case R.id.btnSlctMaps:
                Intent iMaps = new Intent(config.this, apps.class);
                iMaps.putExtra("category","maps");
                iMaps.putExtra("name","mapas");
                iMaps.putExtra("brightness",brightness);
                startActivity(iMaps);
                break;
            case R.id.btnSlctHouse:
                onBrowseForNumbersButtonClicked();
                break;
            case R.id.btnSavePreferences:
                music = ps.getString("music","#");
                maps = ps.getString("maps","#");
                home = ps.getString("home","#");
                if(!music.equals("#")&&!maps.equals("#")&&!home.equals("#")){
                    SharedPreferences.Editor editor = ps.edit();
                    editor.putString("config","saved");
                    editor.commit();
                    Intent intent = new Intent(config.this,home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("brightness",brightness);
                    startActivity(intent);
                }else{
                    Toast.makeText(this,"No has asignado todos los campos",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
