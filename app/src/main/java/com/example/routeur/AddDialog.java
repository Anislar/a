package com.example.routeur;

import android.app.AlertDialog;
import android.app.AppComponentFactory;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddDialog extends AppCompatDialogFragment   {
EditText txtTitle;
private dialogListenner listenner;
EditText txtIp;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=  new AlertDialog.Builder(getActivity()) ;
        builder.setTitle("ajouter Information");
        LayoutInflater inflater = getActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog,null);
            builder.setView(view).setTitle("add").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String title= txtTitle.getText().toString();
                    listenner.applyTexts(title);
                }
            });
        txtTitle= view.findViewById(R.id.edit_title);

            return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listenner= (dialogListenner) context;
        }
        catch (Exception e){ e.getMessage();}
    }

    public  interface  dialogListenner{
void applyTexts(String title);


    }
}
