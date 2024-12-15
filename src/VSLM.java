import java.util.*;

public class VSLM {
    private final IPadress origanalAdress;
    private final TreeMap<Integer,ArrayList<String>> sortHosts;
    private final ArrayList<Integer> sortedAantallen;

    public VSLM(IPadress origanalAdress,int [] aantalHosts, String[] hostnamen) {
        this.origanalAdress = origanalAdress;
        sortHosts= new TreeMap<>();


        for (int i = 0; i < aantalHosts.length; i++) {
            ArrayList<String> arrayList=new ArrayList<>();
            int aantallen=aantalHosts[i];
            if (sortHosts.containsKey(aantallen)) {
                arrayList=sortHosts.get(aantallen);
            }
            arrayList.add(hostnamen[i]);
            sortHosts.put(aantallen, arrayList);
        }
        sortedAantallen = new ArrayList<>(sortHosts.keySet());
        Collections.reverse(sortedAantallen);
    }
    private VSLM(IPadress address) {
        this.origanalAdress = address;
        sortHosts= new TreeMap<>();
        sortedAantallen = new ArrayList<>();
    }
    public static VSLM createVSLMWithAantalHostsPer(IPadress originalAdress,int aantalHostsPer) {
        VSLM vslm = new VSLM(originalAdress);
        vslm.maakVoorAantalHosts(aantalHostsPer);
        return vslm;
    }
    public static VSLM createVSLMWithAantalSubnetsMinimum(IPadress origanalAdress, int aantalSubnetsMinimum) {
        int prefix=origanalAdress.getPrefix();
        int bitsToAdd=ExtendedMath.roundUnder(ExtendedMath.log(aantalSubnetsMinimum,2));
        int newPrefix=prefix+bitsToAdd;
        int aantalHostPer=IPadress.getPossibilities(newPrefix);
        return createVSLMWithAantalHostsPer(origanalAdress,aantalHostPer);
    }

    private void maakVoorAantalHosts(int hostCountPerSubnet) {


        int prefix=IPadress.getSubNetMaskBits(hostCountPerSubnet);
        int prefixThis= origanalAdress.getPrefix();
        int totaalAantalHost=IPadress.getPossibilities(prefixThis);
        int hostPerNetwerk=IPadress.getPossibilities(prefix)+2;

        char number=0;
        ArrayList<String> arrayList=new ArrayList<>();
        for (int i = 0; i < totaalAantalHost; i+=hostPerNetwerk) {
            String toNumber=numberToLetters(number);
            arrayList.add(toNumber);
            number++;
        }
        sortedAantallen.add(hostCountPerSubnet);
        sortHosts.put(hostCountPerSubnet,arrayList);
    }
    public void geefSubnetInfo(boolean general) {
        int bits=origanalAdress.getPrefix()+1;
        int somBits=0;
        for (Integer value : sortedAantallen) {
            int dinges = IPadress.getSubNetMaskBits(value);
            somBits += IPadress.getPossibilities(dinges) + 2;
        }
        int moglijkheden=origanalAdress.getPossibilities()+2;
        if (somBits> moglijkheden) {
            System.out.println("dit gaat er niet inpassen");
            return;
        }
        int adress=origanalAdress.getNetwerk().getAddress();
        if (general) {

            for (Integer integer : sortedAantallen) {
                ArrayList<String> stringList = sortHosts.get(integer);
                int prefex = IPadress.getSubNetMaskBits(integer);
                int mogelijkheden = IPadress.getPossibilities(prefex);
                for (String naam : stringList) {
                    IPadress iPadress = new IPadress(IPadress.toIpAdress(adress) + "/" + prefex);

                    System.out.printf("naam: %s, aantalHosts: %d, broadcastAddress: %s%n", naam, mogelijkheden, iPadress.getSubnetMask());

                    System.out.printf("netwerk: %s: %s/%d%n", naam, iPadress, prefex);
                    adress += mogelijkheden + 1;
                    iPadress = new IPadress(IPadress.toIpAdress(adress) + "/" + prefex);
                    System.out.printf("broadcast: %s: %s/%d%n", naam, iPadress, prefex);
                    System.out.println();
                    adress++;
                }

            }
        } else {
            for (Integer integer : sortedAantallen) {
                ArrayList<String> stringList = sortHosts.get(integer);
                int prefex = IPadress.getSubNetMaskBits(integer);
                int mogelijkheden = IPadress.getPossibilities(IPadress.getSubNetMaskBits(integer));
                for (String naam : stringList) {
                    IPadress iPadress = new IPadress(IPadress.toIpAdress(adress) + "/" + prefex);
                    System.out.printf("netwerk: %s: %s/%d%n", naam, iPadress, prefex);
                    adress++;
                    for (int k = 0; k < mogelijkheden; k++) {
                        iPadress = new IPadress(IPadress.toIpAdress(adress) + "/" + prefex);
                        System.out.printf("host%d: %s: %s/%d%n", k, naam, iPadress, prefex);
                        adress++;
                    }
                    iPadress = new IPadress(IPadress.toIpAdress(adress) + "/" + prefex);
                    System.out.printf("broadcast: %s: %s/%d%n", naam, iPadress, prefex);
                    System.out.println();
                    adress++;
                }
            }
        }

    }

    public void geefHostVanSubNet(int subnet, int network) {
        int adress=origanalAdress.getNetwerk().getAddress();
        String naam=sortHosts.get(sortedAantallen.get(0)).get(0);
        int prefex=0;
        int subnets=0;
        for (Integer integer : sortedAantallen) {
            ArrayList<String> stringList = sortHosts.get(integer);
            prefex = IPadress.getSubNetMaskBits(integer);
            for (String s : stringList) {
                naam = s;
                adress += IPadress.getPossibilities(IPadress.getSubNetMaskBits(integer)) + 2;
                subnets++;
                if (subnet == subnets) {
                    break;
                }
            }
            if (subnet == subnets) {
                break;
            }
        }
        adress+=network;
        IPadress iPadress=new IPadress(IPadress.toIpAdress(adress)+"/"+prefex);
        System.out.printf("%s: %s/%d%n%n", naam, iPadress,prefex);
    }
    public void geefAllesVanSubnet(int subnet) {
        int adress=origanalAdress.getNetwerk().getAddress();
        String naam=sortHosts.get(sortedAantallen.get(0)).get(0);
        int prefex=IPadress.getSubNetMaskBits(sortedAantallen.get(0));
        int subnets=0;
        for (Integer integer : sortedAantallen) {
            if (subnet == subnets) {
                break;
            }
            ArrayList<String> stringList = sortHosts.get(integer);
            prefex = IPadress.getSubNetMaskBits(integer);
            int mogelijkHeden=IPadress.getPossibilities(prefex)+2;
            for (String s : stringList) {
                if (subnet == subnets) {
                    break;
                }
                naam = s;
                adress += mogelijkHeden;
                subnets++;
            }
        }
        int mogelijkheden=IPadress.getPossibilities(prefex)+2;

        IPadress iPadress=new IPadress(IPadress.toIpAdress(adress)+"/"+prefex);
        System.out.printf("netwerk: %s: %s/%d%n", naam, iPadress,prefex);
        adress++;
        for (int i =0; i < mogelijkheden-2; i++) {
            iPadress=new IPadress(IPadress.toIpAdress(adress)+"/"+prefex);
            System.out.printf("host%d: %s: %s/%d%n", i,naam, iPadress,prefex);
            adress++;
        }
        iPadress=new IPadress(IPadress.toIpAdress(adress));
        System.out.printf("broadcast: %s: %s%n", naam, iPadress);

    }
    public void getSeeHostsPerNetwerk() {
        for (Integer integer : sortedAantallen) {
            ArrayList<String> stringList = sortHosts.get(integer);
            int prefex = IPadress.getSubNetMaskBits(integer);
            int mogelijkheden=IPadress.getPossibilities(prefex);
            System.out.printf("%s : %d%n",stringList,mogelijkheden);
        }
    }
    private static String numberToLetters(int number) {
        StringBuilder result = new StringBuilder();

        while (number >= 0) {
            int remainder = number % 26;
            result.insert(0, (char) ('A' + remainder)); // Voeg de letter aan het begin toe
            number = (number / 26) - 1;
        }

        return result.toString();
    }
    public void getInfo(String name, int host) {
        int adress=origanalAdress.getNetwerk().getAddress();
        String naam=sortHosts.get(sortedAantallen.get(0)).get(0);
        int prefex=IPadress.getSubNetMaskBits(sortedAantallen.get(0));
        int subnets=0;
        for (Integer integer : sortedAantallen) {
            if (naam.equals(name)) {
                break;
            }
            ArrayList<String> stringList = sortHosts.get(integer);
            prefex = IPadress.getSubNetMaskBits(integer);
            int mogelijkHeden=IPadress.getPossibilities(prefex)+2;
            for (String s : stringList) {
                if (naam.equals(s)) {
                    break;
                }
                naam = s;
                adress += mogelijkHeden;
                subnets++;
            }
        }
        int mogelijkheden=IPadress.getPossibilities(prefex)+2;
        adress++;
        if (host>mogelijkheden-2) {
            System.out.println("host bestaat niet: max "+ (mogelijkheden-2));
        }
        adress+=host;
        IPadress iPadress=new IPadress(IPadress.toIpAdress(adress));
        System.out.printf("host: %s/%d%n", iPadress, prefex);
    }
}
