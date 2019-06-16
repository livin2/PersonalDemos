package com.dhu777.picm.picdetail;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.dhu777.picm.R;
import com.dhu777.picm.data.entity.PicInfo;
import com.dhu777.picm.util.ComUtil;

import java.text.DateFormat;
import java.util.Date;

public class InfoDialogFragment extends DialogFragment {
    private PicInfo picInfo;

    public InfoDialogFragment(@NonNull PicInfo picInfo) {
        this.picInfo = picInfo;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_pic_detail,null);

        TextView textDate = view.findViewById(R.id.text_info_date);
        String dateT = DateFormat.getDateInstance().format(picInfo.getPicLastModify());
        textDate.append(dateT);

        TextView textUser = view.findViewById(R.id.text_info_username);
        textUser.append(picInfo.getUserName().toString());

        TextView textSize = view.findViewById(R.id.text_info_size);
        textSize.append(picInfo.getPicSize().toString()+" KB");

        TextView textWidth = view.findViewById(R.id.text_info_width);
        textWidth.append(picInfo.getWidth().toString());

        TextView textHeight = view.findViewById(R.id.text_info_height);
        textHeight.append(picInfo.getHeight().toString());

        builder.setView(view)
                .setTitle(R.string.title_info_detail)
                .setNegativeButton(R.string.button_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InfoDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
