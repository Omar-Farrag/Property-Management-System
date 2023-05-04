/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package General;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class PasswordManager {

    //Secret key to be used in the AES encryption
    //Generated using key generator
    private static final String SECRET_KEY = "miNrXFshCH1ejFF5v4P31w==";

    /**
     * Encrypts a given string using AES Encryption
     *
     * @param strToEncrypt plain text to be encrypted
     * @return cypher text encoded using Base64 encoding
     */
    public static String encrypt(String strToEncrypt) {

        try {
            //Cipher object to be used to encrpy passwords
            //It will use the AES encryption algorithm
            //PKCSS5Padding scheme for padding the plaintext to create equal length blocks
            //ECB mode meaning plain text will be divided into blocks -> each encrypted -> concatenated
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            //Initialize a SecretKeySpec object using the string secret key
            //This object will be passed on to the Cipher object to be used for encryption
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

            //Initialize cipher object using secrey key object and set it to encryption mode
            cipher.init(Cipher.ENCRYPT_MODE, key);

            //Convert string to encrypt into binary data that can be encrypted by cipher object
            byte[] encodedBytes;
            encodedBytes = strToEncrypt.getBytes("UTF-8");

            //doFinal takes the bytes of the input string and returns the encrypted bytes
            byte[] encryptedBytes = cipher.doFinal(encodedBytes);

            //Convert the encrypted bytes to string using Base64 encoding
            //Base64 used because it only uses printable ASCII characters which guarantees that we can store string in DB
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(PasswordManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /**
     * Decrypts a given string to its original string before encryption
     *
     * @param strToDecrypt cypher text to be decrypted encoded using Base64
     * @return the original plain text before encryption
     */
    public static String decrypt(String strToDecrypt) {
        try {
            //Cipher object to be used to decrypt passwords
            //It will use the AES encryption algorithm
            //PKCSS5Padding scheme for padding the plaintext to create equal length blocks
            //ECB mode meaning plain text will be divided into blocks -> each decrypted -> concatenated
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            //Initialize a SecretKeySpec object using the string secret key
            //This object will be passed on to the Cipher object to be used for decryption
            SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

            //Initialize cipher object using secrey key object and set it to decryption mode
            cipher.init(Cipher.DECRYPT_MODE, key);

            //Convert encrypted Base64 string back to binary data that can be decrypted by cipher object
            byte[] decodedBytes = Base64.getDecoder().decode(strToDecrypt);

            //doFinal takes decoded bytes of encrypted string and returns the original bytes
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            //return the string back in original UTF-8 encoded format
            return new String(decryptedBytes, "UTF-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(PasswordManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public static void main(String[] args) {
        String[] passwords = new String[]{
            "A1",
            "A2",
            "A3",
            "A4",
            "A5",
            "A6",
            "A7",
            "A8"
        };
        for (String password : passwords) {
            System.out.println("UPDATE USERS SET PASSWORD = '" + encrypt(password) + "' WHERE USER_ID = '" + password + "';");
        }
    }

}
