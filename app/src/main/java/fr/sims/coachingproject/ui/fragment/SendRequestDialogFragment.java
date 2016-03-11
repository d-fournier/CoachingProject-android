package fr.sims.coachingproject.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.model.Sport;

/**
 * Created by dfour on 10/03/2016.
 */
public class SendRequestDialogFragment extends DialogFragment implements DialogInterface.OnClickListener{

    private static final String EXTRA_SPORTS_LIST = "fr.sims.coachingproject.extra.SPORTS_LIST";
    private static final String EXTRA_INDEX = "fr.sims.coachingproject.extra.INDEX";

    private ArrayList<Sport> mSportsList;
    private int mIndex;

    // TODO Check if parcelable list is OK
    public static SendRequestDialogFragment newInstance(ArrayList<Sport> sportList){
        SendRequestDialogFragment fragment = new SendRequestDialogFragment();
        Bundle args = new Bundle();
//        args.putParcelableArrayList("EXTRA_SPORTS_LIST", sportList);
        fragment.setArguments(args);
        return fragment;
    }

    public static SendRequestDialogFragment newInstance(ArrayList<Sport> sportList, int index){
        SendRequestDialogFragment fragment = new SendRequestDialogFragment();
        Bundle args = new Bundle();
//        args.putParcelableArrayList(EXTRA_SPORTS_LIST, sportList);
        args.putInt(EXTRA_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
//            mSportsList = args.getParcelableArrayList(EXTRA_SPORTS_LIST);
            mIndex = args.getInt(EXTRA_INDEX, -1);
        }
    }

   /*
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_signin, null))
                .setPositiveButton(R.string.send_request, this)
                .setNegativeButton(R.string.cancel_request, this);

        builder.
        return builder.create();
    }
    */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
//            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SendRequestDialogListener");
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int id) {
        SendRequestDialogFragment.this.getDialog().cancel();
    }

}
