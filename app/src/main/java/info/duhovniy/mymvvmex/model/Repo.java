package info.duhovniy.mymvvmex.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Repo implements Parcelable {

    public long id;
    public String name;
    public String description;
    public User owner;


    public Repo() {
    }

    protected Repo(Parcel in) {
        id = in.readLong();
        name = in.readString();
        description = in.readString();
        owner = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Repo> CREATOR = new Creator<Repo>() {
        @Override
        public Repo createFromParcel(Parcel in) {
            return new Repo(in);
        }

        @Override
        public Repo[] newArray(int size) {
            return new Repo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeParcelable(owner, i);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Repo repo = (Repo) o;

        if (id != repo.id) return false;
        if (name != null ? !name.equals(repo.name) : repo.name != null) return false;
        if (description != null ? !description.equals(repo.description) : repo.description != null)
            return false;
        return owner != null ? owner.equals(repo.owner) : repo.owner == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        return result;
    }
}
