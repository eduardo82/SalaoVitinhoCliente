package br.com.eduardo.salaovitinhocliente;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.com.eduardo.salaovitinhocliente.adapter.MensagemAdapter;
import br.com.eduardo.salaovitinhocliente.banco.MensagemDB;
import br.com.eduardo.salaovitinhocliente.constants.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinhocliente.model.Mensagem;
import br.com.eduardo.salaovitinhocliente.utils.FirebaseUtils;
import br.com.eduardo.salaovitinhocliente.utils.PreferencesUtils;

public class MensagemFragment extends Fragment {

    View view;
    ListView mensagensClientesListView;
    List<Mensagem> mensagensClientes;
    Mensagem mensagemLida;
    MensagemDB mensagemDB;
    Button buttonChamaDetalheMensagem;

    Context context = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_leitor_mensagem, viewGroup, false);
        context = view.getContext();

        mensagemDB = new MensagemDB(context);

        buttonChamaDetalheMensagem = view.findViewById(R.id.chamaDetalheMensagem);
        mensagensClientesListView = view.findViewById(R.id.listViewMensagens);
        mensagensClientes = new ArrayList<>();

        mensagensClientesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            mensagemLida = mensagensClientes.get(position);

            DetalheMensagemFragment detalhe = new DetalheMensagemFragment();
            getFragmentManager().beginTransaction().replace(R.id.conteudo, detalhe).commit();
            }
        });

        final DialogInterface.OnClickListener acaoBotaoSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.MENSAGEM, SalaoVitinhoConstants.VITINHO, PreferencesUtils.getTelefone(context)).removeValue();
                mensagemDB.deletar();
            }
        };

        mensagensClientes = mensagemDB.buscaMensagem(null);

        if (mensagensClientes.size() > 0) {
            MensagemAdapter mensagemAdapter = new MensagemAdapter(context, mensagensClientes);
            mensagensClientesListView.setAdapter(mensagemAdapter);
            buttonChamaDetalheMensagem.setVisibility(View.GONE);
        }

        buttonChamaDetalheMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            chamaDetalhe();
            }
        });



        return view;
    }

    private void chamaDetalhe() {
        getFragmentManager().beginTransaction().replace(R.id.conteudo, new DetalheMensagemFragment()).commit();
    }
}
