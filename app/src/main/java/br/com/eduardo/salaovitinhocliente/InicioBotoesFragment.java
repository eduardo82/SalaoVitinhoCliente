package br.com.eduardo.salaovitinhocliente;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import br.com.eduardo.salaovitinhocliente.adapter.AdapterHorario;
import br.com.eduardo.salaovitinhocliente.banco.AgendamentoDB;
import br.com.eduardo.salaovitinhocliente.constants.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinhocliente.formatter.DateFormatter;
import br.com.eduardo.salaovitinhocliente.model.Agenda;
import br.com.eduardo.salaovitinhocliente.model.Horario;
import br.com.eduardo.salaovitinhocliente.utils.DateUtils;
import br.com.eduardo.salaovitinhocliente.utils.FirebaseUtils;
import br.com.eduardo.salaovitinhocliente.utils.PreferencesUtils;
import br.com.eduardo.salaovitinhocliente.utils.SalaoVitinhoClienteUtils;

public class InicioBotoesFragment extends Fragment {
    View view;
    Context context;

    private Button agendarBotao;
    private Button visualizarBotao;
    private Button mensagemBotao;
    private Button dadosCadastraisBotao;
    private Button sairBotao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_botoes_tela_inicial, viewGroup, false);
        context = view.getContext();

        agendarBotao = view.findViewById(R.id.agendarBotao);
        visualizarBotao = view.findViewById(R.id.visualizarBotao);
        mensagemBotao = view.findViewById(R.id.mensagemBotao);
        dadosCadastraisBotao= view.findViewById(R.id.dadosCadastraisBotao);
        sairBotao = view.findViewById(R.id.sairBotao);

        trataAcaoClickBotoes();

        return view;
    }

    private void trataAcaoClickBotoes() {
        final FragmentManager fragmentManager = getFragmentManager();

        agendarBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaDadosCadastraisOk()) {
                    fragmentManager.beginTransaction().replace(R.id.conteudo,
                            new AgendamentoFragment()).commit();
                }
                else {
                    SalaoVitinhoClienteUtils.exibeDialogInformacoesUsuario(context, true, true);
                }
            }
        });

        visualizarBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaDadosCadastraisOk()) {
                    fragmentManager.beginTransaction().replace(R.id.conteudo,
                            new VisualizarAgendamentoFragment()).commit();
                }
                else {
                    SalaoVitinhoClienteUtils.exibeDialogInformacoesUsuario(context, true, true);
                }
            }
        });

        dadosCadastraisBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalaoVitinhoClienteUtils.exibeDialogInformacoesUsuario(context, true, true);
            }
        });

        mensagemBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaDadosCadastraisOk()) {
                    fragmentManager.beginTransaction().replace(R.id.conteudo,
                            new MensagemFragment()).commit();
                }
                else {
                    SalaoVitinhoClienteUtils.exibeDialogInformacoesUsuario(context, true, true);
                }
            }
        });

        sairBotao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) context).finishAffinity();
            }
        });
    }

    private boolean verificaDadosCadastraisOk() {
        String usuario = PreferencesUtils.getUsuario(context);
        String telefone = PreferencesUtils.getTelefone(context);

        return usuario.length() > 3 && telefone.length() > 8;
    }
}
