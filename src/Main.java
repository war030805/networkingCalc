import java.math.BigInteger;
import java.nio.Buffer;

public class Main {
    public static void main(String[] args) {

        VSLM vslm=new VSLM(new IPadress("198.168.1.0/24"),new int[]{58,28,12},new String[]{"A","B","C"});

        vslm.getInfo("A",0);
//        vslm.getSeeHostsPerNetwerk();
//        vslm.geefAllesVanSubnet(2);
    }
    public static String bigintergerToByte(BigInteger bigInteger) {
        BigInteger rest=BigInteger.ONE;
        StringBuilder builder=new StringBuilder();
        do {
            BigInteger[] temp=bigInteger.divideAndRemainder(BigInteger.TWO);
            rest=temp[1].abs();
            builder.append(rest);
            bigInteger=temp[0];
        } while (!bigInteger.equals(BigInteger.ZERO));
        return builder.reverse().toString();
    }
}