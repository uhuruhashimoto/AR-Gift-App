package com.rayhc.giftly;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;

import java.util.Calendar;

import kotlin.random.Random;

public class DialogFragment extends androidx.fragment.app.DialogFragment {
    private static final String DIALOG_KEY = "DIALOG_KEY";

    public static DialogFragment newFragment(int id){
        DialogFragment fragment = new DialogFragment();
        Bundle localBundle = new Bundle();
        localBundle.putInt(DIALOG_KEY, id);

        fragment.setArguments(localBundle);
        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);

        Dialog dialog = null;
        Bundle bundle = getArguments();
        int id = bundle.getInt(DIALOG_KEY);

        Activity locActivity = getActivity();

        EditText text = new EditText(locActivity);

        switch (id) {
            case R.id.add_friend:
                // for adding friends
                return new AlertDialog.Builder(getActivity()).setTitle(R.string.add_friend_dialog).setView(text).setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FriendsFragment.setAddedFriend(text.getText().toString());
                    }
                }).setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        text.setText("");
                    }
                }).create();

            default:

        }

        return dialog;
    }

}
