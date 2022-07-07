package com.exampl.wechat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.exampl.weChat.R;

public class Dialog_Contact extends AppCompatDialogFragment {
    private EditText contact;
    private Dialog_contactListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle("Register Your Contact")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String contactnum = contact.getText().toString();
                        listener.applyText(contactnum);//passing the contact number to the another activity that use this
                    }
                });
        contact = view.findViewById(R.id.etPhone);
        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (Dialog_contactListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement Dialog Listener");
        }

    }

    public interface Dialog_contactListener {
        void applyText(String Contact);

    }


}
