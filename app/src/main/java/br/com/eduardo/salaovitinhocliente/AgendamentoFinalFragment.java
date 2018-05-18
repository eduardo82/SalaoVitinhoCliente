package br.com.eduardo.salaovitinhocliente;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
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

public class AgendamentoFinalFragment extends Fragment {
    View view;
    Context context;
    final Locale locale = new Locale("pt", "BR");

    String[] cabeleireiros = new String[]{"Vitinho", "João Vitor"};
    String[] horarios = new String[]{ "08:00h", "08:30h", "09:00h",
        "09:30h", "10:30h", "11:00h", "11:30h", "12:00h",
        "12:30h", "13:00h", "13:30h", "14:00h", "14:30h",
        "15:00h", "15:30h", "16:00h", "16:30h", "17:00h",
        "17:30h", "18:00h", "18:30h", "19:00h", "19:30h", "20:00h"};

    String[] agendaFixa = new String[]{ "08:00h", "08:30h", "09:00h",
            "09:30h", "10:30h", "11:00h", "11:30h", "12:00h",
            "12:30h", "13:00h", "13:30h", "14:00h", "14:30h",
            "15:00h", "15:30h", "16:00h", "16:30h", "17:00h",
            "17:30h", "18:00h", "18:30h", "19:00h", "19:30h", "20:00h"};

    Spinner spnProfissional;
    EditText dataAgendamento;
    ListView horariosListView;
    Button btnAgendamento;
    String diaAgendamento;
    String horaAgendamento;
    ArrayAdapter<String> adapterHorario;
    ArrayList<String> horariosMarcados = new ArrayList<String>();
    TextView horarioTxt;

    private AlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.activity_agendamento_final, viewGroup, false);
        context = view.getContext();

        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        horarioTxt = view.findViewById(R.id.horarioTxt);
        spnProfissional = view.findViewById(R.id.spnProfissional);
        horariosListView = view.findViewById(R.id.horariosListView);
        dataAgendamento = view.findViewById(R.id.dataAgendamentoEditText);
        btnAgendamento = view.findViewById(R.id.buttonAgendamento);

        diaAgendamento = ((Activity) context).getIntent().getStringExtra("data_agendamento");
        final Adapter adapterProfissional = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, cabeleireiros);
        spnProfissional.setAdapter((SpinnerAdapter) adapterProfissional);
        spnProfissional.setFocusable(true);
        spnProfissional.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                horarioTxt.setText("Horários disponíveis!");
                horarioTxt.setTextSize(14);
                btnAgendamento.setEnabled(true);
                verificaAgendaDia(spnProfissional.getSelectedItem().toString(), diaAgendamento);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dataAgendamento.setText(diaAgendamento);
        dataAgendamento.setEnabled(false);

        btnAgendamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostraDialogConfirmacao();
            }
        });

        if (!SalaoVitinhoClienteUtils.isInternetIsConnected(context)) {
            SalaoVitinhoClienteUtils.exibeDialogConfirmacao(context, "Você não está conectado a Internet!\nNão é possível obter os horários disponíveis.", null);
            btnAgendamento.setVisibility(View.GONE);
        }

        return view;
    }

    private void montaAdapter() {
        List<String> listaHorarios = Arrays.asList(horarios);
        Collections.sort(listaHorarios);
        adapterHorario = new AdapterHorario(context, listaHorarios, horariosMarcados, DateUtils.parseData(diaAgendamento));


        horariosListView.setAdapter(adapterHorario);
        horariosListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                horaAgendamento = (String) horariosListView.getItemAtPosition(position);
            }
        });
    }

    private void verificaAgendaDia(final String profissional, final String diaAgendamento) {
        final String diaAgendamentoSemBarras = diaAgendamento.replace("/","_");

        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDA,
            profissional, diaAgendamentoSemBarras).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Agenda agendaDia = dataSnapshot.getValue(Agenda.class);
                if (agendaDia != null) {

                    horarioTxt.setText("Horários disponíveis!");
                    horarioTxt.setTextSize(14);
                    if (!agendaDia.isCancelada()) {
                        horariosListView.setVisibility(View.VISIBLE);
                        btnAgendamento.setVisibility(View.VISIBLE);
                        if (!agendaDia.isHoraPadrao()) {
                            montaHorariosIntervaloHorarioAtendimento(agendaDia);
                        }
                        buscaDadosBanco(profissional, diaAgendamento);
                    }
                    else if (agendaDia.isCancelada()){
                        if (horariosListView != null) {
                            horariosListView.setAdapter(null);
                        }
                        horarioTxt.setText("Não há horários disponíveis!");
                        horarioTxt.setTextSize(18);
                        horariosListView.setVisibility(View.GONE);
                        btnAgendamento.setVisibility(View.GONE);
                    }
                }
                else {
                    horariosListView.setVisibility(View.VISIBLE);
                    btnAgendamento.setVisibility(View.VISIBLE);
                    horarios = null;
                    horarios = agendaFixa;
                    buscaDadosBanco(profissional, diaAgendamento);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void montaHorariosIntervaloHorarioAtendimento(Agenda agenda) {
        List<String> horariosDisponiveis = new ArrayList<String>();
        if (agenda.getPrimeiraHoraInicio().compareTo(agenda.getPrimeiraHoraFim()) < 0 &&
                agenda.getSegundaHoraInicio().compareTo(agenda.getSegundaHoraFim()) < 0) {

            Calendar primeiraDataHoraInicio = retornaDataHoraAgendamento(agenda.getPrimeiraHoraInicio());
            Calendar primeiraDataHoraFim = retornaDataHoraAgendamento(agenda.getPrimeiraHoraFim());
            Calendar segundaDataHoraInicio = retornaDataHoraAgendamento(agenda.getSegundaHoraInicio());
            Calendar segundaDataHoraFim = retornaDataHoraAgendamento(agenda.getSegundaHoraFim());

            if (primeiraDataHoraInicio.getTimeInMillis() < primeiraDataHoraFim.getTimeInMillis() &&
                    segundaDataHoraInicio.getTimeInMillis() < segundaDataHoraFim.getTimeInMillis()) {

                while (primeiraDataHoraInicio.before(primeiraDataHoraFim)) {
                    adicionaHorarioDisponiveis(primeiraDataHoraInicio, horariosDisponiveis);
                }

                while (segundaDataHoraInicio.before(segundaDataHoraFim)) {
                    adicionaHorarioDisponiveis(segundaDataHoraInicio, horariosDisponiveis);
                }
            }
        }

        if (horariosDisponiveis.size() > 0) {
            horarios = null;
            horarios = horariosDisponiveis.toArray(new String[horariosDisponiveis.size()-1]);
        }
    }

    private Calendar retornaDataHoraAgendamento(String horaEscolhida) {
        Calendar diaHoraAtendimento = Calendar.getInstance(locale);
        String[] dataAgendada = diaAgendamento.split("/");
        diaHoraAtendimento.set(Integer.valueOf(dataAgendada[2]), Integer.valueOf(dataAgendada[1]) - 1, Integer.valueOf(dataAgendada[0]));
        diaHoraAtendimento.set(Calendar.HOUR_OF_DAY, Integer.valueOf(horaEscolhida.split(":")[0]));
        diaHoraAtendimento.set(Calendar.MINUTE, Integer.valueOf(horaEscolhida.split(":")[1]));
        diaHoraAtendimento.set(Calendar.SECOND, 0);

        return diaHoraAtendimento;
    }

    private void adicionaHorarioDisponiveis(Calendar horaAdicionar, List<String> vetorRecebedor) {
        String horaInicial = DateFormatter.formatHoraMinuto(horaAdicionar.getTime());
        vetorRecebedor.add(horaInicial + "h");
    }

    private void buscaDadosBanco(String profissional, String diaAgendamento) {
        FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO,
            profissional, SalaoVitinhoConstants.NAO_ATENDIDO,
            diaAgendamento.replace("/", "_")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                horariosMarcados = new ArrayList<>();
                if (dataSnapshot.hasChildren()) {

                    for (DataSnapshot diaAgendamento : dataSnapshot.getChildren()) {
                        Horario horario = diaAgendamento.getValue(Horario.class);

                        if (!horario.isDisponivel()) {
                            horariosMarcados.add(diaAgendamento.getKey());
                        }
                    }
                }
                montaAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Erro ao tentar obter os dados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostraDialogConfirmacao() {
        if (horaAgendamento != null ) {
            diaAgendamento = diaAgendamento.replace("_", "/");
            String mensagem = "Confirma o(s) agendamento(s) para " + diaAgendamento + " de " + horaAgendamento + "?";
            DialogInterface.OnClickListener acaoBotaoSim = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    diaAgendamento = diaAgendamento.replace("/", "_");
                    String profissional = (String) spnProfissional.getSelectedItem();
                    String nome = PreferencesUtils.getStringPrefsByKey(context, SalaoVitinhoConstants.PREF_NOME);
                    String telefone = PreferencesUtils.getStringPrefsByKey(context, SalaoVitinhoConstants.PREF_TELEFONE);

                    final Horario horario = new Horario(nome, telefone, profissional, diaAgendamento, horaAgendamento, false, false, false, false);
                    FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.AGENDAMENTO, profissional, SalaoVitinhoConstants.NAO_ATENDIDO, diaAgendamento, horaAgendamento).setValue(horario);
                    salvaInformacoesPreferences(horario);
                    SalaoVitinhoClienteUtils.exibeDialogConfirmacao(context, "Agendamento realizado com sucesso. Aguarde a confirmação do agendamento!", null);
                    getActivity().getIntent().putExtra("data_agendamento", diaAgendamento);
                    getFragmentManager().beginTransaction().detach(AgendamentoFinalFragment.this).attach(AgendamentoFinalFragment.this).commit();
                }
            };

            SalaoVitinhoClienteUtils.exibeDialogConfirmacao(context, mensagem, acaoBotaoSim, null);

        }
        else {
            Toast.makeText(context, "Escolha um horário para agendar.", Toast.LENGTH_LONG).show();
        }
    }

    private void salvaInformacoesPreferences(Horario horario) {
        AgendamentoDB database = new AgendamentoDB(context);
        database.salvar(horario);
    }

    private void apagaAgendamentoBanco(Horario horario) {
        AgendamentoDB database = new AgendamentoDB(context);
        database.deletar(horario);
    }
}
