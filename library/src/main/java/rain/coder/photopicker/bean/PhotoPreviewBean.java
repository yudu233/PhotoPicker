package rain.coder.photopicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class PhotoPreviewBean implements Parcelable {

    private int position;
    private ArrayList<Photo> photos;
    private ArrayList<String> selectPhotos;
    private int maxPickSize;
    private boolean originalPicture;//是否选择的是原图

    public PhotoPreviewBean(){}

    private PhotoPreviewBean(Parcel in) {
        position = in.readInt();
        photos = in.createTypedArrayList(Photo.CREATOR);
        selectPhotos = in.createStringArrayList();
        maxPickSize = in.readInt();
        originalPicture = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeTypedList(photos);
        dest.writeStringList(selectPhotos);
        dest.writeInt(maxPickSize);
        dest.writeByte((byte) (originalPicture ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoPreviewBean> CREATOR = new Creator<PhotoPreviewBean>() {
        @Override
        public PhotoPreviewBean createFromParcel(Parcel in) {
            return new PhotoPreviewBean(in);
        }

        @Override
        public PhotoPreviewBean[] newArray(int size) {
            return new PhotoPreviewBean[size];
        }
    };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public ArrayList<String> getSelectPhotos() {
        return selectPhotos;
    }

    public void setSelectPhotos(ArrayList<String> selectPhotos) {
        this.selectPhotos = selectPhotos;
    }

    public int getMaxPickSize() {
        return maxPickSize;
    }

    public void setMaxPickSize(int maxPickSize) {
        this.maxPickSize = maxPickSize;
    }

    public boolean isOriginalPicture() {
        return originalPicture;
    }

    public void setOriginalPicture(boolean originalPicture) {
        this.originalPicture = originalPicture;
    }
}
