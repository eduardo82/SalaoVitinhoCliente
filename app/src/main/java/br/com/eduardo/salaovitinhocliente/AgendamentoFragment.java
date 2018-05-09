package br.com.eduardo.salaovitinhocliente;

import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;


public class AgendamentoFragment extends Fragment {
    View view;
    Context context;
    CalendarView calendar;
    String dataEscolhida;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_dia_agendamento, viewGroup, false);
        context = view.getContext();

        calendar = view.findViewById(R.id.calendar);
        calendar.setMinDate(new Date().getTime());

        Calendar maxDate = Calendar.getInstance();
        maxDate.setTime(new Date());
        maxDate.add(Calendar.DAY_OF_MONTH, 7);
        calendar.setMaxDate(maxDate.getTimeInMillis());

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                dataEscolhida = retornaValorMenorDez(dayOfMonth) + "/" + retornaValorMenorDez(month+1) + "/" + year;
                selecionaData();
            }
        });

        return view;
    }

    private String retornaValorMenorDez(int month) {
        if (month < 10) {
            return "0" + month;
        }
        return String.valueOf(month);
    }

    private void selecionaData() {
        getActivity().getIntent().putExtra("data_agendamento", dataEscolhida);
        getFragmentManager().beginTransaction().replace(R.id.conteudo,
            new AgendamentoFinalFragment()).commit();
    }
}
