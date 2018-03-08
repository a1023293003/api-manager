package cn.bluesking.api.manager.util.test;

public class Test {

    public static int getNumber(Integer number) {
        try {
            throw new RuntimeException();
        } catch (Exception e) {
            number ++;
            return number;
        } finally {
            System.out.println(++number);
        }
    }
    
    public static void main(String[] args) {
        Integer number = new Integer(100);
        System.out.println(getNumber(number));
        System.out.println(number);
    }
}
