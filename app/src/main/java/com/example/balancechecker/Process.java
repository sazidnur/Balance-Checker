package com.example.balancechecker;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

public class Process {

    private String key = "S58CQ2HR2W9JJS3JWZ6SB5XHAF1K2UHPF7";

    public Map getOptions(String address){
        Map<String, String> options = new HashMap<>();
        options.put("module", "account");
        options.put("action", "balance");
        options.put("tag", "latest");
        options.put("address", address);
        options.put("apikey", key);

        return options;
    }

    public static boolean isValidAddress(String addr)
    {
        String regex = "^0x[0-9a-f]{40}$";

        if(addr.matches(regex))
        {
            return true;
        }
        return false;

    }

    public static boolean isChecksumAddress(String addr){
        String regex = "^0x[0-9a-fA-F]{40}$";
        if(!addr.matches(regex))
        {
            return false;
        }

        //to fetch the part after 0x
        String subAddr = addr.substring(2);

        //Make it to original lower case address
        String subAddrLower = subAddr.toLowerCase();

        // if the previous step validates then we will test the checksum part

        // Create a SHA3256 hash (Keccak-256)
        try {
            MessageDigest digestSHA3 = MessageDigest.getInstance("SHA3-256");
            digestSHA3.update(subAddrLower.getBytes(StandardCharsets.UTF_8));

            byte[] bytes = digestSHA3.digest();
            BigInteger bi = new BigInteger(1, bytes);
            String digestMessage = String.format("%0" + (bytes.length << 1) + "x", bi);

            System.out.println(digestMessage);

            for(short i=0 ;i < subAddr.length();i++)
            {
                if(subAddr.charAt(i)>=65 && subAddr.charAt(i)<=91)
                {

                    String ss = Character.toString(digestMessage.charAt(i));
                    if(!(Integer.parseInt(ss,16) > 7 ))
                    {
                        return false;
                    }
                }
            }
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return true;
    }
}
