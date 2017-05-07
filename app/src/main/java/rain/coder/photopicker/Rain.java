package rain.coder.photopicker;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/5/3 0003.
 */
public class Rain extends Application{

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        PhotoPick.init(getApplicationContext());
    }
}
