package com.example.log4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final String NO_COLOR = "\u001B[0m";
    private static final String HIGHLIGHT_COLOR = "\u001B[96m";
    public  static final String MSG= HIGHLIGHT_COLOR + "\"This is \nmy ${custom} $${date:MM-dd-yyyy} <script>message!" + NO_COLOR;


    public static void main(String[] argv) {
        Logger logger = LogManager.getLogger();
        logger.info(MSG);
        Exception inner = new Exception(MSG);
        Exception outer = new Exception(MSG, inner);
        logger.info("Oops!", outer);
    }
}
