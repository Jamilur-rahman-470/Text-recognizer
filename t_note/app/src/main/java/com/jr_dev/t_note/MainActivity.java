package com.jr_dev.t_note;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.FirebaseOptions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.FirebaseApp;


import java.util.List;

public class MainActivity extends AppCompatActivity {


    //Variable
    String sms_adr = "";
    String sms_body = "";
    Bitmap imageBitmap;

    //Widget declaration
    EditText p_num;
    TextView body;
    Button cam_btn;
    Button send_btn;
    Button detect_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Firebase App


        p_num = findViewById(R.id.p_num);
        body = findViewById(R.id.body);
        cam_btn = findViewById(R.id.cam_btn);
        send_btn = findViewById(R.id.send_btn);
        detect_btn = findViewById(R.id.detect);

    }

    public void send_sms(View view) {
        sms_adr = p_num.getText().toString();
        sms_body = body.getText().toString();

        String scAddress = null;
        PendingIntent sent = null, delivery = null;

        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(sms_adr, scAddress, sms_body, sent, delivery);
    }

    public void text_recog(View view) {
        dispatchTakePictureIntent();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
        }
    }

    private void detect_text(){

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer recog = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

        Task<FirebaseVisionText> result =
                recog.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                processTxt(firebaseVisionText);
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });


    }

    private void processTxt(FirebaseVisionText txt){
        List<FirebaseVisionText.TextBlock> blocks = txt.getTextBlocks();
        if(blocks.size() == 0){
            Toast.makeText(MainActivity.this, "Sorry no Text :P", Toast.LENGTH_LONG).show();
            return;
        }
        for (FirebaseVisionText.TextBlock block : txt.getTextBlocks()){
            String txts = block.getText();
            body.setTextSize(18);
            body.setText(txts);
        }
    }

    public void detectTXT(View view) {
        detect_text();
    }
}
