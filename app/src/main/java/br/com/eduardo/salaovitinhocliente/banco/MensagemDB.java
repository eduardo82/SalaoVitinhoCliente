package br.com.eduardo.salaovitinhocliente.banco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.eduardo.salaovitinhocliente.model.Mensagem;

/**
 * Created by Eduardo on 09/03/2018.
 */
public class MensagemDB extends BancoDB {
    private static final String TABELA = "mensagem";

    public MensagemDB(Context context) {
        super(context);
    }

    public long salvar(Mensagem mensagem) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            ContentValues valores = new ContentValues();
            valores.put("mensagem", mensagem.getMensagem());
            valores.put("telefone", mensagem.getTelefone());
            valores.put("resposta", mensagem.getResposta());
            valores.put("lido", 1);
            return db.insertOrThrow(TABELA, null, valores);
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        finally {
            db.close();
        }
        return 0;
    }

    public long deletar() {
        SQLiteDatabase db = getWritableDatabase();

        try {
            return db.delete(TABELA, null, null);
        }
        catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        } finally {
            db.close();
        }

        return 0;
    }

    public long deletar(Long id) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            return db.delete(TABELA, "id = " + id, null);
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        } finally {
            db.close();
        }

        return 0;
    }

    public List<Mensagem> buscaMensagem(String mensagem) {
        SQLiteDatabase db = getWritableDatabase();
        List<Mensagem> mensagems = new ArrayList<>();

        try {
            Cursor cursor;
            if (mensagem != null && mensagem.length() > 0) {
                cursor = db.query(TABELA, null, "resposta = '" + mensagem + "'" ,null, null, null, "id DESC");
            }
            else {
                cursor = db.query(TABELA, null, null ,null, null, null, "id DESC LIMIT 1");
            }
            mensagems = toList(cursor);
        } catch (RuntimeException e) {
            Log.e("ERRO", e.getMessage());
        }
        catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        } finally {
            db.close();
        }
        return mensagems;
    }

    public List<Mensagem> buscaTodosAgendamentos() {
        SQLiteDatabase db = getWritableDatabase();
        List<Mensagem> mensagems = new ArrayList<>();

        try {
            Cursor cursor = db.query(TABELA, null, null ,null, null, null, "id");
            mensagems = toList(cursor);
        } catch (RuntimeException e) {
            Log.e("ERRO", e.getMessage());
        }
        catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        } finally {
            db.close();
        }
        return mensagems;
    }

    private List<Mensagem> toList(Cursor cursor) {
        List<Mensagem> horarios = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Mensagem horario = new Mensagem();
                horario.setId(cursor.getLong(cursor.getColumnIndex("id")));
                horario.setMensagem(cursor.getString(cursor.getColumnIndex("mensagem")));
                horario.setTelefone(cursor.getString(cursor.getColumnIndex("telefone")));
                horario.setResposta(cursor.getString(cursor.getColumnIndex("resposta")));
                horario.setLido(Boolean.valueOf(cursor.getString(cursor.getColumnIndex("lido"))));

                horarios.add(horario);
            } while (cursor.moveToNext());

        }
        return horarios;
    }

    public void execSQL(String sql) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL(sql);
        } finally {
            db.close();
        }
    }

    public void execSQL(String sql, Object[] args) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL(sql, args);
        } finally {
            db.close();
        }
    }
}