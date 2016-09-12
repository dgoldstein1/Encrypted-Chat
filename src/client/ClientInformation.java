package client;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.UUID;
import javax.crypto.SecretKey;

/**
 * Serializable class to store info from Client to store in server
 *
 * @author dave
 */
public class ClientInformation implements Serializable {

    private String name;
    private EncryptionType encryption;
    private PublicKey RSApublic,DHpublic;
    private String id;

    ClientInformation(String name, EncryptionType encryption, PublicKey RSApublic,PublicKey DHpublic) {
        this.name = name;
        this.encryption = encryption;
        this.RSApublic = RSApublic;
        this.DHpublic = DHpublic;
        id = UUID.randomUUID().toString();
        
    }


    /**
     * getters and setters*
     */
    public String name() {
        return name;
    }

    public EncryptionType encryption() {
        return encryption;
    }

    public PublicKey rsaPublicKey() {
        return RSApublic;
    }

    public String id() {
        return id;
    }
    
    public PublicKey DHPublicKey(){
        return DHpublic;
    }
    
    public void setEncryption(EncryptionType et){
        this.encryption = et;
    }
    
    public boolean equals(ClientInformation other){
        return other.id().equals(this.id);
    }
    
}
