/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DatabaseManagement.Exceptions;

import DatabaseManagement.Table;

/**
 *
 * @author Dell
 */
public class MissingAttributeException extends Exception {

    String message;

    public MissingAttributeException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
