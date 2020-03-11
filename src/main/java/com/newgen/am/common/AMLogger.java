/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newgen.am.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nhungtt
 */
public class AMLogger {
    private static Logger logger = LoggerFactory.getLogger(AMLogger.class);
    public static void logMessage(String className, String methodName, long refId, String message) {
        logger.info(String.format("[%s][%s]REF=%s, MSG=%s", className, methodName, String.valueOf(refId), message));
    }

    public static void logError(String className, String methodName, long refId, Throwable ex) {
        logger.info(String.format("[%s][%s]REF=%s, EXCEPTION", className, methodName, String.valueOf(refId)), ex);
    }
}
