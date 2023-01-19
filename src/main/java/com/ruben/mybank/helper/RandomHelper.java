package com.ruben.mybank.helper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.StringJoiner;
import java.time.ZoneId;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import com.ruben.mybank.exception.CannotPerformOperationException;

public class RandomHelper {

    protected static SecureRandom random = new SecureRandom();

    public static synchronized String getRandomHexString(int size) {
        StringBuffer sb = new StringBuffer();
        while (sb.length() < size) {
            sb.append(Integer.toHexString(random.nextInt()));
        }
        return sb.toString().substring(0, size);
    }

    public static synchronized String getToken(int size) {
        return Long.toString(Math.abs(random.nextLong()), size);
    }

    public static int getRandomInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    public static String getRandomIntCantidad(int cantidad) {
        
        StringBuilder randomStringFinal = new StringBuilder();

        for(int i=0; i<cantidad; i++){
         Random rand = new Random();
         int randomNum = rand.nextInt((9 - 1) + 1) + 1;
         randomStringFinal.append(String.valueOf(randomNum));
        }

        return randomStringFinal.toString();
    }


    public static String getRandomIban() {
        String pattern = "[E][S][1][2][\\s]\\d{4}[\\s]\\d{4}[\\s]\\d{2}[\\s]\\d{8}$";
        StringJoiner ibanBuilder = new StringJoiner(" ");
        
        String iban = ibanBuilder.add("ES12")
            .add(getRandomIntCantidad(4))
            .add(getRandomIntCantidad(4))
            .add(getRandomIntCantidad(2))
            .add(getRandomIntCantidad(8)).toString(); 
        
        if (Pattern.matches(pattern,iban)) {
            return iban;
        }
        return "null";
    }

    public static Long getRandomLongCuenta(Long min, Long max) {
        Random rand = new Random();
        Long randomNum = rand.nextLong((max - min) + 1) + min;
        return randomNum;
    }

    public static int getRandomInt2(int minValue, int maxValue) {
        return ThreadLocalRandom.current().nextInt(minValue, maxValue);
    }

    public static LocalDateTime getRadomDateTime() {
        return RandomHelper.getRadomDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date getRadomDate() {
        GregorianCalendar gc = new GregorianCalendar();
        int year = getRandomInt(2010, 2023);
        gc.set(gc.YEAR, year);
        int dayOfYear = getRandomInt(1, gc.getActualMaximum(gc.DAY_OF_YEAR));
        gc.set(gc.DAY_OF_YEAR, dayOfYear);
        Date date = new Date(gc.getTimeInMillis());
        return date;
    }

    public static LocalDateTime getRadomDate2() {
        int randomSeconds = new Random().nextInt(3600 * 24);
        LocalDateTime anyTime = LocalDateTime.now().minusSeconds(randomSeconds);
        return anyTime;
    }

    public static char getRadomChar() {
        Random r = new Random();
        char c = (char) (r.nextInt(26) + 'a');
        return Character.toUpperCase(c);
    }

    public static double getRadomDouble(int rangeMin, int rangeMax) {
        Random r = new Random();
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    public static double getRadomDouble(double minValue, double maxValue) {
        return Math.round(ThreadLocalRandom.current().nextDouble(minValue, maxValue) * 100d) / 100d;
    }

    public static String getSHA256(String strToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(strToHash.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(digest).toLowerCase();
        } catch (NoSuchAlgorithmException ex) {
            throw new CannotPerformOperationException("no such algorithm: sha256");
        }
    }

}
