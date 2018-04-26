package br.com.eduardo.salaovitinhocliente;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import br.com.eduardo.salaovitinhocliente.banco.MensagemDB;
import br.com.eduardo.salaovitinhocliente.constants.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinhocliente.model.Mensagem;
import br.com.eduardo.salaovitinhocliente.utils.FirebaseUtils;
import br.com.eduardo.salaovitinhocliente.utils.PreferencesUtils;
import br.com.eduardo.salaovitinhocliente.utils.SalaoVitinhoClienteUtils;

public class DetalheMensagemFragment extends Fragment {

    String[] caminho;
    ScrollView scrollView;
    View view;
    Context context;
    LinearLayout linearLayout;
    LinearLayout layoutMensagem;
    Button btnCriarMensagem;
    MensagemDB mensagemDB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        view = layoutInflater.inflate(R.layout.fragment_mensageiro, viewGroup, false);
        context = view.getContext();

        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutMensageiro);
        caminho = new String[] {SalaoVitinhoConstants.MENSAGEM, SalaoVitinhoConstants.VITINHO, PreferencesUtils.getTelefone(context)};
        mensagemDB = new MensagemDB(context);
        manipulaComponentesActivity();
        observerMensagemUsuario();

        scrollToDown();
        return view;
    }

    private void scrollToDown() {
        scrollView = (ScrollView) view.findViewById(R.id.scrollMensagem);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void manipulaComponentesActivity() {
        List<Mensagem> mensagems = mensagemDB.buscaTodosAgendamentos();

        if (mensagems != null && mensagems.size() > 0) {
            for (Mensagem mensagem : mensagems) {
                if (mensagem.getMensagem() != null && mensagem.getMensagem().length() > 0) {
                    preencheComponenteMensagemUsuario(false, mensagem.getMensagem(), mensagem.getId());
                }
                else {
                    preencheComponenteMensagemUsuario(true, mensagem.getResposta(), mensagem.getId());
                }
            }
        }
        else {
            Toast.makeText(context, "Você não possui mensagens.", Toast.LENGTH_SHORT).show();
        }

        layoutMensagem = (LinearLayout) view.findViewById(R.id.linear_mmensagem);
        layoutMensagem.requestFocus();
        btnCriarMensagem = layoutMensagem.findViewById(R.id.btnCriarMensagem);
        btnCriarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText mensagemMensageiro = (EditText) layoutMensagem.findViewById(R.id.editTextMensagem);

                if (mensagemMensageiro.getText().length() > 0) {
                    Mensagem mensagem = new Mensagem(mensagemMensageiro.getText().toString(),
                            PreferencesUtils.getUsuario(context), PreferencesUtils.getTelefone(context), "", false);

                    FirebaseUtils.getReferenceChild(caminho).setValue(mensagem);

                    mensagemDB.salvar(mensagem);
                    mensagemMensageiro.setText("");
                    getFragmentManager().beginTransaction().detach(DetalheMensagemFragment.this)
                        .attach(DetalheMensagemFragment.this).commit();
                    scrollToDown();
                }
            }
        });
    }

    private void preencheComponenteMensagemUsuario(boolean isResposta, String mensagem, Long id) {
        View view;
        if (isResposta) {
            view = preencheTextViewMensagem(R.layout.layout_textview_received_include, R.id.textViewMsgResposta, mensagem, id);
        }
        else {
            view = preencheTextViewMensagem(R.layout.layout_textview_sent_include, R.id.textViewMsgMensageiro, mensagem, id);
        }

        linearLayout.addView(view);
    }

    @NonNull
    private View preencheTextViewMensagem(int idLayout, int idTextView, final String mensagem, final Long idMensagem) {
        View view;
        TextView textView;
        view = LayoutInflater.from(context).inflate(idLayout, null);
        textView = (TextView) view.findViewById(idTextView);
        textView.setText(mensagem);
        textView.setId(linearLayout.getChildCount() + 1);

        final DialogInterface.OnClickListener botaoSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mensagemDB.deletar(idMensagem);
                getFragmentManager().beginTransaction().detach(DetalheMensagemFragment.this)
                    .attach(DetalheMensagemFragment.this).commit();
            }
        };
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                SalaoVitinhoClienteUtils.exibeDialogConfirmacao(context, "Confirma apagar a mensagem?", botaoSim, null);
                return false;
            }
        });
        return view;
    }

    private void observerMensagemUsuario() {

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Mensagem resultado = dataSnapshot.getValue(Mensagem.class);

                if (resultado != null) {
                    if (resultado.isLido()) {
                        if (mensagemDB.buscaMensagem(resultado.getResposta()).size() == 0) {
                            FirebaseUtils.getReferenceChild(caminho).removeEventListener(this);
                            resultado.setMensagem(null);
                            mensagemDB.salvar(resultado);

                            if (getFragmentManager() != null) {
                                getFragmentManager().beginTransaction().detach(DetalheMensagemFragment.this)
                                        .attach(DetalheMensagemFragment.this).commitAllowingStateLoss();
                            }
                            scrollToDown();
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        FirebaseUtils.getReferenceChild(caminho).addValueEventListener(listener);
    }
}
