package org.fantasme.chatirc.tools.properties;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExceptionService {

    /**
     * logger
     */
    private static Logger logger = Logger.getLogger(ExceptionService.class.getName());

    /**
     * message
     */
    private String message;

    /**
     * exception
     */
    private Exception exception;


    /**
     * Constructeur d'exception
     *
     * @param message   message assorti é l'exception
     * @param exception : exception
     */
    public ExceptionService(String message, Exception exception) {
        this.message = message;
        this.exception = exception;

        //Ecriture dans le fichier de log
        traceLog();
    }

    /**
     * écrit les logs de l'exception dans un fichier
     */
    private void traceLog() {
        if (exception != null) {
            logger.log(Level.WARNING, message, exception);
        } else {
            logger.log(Level.WARNING, message);
        }
    }

    /**
     * getter
     *
     * @return message de l'exception
     */
    public String getMessage() {
        return message;
    }

    /**
     * setter
     *
     * @param message : message de l'exception
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
}
