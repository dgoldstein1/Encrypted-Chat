/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * public key for Client
 *
 * @author dave
 */
public class ChatCipher {

    private KeyPair RSAKeys, dhkeys;
    private Cipher RSAcipher, DHCipher;
    private EncryptionType et;
    private int privateCaesar, privateEncryption;
    private Random randomGen;

    public ChatCipher(EncryptionType et) {
        String id = UUID.randomUUID().toString();
        long seed = 1;
        for (int i = 0; i < 5; i++) {
            seed *= (int) id.charAt(i);
            seed += (int) id.charAt(i);
        }
        randomGen = new Random(seed);
        this.et = et;
        initRSA();
        initDH();

    }

    /**
     * 1024 RSA standard encryption using java.security
     */
    private final void initRSA() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            RSAKeys = kpg.generateKeyPair();
            RSAcipher = Cipher.getInstance("RSA");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     */
    private final void initDH() {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(1024);
            DHCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            dhkeys = keyPairGenerator.generateKeyPair();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * encrypts for given user / comm
     *
     * @param msg
     * @param recipient
     * @return
     */
    public String encryptCommunication(String msg, ClientInformation recipient) {
        String encryptedMsg = "";
        int ascii;
        switch (et) {
            case RSA:
                try {
                    RSAcipher.init(Cipher.ENCRYPT_MODE, recipient.rsaPublicKey());
                    byte[] encryptedBytes = RSAcipher.doFinal(msg.getBytes());
                    return new String(Base64.getEncoder().encode(encryptedBytes));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case DH:
                try {
                    final KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
                    keyAgreement.init(dhkeys.getPrivate());
                    keyAgreement.doPhase(recipient.DHPublicKey(), true);

                    byte[] secretKey = shortenSecretKey(keyAgreement.generateSecret());
                    final SecretKeySpec keySpec = new SecretKeySpec(secretKey, "DES");
                    DHCipher.init(Cipher.ENCRYPT_MODE, keySpec);
                    final byte[] encryptedBytes = DHCipher.doFinal(msg.getBytes());
                    return new String(Base64.getEncoder().encode(encryptedBytes));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            case KeyChain:
                String[] oneTimePad = new String[msg.length()];
                for (int i = 0; i < msg.length(); i++) {
                    int j = randomGen.nextInt(100);
                    oneTimePad[i] = j + "";
                    ascii = ((int) msg.charAt(i)) ^ j;
                    encryptedMsg += String.valueOf((char) ascii);
                }

                //encrypt one time pad
                String strOne = "";
                for (String s : oneTimePad) {
                    strOne += "///" + s;
                }

                this.setEncryption(EncryptionType.DH);
                String encryptedOneTime = this.encryptCommunication(strOne, recipient);
                this.setEncryption(EncryptionType.KeyChain);

                return encryptedOneTime + "/////" + encryptedMsg;

            case Caesar:
                int n = randomGen.nextInt(100) + 1; //1 <-> 128
                for (int i = 0; i < msg.length(); i++) {
                    ascii = ((int) msg.charAt(i)) + n;
                    encryptedMsg += String.valueOf((char) ascii);
                }

                //encrypt shift for recipient
                this.setEncryption(EncryptionType.DH);
                String shift = this.encryptCommunication(n + "", recipient);
                this.setEncryption(EncryptionType.Caesar);

                return shift + "/////" + encryptedMsg;

            case None:
                return msg;

            default:
                break;
        }

        //unreachable
        System.out.println("Encryption Failed");
        return null;
    }

    /**
     * assumed for owner of this cipher
     *
     * @param cm
     * @param sender
     * @return
     */
    public String decryptCommunication(Communication cm) {
        ClientInformation sender = cm.sender();
        String msg = cm.getMessage();
        String decryptedMsg = "";
        int nSlashes = 0;
        int msgStart = 0;
        int ascii;
        switch (cm.encryption()) {
            case RSA:
                try {
                    RSAcipher.init(Cipher.DECRYPT_MODE, RSAKeys.getPrivate());
                    byte[] ciphertextBytes = Base64.getDecoder().decode(msg.getBytes());
                    byte[] decryptedBytes = RSAcipher.doFinal(ciphertextBytes);
                    return new String(decryptedBytes);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            case DH:
                try {
                    final KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
                    keyAgreement.init(dhkeys.getPrivate());
                    keyAgreement.doPhase(sender.DHPublicKey(), true);

                    byte[] secretKey = shortenSecretKey(keyAgreement.generateSecret());
                    final SecretKeySpec keySpec = new SecretKeySpec(secretKey, "DES");

                    DHCipher.init(Cipher.DECRYPT_MODE, keySpec);
                    byte[] ciphertextBytes = Base64.getDecoder().decode(msg.getBytes());
                    byte[] decryptedBytes = DHCipher.doFinal(ciphertextBytes);
                    return new String(decryptedBytes);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            case Caesar:
                String encryptedShift = "";
                for (int i = 0; i < 25; i++) {
                    if (msg.charAt(i) == '/') {
                        nSlashes++;
                        if (nSlashes == 5) {
                            encryptedShift = msg.substring(0, i - 4);
                            msgStart = i + 1;
                            break;
                        }
                    }
                }

                //decrypt shift
                String decryptedShift = this.decryptCommunication(new Communication(CommunicationType.MESSAGE, encryptedShift, EncryptionType.DH, sender, sender));
                int shift = Integer.parseInt(decryptedShift);

                //decypher              
                for (int i = msgStart; i < msg.length(); i++) {
                    ascii = ((int) msg.charAt(i)) - shift;
                    decryptedMsg += String.valueOf((char) ascii);
                }

                return decryptedMsg;
            case KeyChain:
                try {
                    //decrypt one time pad
                    String oneTimePad = "";
                    for (int i = 0; i < msg.length(); i++) {
                        if (msg.charAt(i) == '/') {
                            nSlashes++;
                            if (nSlashes == 5) {
                                oneTimePad = msg.substring(0, i - 4);
                                msgStart = i + 1;
                                break;
                            }
                        }
                    }
                    oneTimePad = this.decryptCommunication(new Communication(CommunicationType.MESSAGE, oneTimePad, EncryptionType.DH, sender, sender));
                    int count = 0;
                    String sub = "";
                    ArrayList<Integer> pad = new ArrayList<Integer>();
                    for (int i = 0; i < oneTimePad.length(); i++) {
                        if (oneTimePad.charAt(i) == '/') {
                            count++;
                        }
                        if (count == 3) {
                            sub = oneTimePad.substring(i + 1, i + 3);
                            pad.add(Integer.parseInt(sub));
                            count = 0;
                        }
                    }

                    int j = 0;
                    for (int i = msgStart; i < msg.length(); i++) {
                        ascii = ((int) msg.charAt(i)) ^ pad.get(j);
                        decryptedMsg += String.valueOf((char) ascii);
                        j++;
                    }

                    return decryptedMsg;

                } catch (Exception e) {
                    return msg;
                }

            case None:
                return msg;
            default:
                break;
        }

        //unreachable
        System.out.println("Decrpytion Failed");
        return null;
    }

    /**
     * 1024 bit symmetric key size is so big for DES so we must shorten the key
     * size. You can get first 8 longKey of the byte array or can use a key
     * factory
     *
     * taken from :
     * https://github.com/codvio/diffie-hellman-helloworld/blob/master/src/main/java/com/codvio/examples/diffie_hellman_helloworld/Person.java
     *
     * @param longKey
     *
     * @return
     */
    private byte[] shortenSecretKey(final byte[] longKey) {

        try {
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            final DESKeySpec desSpec = new DESKeySpec(longKey);
            return keyFactory.generateSecret(desSpec).getEncoded();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Getters and Setters *
     */
    public EncryptionType getEncryption() {
        return et;
    }


    public PublicKey RSApublic() {
        return RSAKeys.getPublic();
    }

    public void setEncryption(EncryptionType et) {
        this.et = et;
    }

    public PublicKey DHpublic() {
        return dhkeys.getPublic();
    }

}
