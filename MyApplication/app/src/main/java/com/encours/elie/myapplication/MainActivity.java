package com.encours.elie.myapplication;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;




import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;


public class MainActivity extends AppCompatActivity  implements ColorPickerDialogListener {
    private static final int DIALOG_ID = 0;

    private FloatingActionButton addPhoto;
    private int currentColor = Color.BLACK;
    private boolean editMode = false;
    DrawableView drawableView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.pick_color:
                         editMode=false;
                        pickColor();
                    preventDrawing();
                    return true;
                case R.id.edit_photo:
                    editMode = true;
                    DrawOnImage();


                return true;
                case R.id.navigation_notifications:
                    editMode=false;
                    preventDrawing();


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
        addPhoto = (FloatingActionButton) findViewById(R.id.addPhoto);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoto();
            }
        });

        drawableView = (DrawableView) findViewById(R.id.paintView);


    }


    private void addPhoto(){

        //start activity to take a picture here
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
        if(editMode){
            DrawableViewConfig config = new DrawableViewConfig();
            config.setStrokeColor(currentColor);
            config.setShowCanvasBounds(true); // If the view is bigger than canvas, with this the user will see the bounds (Recommended)
            config.setStrokeWidth(20.0f);
            config.setMinZoom(1.0f);
            config.setMaxZoom(3.0f);
            config.setCanvasHeight(1080);
            config.setCanvasWidth(1920);
            drawableView.setConfig(config);
        }
    }

    private void preventDrawing()
    {
        if(!editMode)
            drawableView.setConfig(null);

    }

}
