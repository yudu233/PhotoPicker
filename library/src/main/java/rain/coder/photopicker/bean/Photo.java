package rain.coder.photopicker.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe :全部照片
 * Created by Rain on 17-4-28.
 */
public class Photo implements Parcelable {

    private int id;
    private String path;
    private long size;//byte 字节

    public Photo(int id, String path) {
        this.id = id;
        this.path = path;
    }

    public Photo(int id, String path, long size) {
        this.id = id;
        this.path = path;
        this.size = size;
    }

    public Photo() {
    }

    private Photo(Parcel in) {
        id = in.readInt();
        path = in.readString();
        size = in.readLong();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;

        Photo photo = (Photo) o;

        return id == photo.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(path);
        dest.writeLong(size);
    }
}
