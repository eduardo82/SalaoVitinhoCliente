package br.com.eduardo.salaovitinhocliente.utils;

import android.content.Context;
import android.content.SharedPreferences;

import br.com.eduardo.salaovitinhocliente.constants.SalaoVitinhoConstants;

/**
 * Created by eduardo.vasconcelos on 30/10/2017.
 */

public class PreferencesUtils {

    private static final String PREF_FILE_NAME = "salaovitinhoprefs";


    public static void putStringPrefs(Context context, String chave, String valor) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(chave, valor);
        editor.apply();
    }

    public static String getStringPrefsByKey(Context context, String chave){
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_NAME, 0);
        return prefs.getString(chave, "");
    }

    public static void removePref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_NAME, 0);
        prefs.edit().clear().commit();
    }

    public static void removePref(Context context, String chave) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE_NAME, 0);
        prefs.edit().remove(chave).commit();
    }

    public static String getUsuario(Context context) {
        return PreferencesUtils.getStringPrefsByKey(context, SalaoVitinhoConstants.PREF_NOME);
    }

    public static String getTelefone(Context context) {
        return PreferencesUtils.getStringPrefsByKey(context, SalaoVitinhoConstants.PREF_TELEFONE);
    }
}
