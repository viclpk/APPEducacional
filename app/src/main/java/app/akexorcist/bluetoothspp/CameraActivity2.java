package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class CameraActivity2 extends Activity {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyBRGAC17A6USlDSoIhuwu6mElFt19htSx4";
    public static final String FILE_NAME = "temp.jpg";

    private static final String TAG = CameraActivity2.class.getSimpleName();
    private static final int GALLERY_IMAGE_REQUEST = 1;
    public static final int CAMERA_PERMISSIONS_REQUEST = 2;
    public static final int CAMERA_IMAGE_REQUEST = 3;

    @SuppressWarnings("deprecation")
    Preview preview;
    Camera camera;
    Activity act;
    Context ctx;
    public static int rot = 0;
    public static int mov = 0;
    public static int ident =0;


    private Handler mHandler = new Handler();
    private Handler mHandler2 = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        act = this;


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera2);



        preview = new Preview(this, (SurfaceView) findViewById(R.id.surfaceView));
        preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ((FrameLayout) findViewById(R.id.layout)).addView(preview);
        preview.setKeepScreenOn(true);

      /*  new Thread(new Runnable() {
            public void run(){
                while (true) {
                    Log.d(TAG, "Tirando foto automaticamente...");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            camera.takePicture(shutterCallback, rawCallback, jpegCallback);

                        }
                    });

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }
        }).start();*/
      /*  if (mov==3 & rot==3)
        {
            mov =0;
            rot =0;
        }*/

        Toast.makeText(ctx, "teste", Toast.LENGTH_LONG).show();
        if (rot != 3) {


            mHandler2.postDelayed(new Runnable() {
                public void run() {
                    Camera.Parameters params = camera.getParameters();
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(params);
                }
            }, 500);

            mHandler.postDelayed(new Runnable() {
                public void run() {
                    rot = rot + 1;
                    TirarFoto();
                }
            }, 4000);

        }

        /*preview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            }
        });*/

    }

    private void TirarFoto() {
        Toast.makeText(this, "Delayed Toast!", Toast.LENGTH_SHORT).show();
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int numCams = Camera.getNumberOfCameras();
        if(numCams > 0){
            try{
                camera = Camera.open(0);
                camera.startPreview();
                preview.setCamera(camera);
                Camera.Parameters params = camera.getParameters();
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

                camera.setParameters(params);

            } catch (RuntimeException ex){
                Toast.makeText(ctx, "camera nao encontrada" , Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onPause() {
        if(camera != null) {
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        super.onPause();
    }
    private void resetCam() {
        camera.startPreview();
        preview.setCamera(camera);
    }
    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //        Log.d(TAG, "onShutter'd");

        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //        Log.d(TAG, "onPictureTaken - raw");

        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {

            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/camtest");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                byte[] pictureBytes;
                Bitmap thePicture = BitmapFactory.decodeByteArray(data,0,data.length, null);
                Matrix m = new Matrix();
                m.postRotate(90);
                thePicture = Bitmap.createBitmap(thePicture, 0, 0, thePicture.getWidth(), thePicture.getHeight(),m,true);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                thePicture.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                pictureBytes = bos.toByteArray();


                FileOutputStream fs = new FileOutputStream(outFile);
                fs.write(pictureBytes);
                fs.close();

                System.out.println("Caminho : " + outFile);
                System.out.println("Nome : " + fileName);


                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());
                refreshGallery(outFile);



                System.out.println("Rotacao : "+rot);

                if (rot==1) {
                    String TipoFoto = "FotoFrente";
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", TipoFoto);
                    setResult(Activity.RESULT_OK, returnIntent);
                    uploadImage(Uri.fromFile(outFile));
                    finish();
                }
                if (rot==2) {
                    String TipoFoto = "FotoEsquerda";
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", TipoFoto);
                    setResult(Activity.RESULT_OK, returnIntent);
                    uploadImage(Uri.fromFile(outFile));
                    finish();
                }
                if (rot==3) {
                    String TipoFoto = "FotoDireita";
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", TipoFoto);
                    setResult(Activity.RESULT_OK, returnIntent);
                    uploadImage(Uri.fromFile(outFile));
                    finish();
                }



                //  uploadImage(Uri.fromFile(outFile));



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            //  return null;


            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_camera, menu);
        return true;
    }



    public void uploadImage(Uri uri) {

        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                Bitmap bitmap =
                        scaleBitmapDown(
                                MediaStore.Images.Media.getBitmap(getContentResolver(), uri),
                                1200);

                callCloudVision(bitmap);
                //mMainImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                //  Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            // Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }
    }

    private void callCloudVision(final Bitmap bitmap) throws IOException {
        // Switch text to loading
        //  mImageDetails.setText(R.string.loading_message);


        // Do the real work in an async task, because we need to use the network anyway
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new AsyncTask<Object, Void, String>() {

                @Override
                protected String doInBackground(Object... params) {
                    try {
                        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);

                        builder.setVisionRequestInitializer(new
                                VisionRequestInitializer(CLOUD_VISION_API_KEY));
                        Vision vision = builder.build();

                        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                                new BatchAnnotateImagesRequest();
                        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();


                            // Add the image
                            Image base64EncodedImage = new Image();
                            // Convert the bitmap to a JPEG
                            // Just in case it's a format that Android understands but Cloud Vision
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                            byte[] imageBytes = byteArrayOutputStream.toByteArray();

                            // Base64 encode the JPEG
                            base64EncodedImage.encodeContent(imageBytes);
                            annotateImageRequest.setImage(base64EncodedImage);

                            // add the features we want
                            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                                Feature labelDetection = new Feature();
                                labelDetection.setType("LABEL_DETECTION");
                                labelDetection.setMaxResults(20);
                                add(labelDetection);
                            }});

                            String ContentFinal = annotateImageRequest.getImage().getContent();
                            System.out.println("Content1 = "+ContentFinal);

                            // Add the list of one thing to the request
                            add(annotateImageRequest);
                        }});

                        Vision.Images.Annotate annotateRequest =
                                vision.images().annotate(batchAnnotateImagesRequest);
                        // Due to a bug: requests to Vision API containing large images fail when GZipped.
                        annotateRequest.setDisableGZipContent(true);
                        Log.d(TAG, "created Cloud Vision request object, sending request");

                        BatchAnnotateImagesResponse response = annotateRequest.execute();

                        System.out.println("Tags:" +response);
                        System.out.println("tamanho:" +response.size());

                        return convertResponseToString(response);

                    } catch (GoogleJsonResponseException e) {
                        Log.d(TAG, "failed to make API request because " + e.getContent());
                    } catch (IOException e) {
                        Log.d(TAG, "failed to make API request because of other IOException " +
                                e.getMessage());
                    }
                    return "Cloud Vision API request failed. Check logs for details.";
                }

                protected void onPostExecute(String result) {
                    //  mImageDetails.setText(result);
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        // .execute();
    }

    public Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";
        String ResultadoBusca = "ObjetoNaoIdentificado";


        mov=mov+1;
        System.out.println("Movimentacao: " +mov);
        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();




        if (labels != null) {
            for (EntityAnnotation label : labels) {

                label.getMid();
                if(label.getDescription().equals("bottle") || label.getDescription().equals("drink") || label.getDescription().equals("glass"))
                {
                    System.out.println(" Objeto Identificado");
                    ResultadoBusca = "ObjetoIdentificado";

                    if(mov==1)
                    {
                        ident = 1;
                        TerminalActivity.setup2();
                    }
                    if(mov==2)
                    {   ident = 1;
                        TerminalActivity.setup7();

                    }

                    if(mov==3)
                    {
                        ident = 1;
                        TerminalActivity.setup8();

                    }

                }

                message += String.format("%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        } else {
            message += "nothing";
        }
        System.out.println(message);
        if (ResultadoBusca.equals("ObjetoNaoIdentificado")& mov==3 & ident==0) {
            TerminalActivity.setup2();
        }
        if(mov==3)
        {

            VoltarParaTerminal();
        }


        return message;
    }



    private void VoltarParaTerminal()
    {
        System.out.println("Veio no terminal");
       /* Intent returnIntent = new Intent();
        returnIntent.putExtra("result",ResultadoBusca);
        //returnIntent.putExtra("movimentacao",String.valueOf(mov));
        setResult(Activity.RESULT_OK,returnIntent);
        finish();*/
        mov =0;
        rot =0;
        ident=0;

        Intent intent = new Intent(this, TerminalActivity.class);
      //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("methodName","myMethod");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        /*Intent intent = new Intent(this, TerminalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        */
    }

}
