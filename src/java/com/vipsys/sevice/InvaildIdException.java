package com.vipsys.sevice;

public class InvaildIdException extends RuntimeException {
    int gotID;
    int expectID;

    public InvaildIdException(String message, int gotID, int expectID) {
        super(message);
        this.gotID = gotID;
        this.expectID = expectID;
    }

    public int getGotID() {
        return gotID;
    }

    public int getExpectID() {
        return expectID;
    }
}
