package fr.sims.coachingproject.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.sims.coachingproject.R;

/**
 * Created by abarbosa on 10/02/2016.
 */
public class QuestionsAnswersFragment extends GenericFragment{

    public static final String TABS_TITLE = "Questions";

    public static QuestionsAnswersFragment newInstance() {
        QuestionsAnswersFragment fragment = new QuestionsAnswersFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_questions_answers;
    }
}
