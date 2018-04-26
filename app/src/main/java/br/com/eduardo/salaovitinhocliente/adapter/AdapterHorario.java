package br.com.eduardo.salaovitinhocliente.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.eduardo.salaovitinhocliente.utils.DateUtils;

/**
 * Created by eduardo.vasconcelos on 25/10/2017.
 */

public class AdapterHorario extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> horarios;
    private final List<String> horariosAgendados;
    private final Date dataAgendamento;

    @Override
    public int getCount() {
        return horarios != null ? horarios.size() : 0;
    }

    public AdapterHorario(Context context, List<String> horarios, List<String> horariosAgendados, Date diaAgendamento) {
        super(context, android.R.layout.simple_list_item_single_choice, horarios);
        this.context = context;
        this.horarios = horarios;
        this.horariosAgendados = horariosAgendados;
        this.dataAgendamento = diaAgendamento;
    }

    @Override
    public String getItem(int position) {
        return horarios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());

        convertView = inflater.inflate(android.R.layout.simple_list_item_single_choice, parent, false);
        CheckedTextView checkedTextView = (CheckedTextView) convertView.findViewById(android.R.id.text1);
        checkedTextView.setText(getItem(position));

        if (horariosAgendados.contains(horarios.get(position))) {
            checkedTextView.setClickable(false);
            checkedTextView.setPressed(false);
            checkedTextView.setEnabled(false);

            checkedTextView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    ((CheckedTextView) v).toggle();
                }
            });
        }

        String hora = getItem(position);

        Calendar horaAtualCalendar = Calendar.getInstance();
        horaAtualCalendar.add(Calendar.HOUR, 1);

        String hotaAtual = DateUtils.formatHora(horaAtualCalendar.getTime());

        if (dataAgendamento == null || horaAtualCalendar.get(Calendar.DAY_OF_MONTH) == (DateUtils.parseDia(dataAgendamento))) {
            if (hora.compareTo(hotaAtual) < 0) {
                checkedTextView.setClickable(false);
                checkedTextView.setPressed(false);
                checkedTextView.setEnabled(false);

                checkedTextView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        ((CheckedTextView) v).toggle();
                    }
                });
            }
        }

        return convertView;
    }
}

