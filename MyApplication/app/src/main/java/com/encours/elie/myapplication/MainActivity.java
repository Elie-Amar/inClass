package com.encours.elie.myapplication;


import android.graphics.Color;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.content.FileProvider;

import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


import com.encours.elie.myapplication.Helper.Helper_NavigationBottomBar;
import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;


public class MainActivity extends AppCompatActivity  implements ColorPickerDialogListener {
    private static final int DIALOG_ID = 2;
    private int currentColor = Color.BLACK;
    private boolean editMode = false;
    DrawableView drawableView;
      private ImageView thePhoto;
    String mCurrentPhotoPath;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int RESULT_LOAD_IMAGE = 1;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.pick_color:
                    pickColor();
                    return true;

                case R.id.edit_photo:
                    DrawOnImage();
                    return true;

                case R.id.galleryPhoto:
                    showPhoto();
                    return true;

                case R.id.addAPhoto:
                    addPhoto();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




        drawableView = (DrawableView) findViewById(R.id.paintView);


        Helper_NavigationBottomBar helper = new Helper_NavigationBottomBar();
        helper.disableShiftMode(navigation);

    }


    private void addPhoto(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void showPhoto(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK ) {
            try {
                InputStream IS = getApplicationContext().getContentResolver().openInputStream(data.getData());
                Bitmap b = BitmapFactory.decodeStream(IS);
                thePhoto = (ImageView) findViewById(R.id.photoTaken);
                int width= b.getWidth()/2;
                int height= b.getHeight()/2;
                b = getResizedBitmap(b,width,height);
                thePhoto.setImageBitmap(b);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //galleryAddPic();

            //setPic();
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK ) {
            try {
                InputStream IS = getApplicationContext().getContentResolver().openInputStream(data.getData());
                Bitmap b = BitmapFactory.decodeStream(IS);
                thePhoto = (ImageView) findViewById(R.id.photoTaken);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int width=dm.widthPixels;
                int height=dm.heightPixels;
                b = getResizedBitmap(b,width,height);
                thePhoto.setImageBitmap(b);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = thePhoto.getWidth();
        int targetH = thePhoto.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        thePhoto = (ImageView) findViewById(R.id.photoTaken);
        thePhoto.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setCurrentColor(int Color){
        this.currentColor = Color;
    }

    private void pickColor(){
       // ColorPickerDialog.newBuilder().create();
       // ColorPickerDialog.newBuilder().setColor(currentColor).show(MainActivity.this);
        ColorPickerDialog.newBuilder()
                .setDialogType(ColorPickerDialog.TYPE_CUSTOM)
                .setAllowPresets(false)
                .setDialogId(DIALOG_ID)
                .setColor(currentColor)
                .setShowAlphaSlider(true)
                .show(this);

    }

    @Override public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case DIALOG_ID:
                setCurrentColor(color);
                break;
        }
    }

    @Override public void onDialogDismissed(int dialogId) {

    }

    private void DrawOnImage(){
        //current color

            drawableView.setVisibility(View.VISIBLE);
            DrawableViewConfig config = new DrawableViewConfig();
            config.setStrokeColor(currentColor);
            config.setShowCanvasBounds(true); // If the view is bigger than canvas, with this the user will see the bounds (Recommended)
            config.setStrokeWidth(20.0f);
            config.setMinZoom(1.0f);
            config.setMaxZoom(3.0f);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width=dm.widthPixels;
            int height=dm.heightPixels;
            config.setCanvasHeight(height);
            config.setCanvasWidth(width);
            drawableView.setConfig(config);

    }

    private void preventDrawing()
    {
        if(!editMode)
            drawableView.setVisibility(View.INVISIBLE);

    }

}
