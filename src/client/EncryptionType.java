/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

/**
 * types of encryption methods available
 * Ceasar = basic ceasar cipher
 * RSA = standard 1058 bit RSA
 * DH_KeyChain = encrypted key chain with initial Diffie-Helman exchange
 * @author dave
 */
public enum EncryptionType {
    DH,RSA,KeyChain,Caesar, None
}
