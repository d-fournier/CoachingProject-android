package fr.sims.coachingproject.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.ui.view.MarkDownView;

/**
 * Created by dfour on 14/03/2016.
 */
public class BlogPreviewDialogFragment extends DialogFragment {

    private static final String EXTRA_PREVIEW_TEXT = "fr.sims.coachingproject.extra.PREVIEW_TEXT";
    public static final String TAG = BlogPreviewDialogFragment.class.getSimpleName();

    private String mText;

    public static BlogPreviewDialogFragment newInstance(String text){
        BlogPreviewDialogFragment fragment = new BlogPreviewDialogFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_PREVIEW_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            mText = args.getString(EXTRA_PREVIEW_TEXT);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater i = LayoutInflater.from(getActivity());
        View view = i.inflate(R.layout.fragment_dialog_blog_preview, null);

        ((MarkDownView) view.findViewById(R.id.post_preview)).setMarkdown(mText);

        builder.setTitle(R.string.post_preview)
                .setView(view)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
        return builder.create();
    }

}
