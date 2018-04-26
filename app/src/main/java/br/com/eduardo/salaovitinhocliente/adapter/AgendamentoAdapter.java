package br.com.eduardo.salaovitinhocliente.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.eduardo.salaovitinhocliente.R;
import br.com.eduardo.salaovitinhocliente.model.Horario;


/**
 * Created by eduardo.vasconcelos on 09/11/2017.
 */

public class AgendamentoAdapter extends ArrayAdapter<Horario> {

    private final Context context;
    private final List<Horario> horariosAgendados;

    public ArrayList<Horario> getHorariosEscolhidos() {
        return horariosEscolhidos;
    }

    public void setHorariosEscolhidos(ArrayList<Horario> horariosEscolhidos) {
        this.horariosEscolhidos = horariosEscolhidos;
    }

    private ArrayList<Horario> horariosEscolhidos = new ArrayList<>();

    public AgendamentoAdapter(@NonNull Context context, @LayoutRes int id, @NonNull ArrayList<Horario> objects) {
        super(context, id, objects);
        this.context = context;
        this.horariosAgendados = objects;
    }

    @Override
    public int getCount() {
        return horariosAgendados != null ? horariosAgendados.size() : 0;
    }

    @Override
    public Horario getItem(int position) {
        return horariosAgendados.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_adapter_agendamentos, parent, false);
        final TextView dataHora = (TextView) view.findViewById(R.id.textViewDataHoraSolicitada);
        final TextView solicitante = (TextView) view.findViewById(R.id.textViewSolicitante);
        final CheckBox checkedTextView = (CheckBox) view.findViewById(R.id.checkBoxAcao);
        final ImageView status = (ImageView) view.findViewById(R.id.imageViewStatus);

        final Horario item = getItem(position);

        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 35, 3, 0);

        String horaAtendimento = "";
        if (item.getHoraAtendimento() != null) {
            horaAtendimento = item.getHoraAtendimento().split(" ")[0];
        }

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, context.getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 29, context.getResources().getDisplayMetrics());

        if (item.isRecusado()) {
            layoutParams=new LinearLayout.LayoutParams(width, height);
            layoutParams.setMargins(0, 30, 20, 0);
            status.setImageResource(R.drawable.cancel_icon);
            status.setLayoutParams(layoutParams);
            checkedTextView.setVisibility(View.GONE);
            checkedTextView.setClickable(false);
        }
        else if (item.isAutorizado()) {
            layoutParams=new LinearLayout.LayoutParams(width, height);
            layoutParams.setMargins(0, 30, 20, 0);
            status.setLayoutParams(layoutParams);
            checkedTextView.setVisibility(View.GONE);
            checkedTextView.setClickable(false);
        }
        else if (!item.isAutorizado()) {
            status.setVisibility(View.GONE);
            checkedTextView.setVisibility(View.VISIBLE);
            checkedTextView.setLayoutParams(layoutParams);
            checkedTextView.setClickable(true);
        }

        dataHora.setText(item.getDiaAtendimento().replace("_", "/") + " - " +  horaAtendimento);
        solicitante.setText("Profissional: " + item.getProfissional());
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedTextView.isChecked()) {
                    horariosEscolhidos.add(item);
                    checkedTextView.setChecked(true);
                }
                else {
                    checkedTextView.setChecked(false);
                    horariosEscolhidos.remove(item);
                }
            }
        });

        return view;
    }

    public List<Horario> getHorariosAgendados() {
        return horariosAgendados;
    }
}
