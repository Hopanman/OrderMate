package hopanman.android.ordermate;

import com.google.firebase.firestore.GeoPoint;

public class Address {
    private String addressName;
    private GeoPoint addressCdn;

    public Address(String addressName, GeoPoint addressCdn) {
        this.addressName = addressName;
        this.addressCdn = addressCdn;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public GeoPoint getAddressCdn() {
        return addressCdn;
    }

    public void setAddressCdn(GeoPoint addressCdn) {
        this.addressCdn = addressCdn;
    }
}
