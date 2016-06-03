package uk.co.probablyfine.bytemonkey.testfiles;

public class NullabilityTestPojo {

    private String name;

    public NullabilityTestPojo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName1stArg(String name) {
        this.name = name;
    }

    public void setName2ndArg(int i, String name) {
        this.name = name;
    }

    public void setNameNoArgs() {
        this.name = "zap";
    }

    public void setNamePrimitiveArgs(int i, int i2) {
        this.name = "zoom";
    }
}
