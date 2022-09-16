package com.kendohamster.instructionModel;

public class Instruction {
    private final int stringId;
    private final int imageId;

    public Instruction(int stringId, int imageId) {
        this.stringId = stringId;
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public int getStringId() {
        return stringId;
    }
}
