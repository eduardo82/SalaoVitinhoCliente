package br.com.eduardo.salaovitinhocliente.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import br.com.eduardo.salaovitinhocliente.R;
import br.com.eduardo.salaovitinhocliente.constants.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinhocliente.formatter.BrPhoneNumberFormatter;

/**
 * Created by Eduardo on 04/12/2017.
 */

public class SalaoVitinhoClienteUtils {

    private SalaoVitinhoClienteUtils() {

    }


    public static void exibeDialogConfirmacao(Context context, String mensagem, DialogInterface.OnClickListener acaoBotaoSim, DialogInterface.OnClickListener acaoBotaoNao) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmação");
        builder.setIcon(R.drawable.ok_icon);
        builder.setMessage(mensagem);
        builder.setPositiveButton("Sim", acaoBotaoSim);
        builder.setNegativeButton("Não", acaoBotaoNao);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void exibeDialogConfirmacao(Context context, String mensagem, DialogInterface.OnClickListener acaoBotaoSim) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Mensagem");
        builder.setIcon(R.drawable.ok_icon);
        builder.setMessage(mensagem);
        builder.setPositiveButton("OK", acaoBotaoSim);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void exibeDialogInformacoesUsuario(final Context context, boolean exibeSim, boolean exibeNao) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_dados_usuario, null);

        final EditText nome = view.findViewById(R.id.editTextNome);

        final EditText telephone = view.findViewById(R.id.editTextTelefone);
        BrPhoneNumberFormatter addLineNumberFormatter = new BrPhoneNumberFormatter(new WeakReference<>(telephone));
        telephone.addTextChangedListener(addLineNumberFormatter);

        nome.setText(PreferencesUtils.getUsuario(context));
        telephone.setText(PreferencesUtils.getTelefone(context));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (exibeSim) {
            builder.setTitle("Insira suas informações");
        }

        builder.setView(view);

        if (exibeSim) {
            builder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }

        if (exibeNao) {
            builder.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();

        if (exibeSim) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nome = ((EditText) view.findViewById(R.id.editTextNome)).getText().toString();
                    String telefone = ((EditText) view.findViewById(R.id.editTextTelefone)).getText().toString();

                    String nomePref = PreferencesUtils.getStringPrefsByKey(context, SalaoVitinhoConstants.PREF_NOME);

                    if (nomePref.length() > 3 || nome.length() > 3) {
                        PreferencesUtils.putStringPrefs(context, SalaoVitinhoConstants.PREF_NOME, nome);
                        ((EditText) view.findViewById(R.id.editTextNome)).setText(nome.length() > 3 ? nome : nomePref);
                        if (telefone.length() > 0 && telefone.matches(".(31.)\\s9[7-9][0-9]{3}-[0-9]{4}")) {
                            PreferencesUtils.removePref(context, SalaoVitinhoConstants.PREF_TELEFONE);
                            PreferencesUtils.putStringPrefs(context, SalaoVitinhoConstants.PREF_TELEFONE, telefone);
                            dialog.dismiss();
                        }
                        else {
                            Toast.makeText(context, "O telefone deve ser válido e estar no formato (31) 99999-9999.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(context, "O nome deve ter pelo menos 3 letras.", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

    public static void insereMensagemLayout(Context context, LinearLayout linearLayout, String mensagemInserida) {
        linearLayout.removeAllViews();
        TextView et = new TextView(context);
        et.setText(mensagemInserida);
        et.setPadding(0, 100, 0, 100);
        et.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        et.setTextSize(20);
        linearLayout.addView(et);
    }

    public static boolean isInternetIsConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;

            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }
}