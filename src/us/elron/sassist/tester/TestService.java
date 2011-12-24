package us.elron.sassist.tester;

public class TestService implements ITestService {

    @Override
    public String testMethod(int param1, String param2, TestObject param3) {
        System.out.println("Test method has been called..");
        return "I'm the return value";
    }

}
