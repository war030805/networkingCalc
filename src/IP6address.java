import java.math.BigInteger;
import java.util.TreeMap;

public class IP6address {

    private long firstBits, lastBits;

    public static final int ADDRESS_LENGTH = 8;
    public static final int PART_MAX = 0xFFFF;
    public static final int ADDRESS_BITS = 128;
    private IP6address subnetMask;

    public IP6address(String adress) {
        StringBuilder builder  = new StringBuilder(adress);

        if (adress.contains("/")) {
            String[] split =adress.split("/");
            adress = split[0];
            if (!split[1].equals("sm")) {
                setSubnetMask(Integer.parseInt(split[1]));
            }
        } else {
            setSubnetMask(64);
        }


        if (adress.equals("::")) {
            adress="0:";
            adress=new StringBuilder(adress.repeat(ADDRESS_LENGTH)).delete((ADDRESS_LENGTH *2)-1, ADDRESS_LENGTH *2).toString();
        }
        if (adress.contains("::")) {
            int index=builder.indexOf("::");
            builder=new StringBuilder(adress).delete(index,index+1);
            //gaat het dubble eruithalen want dan weet je hoeveel stukken er zijn omdat je dan kan splitsen op de :
            int parts=builder.toString().split(":").length;
            int partsMissing= ADDRESS_LENGTH -parts;
            index++;
            for (int i=0;i<(partsMissing*2);i+=2) {
                builder.insert(index+i,"0:");
            }
            adress=builder.toString();
        }
        String[] split =adress.split(":");
        for (int i = 0; i < 4; i++) {
            String s = split[i];
            this.firstBits <<= bitsPerPLace();
            long value = Long.parseLong(s,16);
            this.firstBits |= value;
        }
        for (int i = 4; i < 8; i++) {
            String s = split[i];
            this.lastBits <<= bitsPerPLace();
            long value = Long.parseLong(s,16);
            this.lastBits |= value;
        }
    }
    public IP6address(BigInteger bigInteger) {
        BigInteger firstBits=bigInteger.shiftRight(64);
        BigInteger lastBits=bigInteger.and(new BigInteger("ffffffffffffffff",16));
        this.firstBits=Long.parseLong(firstBits.toString());
        this.lastBits=Long.parseLong(lastBits.toString());
    }
    public IP6address(IP6address ip6Address) {
        this.firstBits = ip6Address.firstBits;
        this.lastBits = ip6Address.lastBits;
        if (ip6Address.subnetMask != null) {
            this.subnetMask=new IP6address(ip6Address.subnetMask);
        }
    }

    public static int bitsPerPLace() {
        return Integer.bitCount(PART_MAX);
    }
    public static String toIp6AdressUncomPressed(long firstBits,long lastBits) {
        return String.format("%s:%s",toStringAdress(firstBits),toStringAdress(lastBits));
    }
    public static String toIp6AdressUncomPressed(IP6address ip6Address) {
        return toIp6AdressUncomPressed(ip6Address.firstBits, ip6Address.lastBits);
    }
    public static String toIp6Adress(long firstBits,long lastBits) {
        String adress=toIp6AdressUncomPressed(firstBits,lastBits);
        if (adress.contentEquals(new StringBuilder(adress.repeat(ADDRESS_LENGTH)).delete((ADDRESS_LENGTH *2)-1, ADDRESS_LENGTH *2))) {
            return "::";
        } else if (adress.contains("0:0")) {
            int startPlaceAfterEachOther=0, lastPlaceAfterEachOther;
            TreeMap<Integer,Integer[]> plaatsenMap=new TreeMap<>();
            boolean foundZero=false;
            for (int i = 0; i < adress.length(); i++) {
                char c=adress.charAt(i);
                if (c!='0') {
                    if (foundZero) {
                        foundZero=false;
                        lastPlaceAfterEachOther=i-1;
                        plaatsenMap.put(lastPlaceAfterEachOther-startPlaceAfterEachOther,new Integer[]{startPlaceAfterEachOther,lastPlaceAfterEachOther});
                    }
                    i=skipToNextPart(i,adress);
                } else if (!foundZero){
                    foundZero=true;
                    startPlaceAfterEachOther=i;
                    i++;
                } else {
                    i++;
                }
            }

            if (foundZero) {
                lastPlaceAfterEachOther=adress.length();
                plaatsenMap.put(lastPlaceAfterEachOther-startPlaceAfterEachOther,new Integer[]{startPlaceAfterEachOther,lastPlaceAfterEachOther});
            }
            Integer[] places=plaatsenMap.lastEntry().getValue();
            startPlaceAfterEachOther=places[0];
            lastPlaceAfterEachOther=places[1];
            int addressLength=adress.length();
            adress=new StringBuilder(adress).delete(startPlaceAfterEachOther,lastPlaceAfterEachOther).toString();
            if (startPlaceAfterEachOther==0) {
                adress=":"+adress;
            }
            if (lastPlaceAfterEachOther==addressLength) {
                adress+=":";
            }

        }
        return adress;
    }
    private static int skipToNextPart(int i, String adress) {
        char c;
        do {
            i++;
            if (i==adress.length()) {
                return i;
            }
            c=adress.charAt(i);
        } while (c!=':' );
        return i;
    }
    private static String toStringAdress(long bits) {
        StringBuilder str = new StringBuilder();
        for (int i = 3; i > -1; i--) {
            if (i!=3) {
                str.append(":");
            }
            long mask=((long) PART_MAX <<(i*bitsPerPLace()));
            long waarde= ((bits&mask)>>(i*bitsPerPLace()))&PART_MAX;
            str.append(Long.toHexString(waarde));
        }
        return str.toString();
    }


    @Override
    public String toString() {
        return toIp6Adress(firstBits,lastBits);
    }
    public void setSubnetMask(int subnetMask) {
        StringBuilder firstBitsB = new StringBuilder(), lastBitsB=new StringBuilder();

        for (int i = 0; i < ADDRESS_BITS; i++) {
            if (i<63) {
                addBits(firstBitsB,i,subnetMask);
            } else if (i>64) {
                addBits(lastBitsB,i,subnetMask);
            }
        }
        long firstBits=Long.parseLong(firstBitsB.toString(),2);
        long lastBits=Long.parseLong(lastBitsB.toString(),2);
        this.subnetMask=new IP6address(toIp6AdressUncomPressed(firstBits,lastBits)+"/sm");
    }
    private void addBits(StringBuilder builder, int i, int subnetMask) {
        if (i<subnetMask) {
            builder.append("1");
        } else {
            builder.append("0");
        }
    }
    public IP6address getNetwerk() {
        BigInteger address=addressAsBigInteger();
        BigInteger subMask=subnetMask.addressAsBigInteger();
        BigInteger netWork=address.and(subMask);
        return new IP6address(netWork);
    }
    public IP6address getHostBits() {
        BigInteger adress=addressAsBigInteger();
        BigInteger subMask=subnetMask.addressAsBigInteger();
        BigInteger host=adress.and(subMask.not());
        return new IP6address(host);
    }
    public BigInteger addressAsBigInteger() {
        BigInteger firstBits=new BigInteger(Long.toString(this.firstBits));
        System.out.println("firstBits: "+Main.bigintergerToByte(firstBits));
        BigInteger bigInteger=firstBits.shiftLeft(64);
        System.out.println("shift: "+ Main.bigintergerToByte(bigInteger));
        BigInteger lastBits=new BigInteger(Long.toString(this.lastBits));
        System.out.println(this);
        System.out.println(bigInteger.or(lastBits));
        System.out.println(Main.bigintergerToByte(bigInteger.or(lastBits)));
        return bigInteger.or(lastBits);
    }
    public BigInteger getMogelijkhedenThis() {
        if (subnetMask == null) {
            return new BigInteger("-1");
        }
        return getMogelijkheden(geefPrefixInt());
    }
    public static BigInteger getMogelijkheden(int prefix) {
        int mogelijkheden = (ADDRESS_BITS) - prefix;
        return (new BigInteger("2").pow(mogelijkheden)).subtract(BigInteger.valueOf(2));
    }
    public IP6address getSubnetMask() {
        return subnetMask;
    }
    public int geefPrefixInt() {
        return Long.bitCount(subnetMask.firstBits) + Long.bitCount(subnetMask.lastBits);
    }

    public static String toIpAdress(BigInteger address) {
        return new IP6address(address).toString();
    }
    public static int geefSubNetMaskBits(long aantalHost) {
        double bits=ExtendedMath.log(aantalHost+2,2);
        return ADDRESS_BITS -ExtendedMath.roundUnder(bits);
    }

}
