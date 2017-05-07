package rain.coder.photopicker.NineGridImageView;

import android.content.Context;
import android.widget.ImageView;

import java.util.List;

/**
 * Describe :
 * Created by Rain on 17-4-26.
 */
public interface ItemImageClickListener<T> {
    void onItemImageClick(Context context, ImageView imageView, int index, List<T> list);
}
