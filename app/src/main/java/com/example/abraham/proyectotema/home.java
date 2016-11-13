package com.example.abraham.proyectotema;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abraham.proyectotema.com.example.abraham.proyectotema.model.appElement;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class home extends AppCompatActivity implements View.OnClickListener , View.OnTouchListener{

    //App definition
    SharedPreferences ps;
    CountDownTimer timer;
    TextClock clock, ampm;

    ImageButton btnWifi;
    boolean boolWifi;
    WifiManager managerWifi;

    ImageButton btnBT;
    boolean boolBT;
    BluetoothAdapter managerBT;

    ImageButton btnGPS;

    ImageButton btnConfig;

    SeekBar seekBright;
    Float brightness;

    LocationManager lm;

    ImageButton btnMusic,btnMaps,btnPhone,btnVoice;
    String pkgMusic,pkgMaps,pkgPhone;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LinearLayout home = (LinearLayout) findViewById(R.id.home);
        home.setOnTouchListener(this);
        //##########################################Shared preferences##############################
        ps = getSharedPreferences("CONFIGS", Context.MODE_PRIVATE);
        if(ps.getString("config","#")=="#"){
            Intent i = new Intent(home.this, config.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        //##########################################Navigation bar code#############################
        timer = new CountDownTimer(25000, 1000) {
            public void onTick(long millisUntilFinished) {

            }
            public void onFinish() {
                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        };
        timer.start();

        //##############################################App code#################################

        clock = (TextClock) findViewById(R.id.clock);
        ampm = (TextClock) findViewById(R.id.ampm);

        btnWifi = (ImageButton) findViewById(R.id.btnWifi);
        btnWifi.setOnClickListener(this);
        managerWifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        btnBT = (ImageButton) findViewById(R.id.btnBT);
        btnBT.setOnClickListener(this);
        managerBT = BluetoothAdapter.getDefaultAdapter();

        btnGPS = (ImageButton) findViewById(R.id.btnGPS);
        btnGPS.setOnClickListener(this);

        btnConfig = (ImageButton) findViewById(R.id.btnConfig);
        btnConfig.setOnClickListener(this);

        seekBright = (SeekBar) findViewById(R.id.bright);
        if(getIntent().getFloatExtra("brightness",-1)!=-1){
            brightness=getIntent().getFloatExtra("brightness",-1);
        }else{
            brightness=0.5f;
        }
        seekBright.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.screenBrightness = seekBright.getProgress()/100f;
                brightness=lp.screenBrightness;
                getWindow().setAttributes(lp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        lm = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        btnVoice = (ImageButton) findViewById(R.id.btnVoice);
        btnVoice.setOnClickListener(this);
        btnMusic = (ImageButton) findViewById(R.id.btnMusic);
        btnMusic.setOnClickListener(this);
        btnMaps = (ImageButton) findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(this);
        btnPhone = (ImageButton) findViewById(R.id.btnPhone);
        btnPhone.setOnClickListener(this);

        setConfigs();
        //#######################################Shared preferences ###############################
        String musicPackage = ps.getString("music","#");
        if(!musicPackage.equals("#")){
            pkgMusic = musicPackage;
        }
        String mapsPackage = ps.getString("maps","#");
        if(!mapsPackage.equals("#")){
            pkgMaps = mapsPackage;
        }
        String phonePackage = ps.getString("phone","#");
        if(!phonePackage.equals("#")){
            pkgPhone = phonePackage;
        }
    }

    @Override
    protected void onResume() {

        setConfigs();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean toggleBtn(ImageButton btn, boolean bool, int img) {
        if (bool) {
            btn.setImageResource(img);
            btn.setColorFilter(Color.argb(255, 120, 183, 191));
        } else {
            btn.setImageResource(img);
            btn.setColorFilter(Color.argb(255, 255, 255, 255));
        }
        return !bool;
    }

    public boolean toggleBtn(ImageButton btn, boolean bool) {
        if (bool) {
            btn.setColorFilter(Color.argb(255, 120, 183, 191));
        } else {
            btn.setColorFilter(Color.argb(255, 255, 255, 255));
        }
        return !bool;
    }

    private void setConfigs() {
        boolWifi = managerWifi.isWifiEnabled();
        if (boolWifi) {
            toggleBtn(btnWifi, !boolWifi, R.drawable.wifi_on);
        } else {
            toggleBtn(btnWifi, !boolWifi, R.drawable.wifi_off);
        }
        boolBT = managerBT.isEnabled();
        if (boolBT) {
            toggleBtn(btnBT, !boolBT, R.drawable.bluetooth_on);
        } else {
            toggleBtn(btnBT, !boolBT, R.drawable.bluetooth_of);
        }
        if ( lm.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            toggleBtn(btnGPS,false);
        }else{
            toggleBtn(btnGPS,true);
        }
        seekBright.setProgress((int)(brightness*100f));
    }

    @Override
    public void onClick(View view) {
        ImageButton btn = (ImageButton) view;
        switch (view.getId()) {
            case R.id.btnWifi:
                if (boolWifi) {
                    boolWifi = toggleBtn(btn, boolWifi, R.drawable.wifi_off);
                    managerWifi.setWifiEnabled(false);
                } else {
                    boolWifi = toggleBtn(btn, boolWifi, R.drawable.wifi_on);
                    managerWifi.setWifiEnabled(true);
                }
                break;
            case R.id.btnBT:
                if (boolBT) {
                    boolBT = toggleBtn(btn, boolBT, R.drawable.bluetooth_of);
                    managerBT.disable();
                } else {
                    boolBT = toggleBtn(btn, boolBT, R.drawable.bluetooth_on);
                    managerBT.enable();
                }
                break;
            case R.id.btnGPS:
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                break;
            case R.id.btnConfig:
                Intent i = new Intent(home.this, config.class);
                i.putExtra("brightness",brightness);
                startActivity(i);
                break;
            case R.id.btnMusic:
                openApp(this,pkgMusic);
                break;
            case R.id.btnMaps:
                openApp(this,pkgMaps);
                break;
            case R.id.btnPhone:
                Intent intentPhone = new Intent(Intent.ACTION_CALL_BUTTON);
                startActivity(intentPhone);
                break;
            case R.id.btnVoice:
                startVoiceRecognitionActivity();
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        timer.cancel();
        timer.start();
        return false;
    }

    public static boolean openApp(Context context, String packageName) {
        PackageManager manager = context.getPackageManager();
        Intent i = manager.getLaunchIntentForPackage(packageName);
        if (i == null) {
            return false;
            //throw new PackageManager.NameNotFoundException();
        }
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(i);
        return true;
    }
//############################### Voice commands #################################################
    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Dí un comando");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // matches is the result of voice input. It is a list of what the
            // user possibly said.
            manageVoiceResults(matches);
        }else{
            Toast.makeText(this,"Lo siento ocurrió un error",Toast.LENGTH_SHORT).show();
        }
    }
    public static void replace(ArrayList<String> strings)
    {
        ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext())
        {
            iterator.set(iterator.next().toLowerCase());
        }
    }
    private void manageVoiceResults(ArrayList<String> matches){
        replace(matches);
        if (matches.contains("configuration")||matches.contains("configuracion")) {
            Intent i = new Intent(home.this, config.class);
            i.putExtra("brightness",brightness);
            startActivity(i);
        }else if (matches.contains("call")||matches.contains("llamar")||matches.contains("llama")){
            Intent intentPhone = new Intent(Intent.ACTION_CALL_BUTTON);
            startActivity(intentPhone);
        }else if(matches.contains("music")||matches.contains("musica")||matches.contains("escuchar")){
            openApp(this,pkgMusic);
        }
        else if(matches.contains("maps")||matches.contains("mapas")||matches.contains("viajar")){
            openApp(this,pkgMaps);
        }
        else if(matches.contains("llama a casa")||matches.contains("llamar a casa")){

            Uri number = Uri.parse("tel:"+ps.getString("home","#"));
            Intent callIntent = new Intent(Intent.ACTION_CALL, number);
            callIntent.setData(Uri.parse("tel:" +number));
            try {
                startActivity(callIntent);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            boolean notFound=false;
            for(String match : matches){
                match = match.replace(" ", "");
                if(checkNumber(match.toCharArray())){
                    Uri number = Uri.parse("tel:"+match);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                    break;
                }else{
                    notFound= true;
                }
            }
            if(notFound){
                Toast.makeText(this,"Lo siento, no reconozco el comando",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkNumber(char [] str){
        for(char c : str){
            if(!PhoneNumberUtils.isReallyDialable(c)){
                return false;
            }
        }
        return true;
    }
}
