package br.com.eduardo.salaovitinhocliente.banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static br.com.eduardo.salaovitinhocliente.constants.SalaoVitinhoConstants.NOME_BANCO;
import static br.com.eduardo.salaovitinhocliente.constants.SalaoVitinhoConstants.VERSAO;

/**
 * Created by Eduardo on 18/03/2018.
 */

public class BancoDB extends SQLiteOpenHelper {

    public BancoDB(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists agendamento(id integer primary key autoincrement," +
            "data text, horario text, profissional text, aprovado text, recusado text)");
        sqLiteDatabase.execSQL("create table if not exists mensagem(id integer primary key autoincrement," +
            "mensagem text, telefone text, resposta text, lido text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
