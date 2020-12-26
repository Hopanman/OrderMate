package hopanman.android.ordermate;

import android.app.Application;

import timber.log.Timber;

public class OrderMateApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
