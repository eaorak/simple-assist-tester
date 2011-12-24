package us.elron.sassist.tester;

public class TestObject {

    private final int    var1;
    private final String var2;

    public TestObject(int var1, String var2) {
        this.var1 = var1;
        this.var2 = var2;
    }

    @Override
    public String toString() {
        return "TestObject [var1=" + this.var1 + ", var2=" + this.var2 + "]";
    }

}
