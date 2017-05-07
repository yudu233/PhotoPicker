package rain.coder.photopicker.show;

/**
 * × Descriptions : 预览的监听器
 * Created by Rain on 16-5-11.
 */
public interface OnPreviewListener {
    public void onPreview(int pos, boolean showDelete);
    public void onPick();
}
