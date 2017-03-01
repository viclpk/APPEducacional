/*
 * Copyright (C) 2014 Akexorcist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener;

public class TerminalActivity extends Activity{
    static BluetoothSPP bt;

    TextView textStatus, textRead;
    EditText etMessage;
    Uri imageUri;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    Menu menu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        Log.i("Check", "onCreate");

        textRead = (TextView) findViewById(R.id.textRead);
        textStatus = (TextView) findViewById(R.id.textStatus);
        etMessage = (EditText) findViewById(R.id.etMessage);

       // Bundle b = getIntent().getExtras();
      //  long value = b.getLong("startTime", 0);

        System.out.println("Voltou para a terminal");

        bt = new BluetoothSPP(this);




        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                textRead.append(message + "\n");
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                textStatus.setText("Status : Not connect");
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_connection, menu);
            }

            public void onDeviceConnectionFailed() {
                textStatus.setText("Status : Connection failed");
            }

            public void onDeviceConnected(String name, String address) {

                /*Intent intent = null;
                intent = new Intent(getApplicationContext(), CameraActivity2.class);
                startActivity(intent);*/
                textStatus.setText("Status : Connected to " + name);
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_disconnection, menu);


                Intent intent = new Intent(getApplicationContext(),CameraActivity2.class);
                startActivityForResult(intent,1);
                /*Intent intent = null;
                intent = new Intent(getApplicationContext(), CameraActivity2.class);
                startActivity(intent);*/


            }
        });

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_android_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_ANDROID);
			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if (id == R.id.menu_device_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if (id == R.id.menu_disconnect) {
            if (bt.getServiceState() == BluetoothState.STATE_CONNECTED)
                bt.disconnect();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                setup();
            }
        }
        System.out.println("Veio no onStart");
      /*  String ren = getIntent().getStringExtra("methodName");
            if (ren.equals("myMethod"))
            {
                Reiniciar();
            }*/
            //The key argument here must match that used in the other activity
    }
    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null)
            setIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String value = extras.getString("methodName");
            if(value.equals("myMethod"));
            {
                Reiniciar();
            }
            //The key argument here must match that used in the other activity
        }
    }
   /* @Override
    public void onResume(){
        System.out.println("Veio no onResumo");
        super.onResume();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String value = extras.getString("methodName");
            if(value.equals("myMethod"));
            {
                Reiniciar();
            }
            //The key argument here must match that used in the other activity
        }

    }*/
    public void Reiniciar() {

        Intent intent = new Intent(getApplicationContext(),CameraActivity2.class);
        startActivityForResult(intent,1);
    }

    public void setup() {

    }

    public static void setup2() {


        bt.send("5,1,2000", true);
       // Intent intent = new Intent(getApplicationContext(),CameraActivity2.class);
       // startActivityForResult(intent,1);
    }
    public void setup4() {


        bt.send("0,1,2000", true);

        Intent intent = new Intent(getApplicationContext(),CameraActivity2.class);
        startActivityForResult(intent,1);
    }
    public void setup5() {


        bt.send("1,1,2000", true);

        Intent intent = new Intent(getApplicationContext(),CameraActivity2.class);
        startActivityForResult(intent,1);
    }
    public void setup6() {

        bt.send("2,1,2000", true);

        Intent intent = new Intent(getApplicationContext(),CameraActivity2.class);
        startActivityForResult(intent,1);
    }
    public static void setup7() {

        bt.send("4,1,2000", true);

    }
    public static void setup8() {

        bt.send("3,1,2000", true);

    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        }

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){

                String ResultadoObjeto=data.getStringExtra("result");
               // String Movimentar=data.getStringExtra("movimentacao");
                System.out.println("Veio no result");


              /*  if(ResultadoObjeto.equals("ObjetoIdentificado"))
                {
                    if (Movimentar.equals("1"));
                    {
                        setup4();
                    }
                    if (Movimentar.equals("2"));
                    {
                        setup5();
                    }
                    if (Movimentar.equals("3"));
                    {
                        setup6();
                    }
                    System.out.println("Entrou no OBJETOIDENTIFICADO");

                }*/
                if(ResultadoObjeto.equals("Reiniciar"))
                {
                    Intent intent = new Intent(getApplicationContext(),CameraActivity2.class);
                    startActivityForResult(intent,1);
                }
                if(ResultadoObjeto.equals("ObjetoIdentificado"))
                {
                    setup2();
                }
                if(ResultadoObjeto.equals("ObjetoNaoIdentificado"))
                {
                    System.out.println("Entrou no OBJETO NAO IDENTIFICADO");
                    setup2();
                }
                if(ResultadoObjeto.equals("FotoFrente"))
                {
                    setup4();
                }
                if(ResultadoObjeto.equals("FotoEsquerda"))
                {
                    setup5();
                }
                if(ResultadoObjeto.equals("FotoDireita"))
                {
                    setup6();
                }

               /* else
                {
                    System.out.println("Entrou EM NENHUM");
                    Intent intent = new Intent(getApplicationContext(),CameraActivity2.class);
                    startActivityForResult(intent,1);
                }*/
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
                setup();
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}