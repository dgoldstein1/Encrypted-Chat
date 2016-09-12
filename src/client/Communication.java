package client;

import java.io.*;
/*
 * serializable class used to communicate between clients on the server
 */
public class Communication implements Serializable {
    protected static final long serialVersionUID = 1112122200L;
    private final CommunicationType type;
    private String message;
    private EncryptionType encryption;
    private ClientInformation sender, recipient;

    public Communication(CommunicationType type, String message, EncryptionType encryption, ClientInformation sender,ClientInformation recipient) {
        this.type = type;
        this.message = message;
        this.encryption = encryption;
        this.sender = sender;
        this.recipient = recipient;
    }

    /**getters and setters**/
    public CommunicationType getType() {
        return type;
    }
    public String getMessage() {
        return message;
    }
    public ClientInformation sender(){
        return sender;
    }
    public ClientInformation recipient(){
        return recipient;
    }
    public EncryptionType encryption(){
        return encryption;
    }
    
}

