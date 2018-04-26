package br.com.eduardo.salaovitinhocliente;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.eduardo.salaovitinhocliente.adapter.AgendamentoAdapter;
import br.com.eduardo.salaovitinhocliente.banco.AgendamentoDB;
import br.com.eduardo.salaovitinhocliente.constants.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinhocliente.formatter.DateFormatter;
import br.com.eduardo.salaovitinhocliente.model.Horario;
import br.com.eduardo.salaovitinhocliente.utils.FirebaseUtils;
import br.com.eduardo.salaovitinhocliente.utils.SalaoVitinhoClienteUtils;

/**
 * Created by Eduardo on 10/03/2018.
 */
public class VisualizarAgendamentoFragment extends Fragment {

    View view;
    Context context;
    ListView listViewAgendamentos;
    LinearLayout legendaLayout;
    Button btnCancelarAgendamento;
    AgendamentoAdapter agendamentoAdapter;
    List<Horario> horarios = new ArrayList<>();
    int contadorAgendamento = 1;
    AgendamentoDB db;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_visualizar_agendamento, viewGroup, false);
        context = view.getContext();
        listViewAgendamentos = view.findViewById(R.id.listViewCancelamentos);
        btnCancelarAgendamento = view.findViewById(R.id.buttonCancelarAgendamento);
        legendaLayout = view.findViewById(R.id.layout_legenda);
        db = new AgendamentoDB(context);

        prencheListViewHorarios();

        if (horarios.size() == 0) {
            listViewAgendamentos.setVisibility(View.GONE);
            btnCancelarAgendamento.setVisibility(View.GONE);
            legendaLayout.setVisibility(View.GONE);
            Toast.makeText(context, "Você não possui agendamentos.", Toast.LENGTH_SHORT).show();
        }
        else {
            listViewAgendamentos.setVisibility(View.VISIBLE);
            btnCancelarAgendamento.setVisibility(View.VISIBLE);
            legendaLayout.setVisibility(View.VISIBLE);
            criaAcaoBotaoCancelamento();
        }
        return view;
    }

    private void criaAcaoBotaoCancelamento() {
        final DialogInterface.OnClickListener acaoBotaoSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ArrayList<Horario> horariosEscolhidos = agendamentoAdapter.getHorariosEscolhidos();
                contadorAgendamento = agendamentoAdapter.getCount();
                if (horariosEscolhidos.size() > 0) {
                    for (Horario horario : horariosEscolhidos) {
                        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO, SalaoVitinhoConstants.VITINHO,
                            SalaoVitinhoConstants.NAO_ATENDIDO, horario.getDiaAtendimento(), horario.getHoraAtendimento())
                            .removeValue();
                        db.deletar(horario);
                    }

                    for (Horario horario : horariosEscolhidos) {
                        agendamentoAdapter.getHorariosAgendados().remove(horario);
                    }

                    if (horariosEscolhidos.size() == contadorAgendamento) {
                        DialogInterface.OnClickListener acaBotaoSim = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                        };
                        SalaoVitinhoClienteUtils.exibeDialogConfirmacao(context, "Agendamentos apagados com sucesso!", acaBotaoSim);
                    }
                    else {
                        DialogInterface.OnClickListener acaBotaoSim = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getFragmentManager().beginTransaction().replace(R.id.conteudo,
                                    new VisualizarAgendamentoFragment()).commit();
                            }
                        };
                        SalaoVitinhoClienteUtils.exibeDialogConfirmacao(context, "Agendamentos apagados com sucesso!", acaBotaoSim);

                    }
                }
                else {
                    Toast.makeText(context, "Escolha pelo menos um horário.", Toast.LENGTH_SHORT).show();
                }
            }
        };


        btnCancelarAgendamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalaoVitinhoClienteUtils.exibeDialogConfirmacao(context, "Confirma o cancelamento do(s) agendamento(s)?", acaoBotaoSim, null);
            }
        });
    }

    private void prencheListViewHorarios() {
        List<Horario> horariosAux = db.buscaTodosAgendamentos();

        if (horariosAux.size() > 0) {
            for (Horario horario : horariosAux) {
                if (DateFormatter.getDiaAtual().compareTo(horario.getDiaAtendimento()) <= 0) {
                    horarios.add(horario);
                }
                else {
                    db.deletar(horario);
                }
            }
            agendamentoAdapter = new AgendamentoAdapter(context, R.layout.fragment_visualizar_agendamento, new ArrayList<>(horarios));
            listViewAgendamentos.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listViewAgendamentos.setAdapter(agendamentoAdapter);

            listViewAgendamentos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                }
            });
        }
    }
}
