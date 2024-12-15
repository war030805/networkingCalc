public abstract class ExtendedMath {
    public static double log(double value,double base) {
        return Math.log(value)/Math.log(base);
    }
    public static double log(long value,long base) {
        return Math.log(value)/Math.log(base);
    }
    public static int roundUnder(double value) {
        if (((int) value)!=value) {
            value=Math.ceil(value);
        }
        return (int) value;
    }
}
