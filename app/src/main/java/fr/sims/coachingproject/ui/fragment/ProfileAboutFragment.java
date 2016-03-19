package fr.sims.coachingproject.ui.fragment;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.sims.coachingproject.R;
import fr.sims.coachingproject.loader.local.UserLoader;
import fr.sims.coachingproject.model.UserProfile;
import fr.sims.coachingproject.ui.adapter.ProfileSportListAdapter;
import fr.sims.coachingproject.util.Const;

/**
 * Created by dfour on 14/03/2016.
 */
public class ProfileAboutFragment extends GenericFragment implements LoaderManager.LoaderCallbacks<UserProfile> {
    // TODO Put it in resources
    public static final String TITLE = "About";
    private static final String EXTRA_USER_ID = "fr.sims.coachingproject.extra.USER_ID";

    private long mUserId;

    private LinearLayout mSportsLL;
    private ProfileSportListAdapter mSportsListAdapter;
    private TextView mDescriptionTV;


    public static ProfileAboutFragment newInstance(long userId){
        ProfileAboutFragment fragment = new ProfileAboutFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void bindArguments(Bundle args) {
        super.bindArguments(args);
        mUserId = args.getLong(EXTRA_USER_ID);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile_about;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(Const.Loaders.USER_LOADER_ID, null, this);
    }

    @Override
    protected void bindView(View view) {
        super.bindView(view);
        mDescriptionTV = (TextView) view.findViewById(R.id.profile_description);
        mSportsLL = (LinearLayout) view.findViewById(R.id.profile_sports);

        mSportsListAdapter = new ProfileSportListAdapter(getActivity());
    }

    @Override
    public Loader<UserProfile> onCreateLoader(int id, Bundle args) {
        return new UserLoader(getActivity(), mUserId);
    }

    @Override
    public void onLoadFinished(Loader<UserProfile> loader, UserProfile data) {
        mDescriptionTV.setText(data.mDescription);
        mSportsListAdapter.setData(data.mSportsList);

        mSportsLL.removeAllViews();
        int length = mSportsListAdapter.getCount();
        for (int i = 0; i < length; i++) {
            mSportsLL.addView(mSportsListAdapter.getView(i, null, mSportsLL));
        }
    }

    @Override
    public void onLoaderReset(Loader<UserProfile> loader) { }
}
