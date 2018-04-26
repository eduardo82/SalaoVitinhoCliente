package br.com.eduardo.salaovitinhocliente.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by eduardo.vasconcelos on 27/10/2017.
 */

public class DateUtils {
    private static final String DATA_HORA = "dd/MM/yyyy HH:mm";
    private static final String HORA = "HH:mm";
    private static final String DATA = "dd/MM/yyyy";
    private static final String DIA = "dd";
    private static SimpleDateFormat df = new SimpleDateFormat(DATA_HORA);


    public static Date parseDataHora(String dataHora) {
        try {
            return df.parse(dataHora);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Date parseData(String dataHora) {
        try {
            df = new SimpleDateFormat(DATA);
            return df.parse(dataHora);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String formatHora(Date data) {
        df = new SimpleDateFormat(HORA);
        return df.format(data);
    }

    public static String formatDia(Date data) {
        df = new SimpleDateFormat(DIA);
        return df.format(data);
    }

    public static Integer parseDia(Date data) {
        if (data != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(data);
            return calendar.get(Calendar.DAY_OF_MONTH);
        }
        return 0;
    }
}
