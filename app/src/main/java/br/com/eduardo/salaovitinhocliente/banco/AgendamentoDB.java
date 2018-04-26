package br.com.eduardo.salaovitinhocliente.banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.eduardo.salaovitinhocliente.formatter.DateFormatter;
import br.com.eduardo.salaovitinhocliente.model.Horario;

/**
 * Created by Eduardo on 09/03/2018.
 */
public class AgendamentoDB extends BancoDB {
    private static final String TABELA = "agendamento";

    public AgendamentoDB(Context context) {
        super(context);
    }

    public long salvar(Horario horario) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues valores = new ContentValues();
            valores.put("data", horario.getDiaAtendimento());
            valores.put("horario", horario.getHoraAtendimento());
            valores.put("profissional", horario.getProfissional());
            valores.put("aprovado", horario.isAutorizado());
            valores.put("recusado", horario.isRecusado());
            return db.insertOrThrow(TABELA, null, valores);
        } catch (RuntimeException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        finally {
            db.close();
        }
        return 0;
    }

    public long atualizar(Horario horario) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues valores = new ContentValues();
            valores.put("data", horario.getDiaAtendimento());
            valores.put("horario", horario.getHoraAtendimento());
            valores.put("profissional", horario.getProfissional());
            valores.put("aprovado", String.valueOf(horario.isAutorizado()));
            valores.put("recusado", String.valueOf(horario.isRecusado()));
            return db.update(TABELA, valores, "data = '" + horario.getDiaAtendimento() +"' AND horario = '" +horario.getHoraAtendimento() + "'", null);
        } catch (RuntimeException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        finally {
            db.close();
        }
        return 0;
    }

    public long deletar(Horario horario) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            return db.delete(TABELA, "data=? AND horario=?", new String[]{horario.getDiaAtendimento(), horario.getHoraAtendimento()});
        } catch (RuntimeException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        } finally {
            db.close();
        }

        return 0L;
    }

    public Horario buscaHorario(Horario filtro) {
        SQLiteDatabase db = getWritableDatabase();
        List<Horario> horarios = new ArrayList<>();

        try {
            Cursor cursor = db.query(TABELA, null, "data = '" + filtro.getDiaAtendimento() + "' AND horario = '" + filtro.getHoraAtendimento() +"'" ,null, null, null, "data, horario");
            horarios = toList(cursor);
        } catch (RuntimeException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        } finally {
            db.close();
        }
        return horarios.get(0);
    }

    public List<Horario> buscaTodosAgendamentos() {
        SQLiteDatabase db = getWritableDatabase();
        List<Horario> horarios = new ArrayList<>();

        try {
            Cursor cursor = db.query(TABELA, null, "data >= '" + DateFormatter.getDiaAtual() + "'" ,null, null, null, "data, horario");
            db.delete(TABELA, "data <=?", new String[]{DateFormatter.getDiaAnterior()});
            horarios = toList(cursor);
        } catch (RuntimeException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        } finally {
            db.close();
        }
        return horarios;
    }

    private List<Horario> toList(Cursor cursor) {
        List<Horario> horarios = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Horario horario = new Horario();
                horario.setHoraAtendimento(cursor.getString(cursor.getColumnIndex("horario")));
                horario.setDiaAtendimento(cursor.getString(cursor.getColumnIndex("data")));
                horario.setProfissional(cursor.getString(cursor.getColumnIndex("profissional")));
                horario.setAutorizado(Boolean.valueOf(cursor.getString(cursor.getColumnIndex("aprovado"))));
                horario.setRecusado(Boolean.valueOf(cursor.getString(cursor.getColumnIndex("recusado"))));

                horarios.add(horario);
            } while (cursor.moveToNext());

        }
        return horarios;
    }
}