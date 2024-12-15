public class IPadress {
    private int address;
    public static final int ADDRESS_LENGTH = 4;
    public static final int PART_MAX = 255;
    private IPadress subnetMask;

    public IPadress(int address) {
        this.address = address;
    }

    public IPadress(String address) {

        if (address.contains("/")) {
            String[] split= address.split("/");
            address = split[0];

            this.subnetMask=makeSubnetMaskFromPrefix(Integer.parseInt(split[1]));
        }
        String[] split = address.split("\\.");
        for (String s : split) {
            this.address <<= bitsPerPLace();
            int value = Integer.parseInt(s);
            this.address |= value;
        }
    }
    public IPadress(String address, String subnetMask) {
        this(address);
        this.subnetMask=new IPadress(subnetMask);
    }
    @Override
    public String toString() {
        return toIpAdress(address);
    }

    public static int bitsPerPLace() {
        return Integer.bitCount(PART_MAX);
    }

    /**
     * Dit gaat eerst een 1 verplaatsen naar de eerste plaats dus dan krijg je 10000000000000000000000000000000
     * dan verplaats je de 1 naar de plek van het prefix en na de 1 blijven 1nen achter dus dan krijg je een getal met
     * allemaal nummers
     * @param prefix 
     * @return 
     */
    public static IPadress makeSubnetMaskFromPrefix(int prefix) {
        int subnetMask=(1<<((ADDRESS_LENGTH *bitsPerPLace())-1))>>prefix-1;
        return new IPadress(subnetMask);
    }
    public static String toIpAdress(int address) {
        StringBuilder str = new StringBuilder();
        for (int i = ADDRESS_LENGTH -1; i > -1; i--) {
            if (i!= ADDRESS_LENGTH -1) {
                str.append(".");
            }
            int mask=(255<<(i*bitsPerPLace()));
            int value= ((address&mask)>>(i*bitsPerPLace()))&255;
            str.append(value);
        }
        return str.toString();
    }
    public IPadress getNetwerk() {
        return new IPadress(address & subnetMask.address);
    }
    public IPadress getHostBits() {
        return new IPadress(address &(~subnetMask.address));
    }

    public void setSubnetMask(IPadress subnetMask) {
        this.subnetMask = subnetMask;
    }

    public static int getPossibilities(int prefix) {
        int possibilities=(bitsPerPLace()* ADDRESS_LENGTH)-prefix;
        return  (int) Math.pow(2,possibilities)-2;
    }
    public int getPossibilities() {
        if (subnetMask==null) {return -1;}
        int bits1SubnetMask= getPrefix();

        return getPossibilities(bits1SubnetMask);
    }
    public int getPrefix() {
        return Integer.bitCount(subnetMask.address);
    }
    public static int getSubNetMaskBits(int aantalHost) {
        double bits=ExtendedMath.log(aantalHost+2,2);
        return 32-ExtendedMath.roundUnder(bits);
    }
    public IPadress getSubnetMask() {
        return subnetMask;
    }

    public int getAddress() {
        return address;
    }

}
