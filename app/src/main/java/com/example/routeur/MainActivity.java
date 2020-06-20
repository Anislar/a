package com.example.routeur;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.provider.FontsContractCompat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CameraView cameraView ;
    boolean isDetect = false;
    Button btn;
    FirebaseVisionBarcodeDetectorOptions options;
    FirebaseVisionBarcodeDetector detector;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Dexter.withActivity(this).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                settingCam();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                 Toast.makeText(MainActivity.this,"you must accecpt Permission", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

            }
        }).check();
    }
    private void settingCam(){

        btn=(Button)findViewById(R.id.btn_start);
        btn.setEnabled(isDetect);
btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
isDetect=!isDetect;
    }
});
cameraView = (CameraView)findViewById(R.id.cameraView);
cameraView.setLifecycleOwner(this);
cameraView.addFrameProcessor(new FrameProcessor() {
    @Override
    public void process(@NonNull Frame frame) {
        processImg(getVisionImageFromFrame(frame));
    }
});
    }

//options= new  FirebaseVisionBarcodeDetectorOptions.Builder().setBarcodeFormats(FirebaseVisionBarcode.F)

    private void processImg(FirebaseVisionImage image) {
        if(!isDetect)
        {

            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                @Override
                public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        processRes(firebaseVisionBarcodes);
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,""+e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    private void processRes(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {

        if (firebaseVisionBarcodes.size()>0)
        {

            isDetect=true;btn.setEnabled(isDetect);
            for(FirebaseVisionBarcode item: firebaseVisionBarcodes){

                int value =item.getValueType();
                switch (value) {
                    case FirebaseVisionBarcode.TYPE_TEXT: {
                        createDialog(item.getRawValue());

                    }
break;
                    case FirebaseVisionBarcode.TYPE_CONTACT_INFO:
                    {

                        String info = new StringBuilder("model: ").append(item.getContactInfo().getTitle()).append("\n")
                    .append("adress").append(item.getContactInfo().getAddresses()).toString();
                        createDialog(info);
                    }
                    break;
                    default: break;

                    }
            }
        }
    }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame){
        byte [] data=frame.getData();
        FirebaseVisionImageMetadata firebaseVisionImageMetadata = new FirebaseVisionImageMetadata.Builder().setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize().getHeight()).setWidth(frame.getSize().getWidth()).build();
        return FirebaseVisionImage.fromByteArray(data,firebaseVisionImageMetadata);
    }


    private void createDialog (String text){


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText titleRouteur = new EditText(MainActivity.this);
        final EditText adresseIP = new EditText(MainActivity.this);
        final EditText model = new EditText(MainActivity.this);

        final EditText adresseMac = new EditText(MainActivity.this);

        titleRouteur.setHint("donner le nom");
        adresseIP.setHint("TP-LINK Router");
        adresseMac.setHint("adresse Mac");
        adresseIP.setHint("donner l'address IP");

        builder.setView(titleRouteur);
        builder.setView(adresseIP);
        builder.setView(adresseMac);
        builder.setView(adresseIP);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String routeur= titleRouteur.getText().toString();



                if (TextUtils.isEmpty((CharSequence) titleRouteur)|| TextUtils.isEmpty((CharSequence) adresseIP) ){

                    Toast.makeText(MainActivity.this,"please you must fill all the champ...",Toast.LENGTH_SHORT).show();
                }

                else{

                    createNewGroup(routeur);

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();            }
        });

        AlertDialog dialog=builder.create();
        dialog.show();
    }
    private void createNewGroup(final String routeur) {
        reference= FirebaseDatabase.getInstance().getReference("routeur");
        reference.child(routeur).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               /* HashMap<String,String> hashMap= new HashMap<>();
                hashMap.put("id",);
                hashMap.put("title",);*/
                if(task.isSuccessful())
                {

                    Toast.makeText(MainActivity.this,routeur+" un nouveau routeur is created Successfully!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
