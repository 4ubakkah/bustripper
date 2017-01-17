package io.telenor.bustripper;

public enum LocationType {
    STOP("Stop"),
    AREA("Area"),
    POI("POI");


    private String value;

    LocationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }

}
