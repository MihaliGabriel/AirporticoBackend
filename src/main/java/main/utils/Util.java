package main.utils;


import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private Util() {

    }
    public static Long getLong(Object number) {

        if (number == null) {
            return null;
        }

        return ((Number) number).longValue();
    }

    public static Double getDouble(Object number) {

        if (number == null) {
            return null;
        }

        return ((Number) number).doubleValue();
    }

    public static Integer getInteger(Object number) {

        if (number == null) {
            return null;
        }

        return ((Number) number).intValue();
    }

    public static boolean checkValidInteger(Integer number) {
        if(number == null)
            return false;

        return number >= 0;
    }

    public static boolean checkValidDouble(Double number) {
        if(number == null)
            return false;

        return number >= 0;
    }


    public static boolean checkValidString(String field) {
        return field != null && !field.isEmpty();
    }

    /**
     * @Author GXM
     * @param passengerSeat un string de forma "16A" care reprezinta scaunul pasagerului
     * @return returneaza un array de rand si coloana dintr-un string care reprezinta locul parsat cu un regex
     */
    public static int[] splitPassengerSeats(String passengerSeat) {
        Pattern pattern = Pattern.compile("^(\\d+)([A-Z])(\\+?)$");
        Matcher matcher = pattern.matcher(passengerSeat);

        if (matcher.find()) {
            int number = Integer.parseInt(matcher.group(1));
            int letterNum = matcher.group(2).charAt(0) - 'A';
            //parseaza + ca un intreg, urmeaza a fi procesat in generateSeatMap
            if(!matcher.group(3).isEmpty()) {
                int reserved =  matcher.group(3).charAt(0) - '+' + 1;
                System.out.println(number + " " + letterNum + " " + reserved);
                return new int[]{letterNum, number, reserved};
            }
            else {
                System.out.println(number + " " + letterNum);
                return new int[]{letterNum, number};
            }
        } else {
            throw new IllegalArgumentException("Invalid input format");
        }
    }

    public static boolean isBirthday(Date date, int birthDay, int birthMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;

        return day == birthDay && month == birthMonth;
    }

    public static boolean is5YearAnniversary(Date currentDate, Date userDate) {

        LocalDate localDate1 = userDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Period period = Period.between(localDate1, localDate2);

        return period.getYears() == 5;
    }


}
