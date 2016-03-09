package fr.sims.coachingproject.service.gcmService;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Donovan on 04/03/2016.
 */
public class CustomInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = CustomInstanceIDListenerService.class.getSimpleName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        RegistrationGCMIntentService.startActionUpdateGCMToken(this);
    }

}
