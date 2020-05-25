package com.example.monumentsclassification;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Bitmap photo;
    TextView details;
    AlertDialog.Builder builder;
    com.example.monumentsclassification.ImageClassifier imageClassifier;
    final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //senor orientation  bitmap

        imageView = (ImageView) this.findViewById(R.id.myimage);
        Button photoButton = (Button) this.findViewById(R.id.button);
        details=this.findViewById(R.id.info);

//        photoButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, CAMERA_REQUEST);
//            }
//        });
        builder = new AlertDialog.Builder(this);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (options[item].equals("Take Photo")) {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);

                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto , 1);

                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK) {
                    photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    imageView.setImageBitmap(photo);
                    classifyImage(photo);
                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageView.setImageURI(selectedImage);
                    try {
                        photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    classifyImage(photo);
                }
                break;
        }
    }

    protected void classifyImage(Bitmap pic)
    {
        try {
            imageClassifier = new com.example.monumentsclassification.ImageClassifier(this,Classifier.Device.CPU,1);

        } catch (IOException e) {
            e.printStackTrace();
        }
//        Bitmap Icon = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        List<Classifier.Recognition> answer = imageClassifier.getResults(pic);
        Toast.makeText(this, "Answer="+answer.get(0).getConfidence()+" "+answer.get(0).getTitle(), Toast.LENGTH_LONG).show();
        details.setText("Answer= confidence : "+answer.get(0).getConfidence()+" monument : "+answer.get(0).getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
