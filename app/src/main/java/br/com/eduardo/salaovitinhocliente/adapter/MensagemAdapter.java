package br.com.eduardo.salaovitinhocliente.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.eduardo.salaovitinhocliente.R;
import br.com.eduardo.salaovitinhocliente.model.Mensagem;

/**
 * Created by Eduardo on 30/11/2017.
 */

public class MensagemAdapter extends BaseAdapter {

    private final Context context;
    private final List<Mensagem> mensagens;

    public MensagemAdapter(Context context, List<Mensagem> mensagens) {
        this.context = context;
        this.mensagens = mensagens;
    }


    @Override
    public int getCount() {
        return mensagens.size();
    }

    @Override
    public Object getItem(int position) {
        return mensagens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_leitor_mensagem_usuario, parent, false);
        TextView header = (TextView) view.findViewById(R.id.textViewCabecalhoMensagem);
        TextView mensagem = (TextView) view.findViewById(R.id.textViewMensagem);

        Mensagem mensagemCliente = mensagens.get(position);

        if (mensagemCliente.getMensagem() != null) {
            header.setText("VITINHO - Não lida");
            mensagem.setText(mensagemCliente.getMensagem());
        } else {
            header.setText("VITINHO");
            mensagem.setText(mensagemCliente.getResposta());
        }

        return view;
    }
}
