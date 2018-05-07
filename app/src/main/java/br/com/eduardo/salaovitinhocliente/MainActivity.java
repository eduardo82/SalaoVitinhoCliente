package br.com.eduardo.salaovitinhocliente;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import br.com.eduardo.salaovitinhocliente.banco.AgendamentoDB;
import br.com.eduardo.salaovitinhocliente.banco.MensagemDB;
import br.com.eduardo.salaovitinhocliente.constants.SalaoVitinhoConstants;
import br.com.eduardo.salaovitinhocliente.model.Horario;
import br.com.eduardo.salaovitinhocliente.model.Mensagem;
import br.com.eduardo.salaovitinhocliente.model.Telefone;
import br.com.eduardo.salaovitinhocliente.utils.CircleTransform;
import br.com.eduardo.salaovitinhocliente.utils.FirebaseUtils;
import br.com.eduardo.salaovitinhocliente.utils.NotificacaoUtil;
import br.com.eduardo.salaovitinhocliente.utils.PreferencesUtils;
import br.com.eduardo.salaovitinhocliente.utils.SalaoVitinhoClienteUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private final Context context = this;
    private Activity activity = MainActivity.this;
    private String wantPermission = android.Manifest.permission.READ_PHONE_STATE;
    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth auth;

    private String[] caminho;
    private String numeroTelefoneUsuario;
    private String nomeUsuario;

    private AgendamentoDB db;
    private MensagemDB mensagemDB;

    TextView textViewNomeUsuario;
    TextView textViewEmailUsuario;
    ImageView imageViewUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View viewHeader = navigationView.getHeaderView(0);
        textViewEmailUsuario = viewHeader.findViewById(R.id.textViewEmailUsuario);
        textViewNomeUsuario = viewHeader.findViewById(R.id.textViewNomeUsuario);
        imageViewUsuario = viewHeader.findViewById(R.id.imageViewUsuario);

        auth = FirebaseAuth.getInstance();

        getFragmentManager().beginTransaction().replace(R.id.conteudo,
            new InicioBotoesFragment()).commit();

        if (auth.getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.GoogleBuilder().build())).build(), RC_SIGN_IN);
        } else {
            PreferencesUtils.removePref(context, SalaoVitinhoConstants.PREF_NOME);
            PreferencesUtils.putStringPrefs(context, SalaoVitinhoConstants.PREF_NOME, auth.getCurrentUser().getDisplayName());
            textViewEmailUsuario.setText(auth.getCurrentUser().getEmail());
            textViewNomeUsuario.setText(auth.getCurrentUser().getDisplayName());
            Picasso.with(this)
                    .load(auth.getCurrentUser().getPhotoUrl())
                    .error(R.mipmap.ic_launcher_round)
                    .resize(180, 180)
                    .transform(new CircleTransform())
                    .into(imageViewUsuario);
            verificaPermissao();
        }


    }

    private void verificaPermissao() {
        if (!checkPermission(wantPermission)) {
            PreferencesUtils.removePref(context);
            requestPermission(wantPermission);
        } else {
            numeroTelefoneUsuario = PreferencesUtils.getTelefone(this);
            nomeUsuario = PreferencesUtils.getUsuario(this);
            db = new AgendamentoDB(context);
            mensagemDB = new MensagemDB(context);

            if (numeroTelefoneUsuario != null && numeroTelefoneUsuario.length() > 8 && nomeUsuario != null && nomeUsuario.length() > 3) {

                FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.NODE_FIREBASE_TELEFONES, numeroTelefoneUsuario).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                            Telefone telefone = dataSnapshot.getValue(Telefone.class);

                            if (telefone.getAutorizado()) {
                                Bundle extras = getIntent().getExtras();

                                if (extras != null && extras.size() > 0) {
                                    if (extras.get("mensagem") != null) {
                                        getFragmentManager().beginTransaction().replace(R.id.conteudo,
                                                new DetalheMensagemFragment()).commit();
                                        NotificacaoUtil.cancelaNotificacao(context, 2);
                                    }
                                    else if (extras.get("marcacao") != null) {
                                        getFragmentManager().beginTransaction().replace(R.id.conteudo,
                                                new VisualizarAgendamentoFragment()).commit();
                                        NotificacaoUtil.cancelaNotificacao(context, 1);
                                    }
                                }
                            }
                            else {
                                exibeDialogUsuarioNaoAutorizado();
                            }
                        }
                        else {
                            exibeDialogUsuarioNaoAutorizado();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            else {
                SalaoVitinhoClienteUtils.exibeDialogInformacoesUsuario(context, true, true);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (PreferencesUtils.getTelefone(this).length() > 8) {
            FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.NODE_FIREBASE_TELEFONES, PreferencesUtils.getTelefone(this)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                        Telefone telefone = dataSnapshot.getValue(Telefone.class);

                        if (telefone.getAutorizado()) {
                            verificaAutorizacaoServico();
                            verificaMensagem();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void verificaAutorizacaoServico() {
        if (db != null) {
            List<Horario> agendamentosSolicitados = db.buscaTodosAgendamentos();
            if (agendamentosSolicitados != null && agendamentosSolicitados.size() > 0) {
                for (final Horario horario : agendamentosSolicitados) {
                    final String[] caminho = { SalaoVitinhoConstants.AGENDAMENTO, horario.getProfissional(), SalaoVitinhoConstants.NAO_ATENDIDO, horario.getDiaAtendimento(), horario.getHoraAtendimento()};
                    FirebaseUtils.getReferenceChild(caminho).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                                Horario resultado = dataSnapshot.getValue(Horario.class);
                                String mensagem;

                                if (resultado != null) {
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.putExtra("marcacao", true);

                                    if (resultado.isVerificado()) {
                                        if (resultado.isRecusado()) {

                                            Horario horarioBanco = db.buscaHorario(resultado);

                                            if (!horarioBanco.isRecusado()) {
                                                mensagem = "Seu agendamento no Salão do Vitinho foi recusado!";
                                                NotificacaoUtil.geraNotificacaoSimples(context, intent, "Mensagem",  mensagem , 1);
                                                FirebaseUtils.getReferenceChild(caminho).removeEventListener(this);
                                                db.atualizar(resultado);
                                            }

                                        }
                                        else {
                                            if (resultado.isAutorizado()) {

                                                Horario horarioBanco = db.buscaHorario(resultado);

                                                if (!horarioBanco.isAutorizado()) {
                                                    mensagem = "Seu agendamento no Salão do Vitinho foi autorizado!";
                                                    NotificacaoUtil.geraNotificacaoSimples(context, intent, "Mensagem",  mensagem , 1);
                                                    FirebaseUtils.getReferenceChild(caminho).removeEventListener(this);
                                                    db.atualizar(resultado);
                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(context, "Erro ao tentar obter seu agendamento. Verifique sua conexão com a Internet.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final int id = item.getItemId();
        numeroTelefoneUsuario = PreferencesUtils.getTelefone(this);
        nomeUsuario = PreferencesUtils.getUsuario(this);

        if (id == R.id.nav_sair) {
            finishAffinity();
        }

        if (numeroTelefoneUsuario != null && numeroTelefoneUsuario.length() > 8 && nomeUsuario != null && nomeUsuario.length() > 3) {
            FirebaseUtils.getReferenceChild(SalaoVitinhoConstants.NODE_FIREBASE_TELEFONES, numeroTelefoneUsuario).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                        Telefone telefone = dataSnapshot.getValue(Telefone.class);

                        if (telefone.getAutorizado()) {
                            FragmentManager fragmentManager = getFragmentManager();

                            if (id == R.id.nav_modificar_contato) {
                                SalaoVitinhoClienteUtils.exibeDialogInformacoesUsuario(context, true, true);
                            }
                            else if (id == R.id.nav_agendamentos) {
                                fragmentManager.beginTransaction().replace(R.id.conteudo,
                                        new AgendamentoFragment()).commit();
                            } else if (id == R.id.nav_visualizar_agendamentos) {
                                fragmentManager.beginTransaction().replace(R.id.conteudo,
                                        new VisualizarAgendamentoFragment()).commit();
                            } else if (id == R.id.nav_mensagem) {
                                fragmentManager.beginTransaction().replace(R.id.conteudo,
                                        new MensagemFragment()).commit();
                            }
                        }
                    }
                    else {
                        exibeDialogUsuarioNaoAutorizado();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            SalaoVitinhoClienteUtils.exibeDialogInformacoesUsuario(context, true, true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                textViewEmailUsuario.setText(auth.getCurrentUser().getEmail());
                textViewNomeUsuario.setText(auth.getCurrentUser().getDisplayName());
                Picasso.with(this)
                        .load(auth.getCurrentUser().getPhotoUrl())
                        .error(R.mipmap.ic_launcher_round)
                        .resize(180, 180)
                        .transform(new CircleTransform())
                        .into(imageViewUsuario);

                verificaPermissao();
            }
        }
    }

    private void verificaMensagem() {
        if (PreferencesUtils.getTelefone(context).length() > 8) {
            caminho = new String[] {SalaoVitinhoConstants.MENSAGEM, SalaoVitinhoConstants.VITINHO, PreferencesUtils.getTelefone(context)};
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Mensagem resultado = dataSnapshot.getValue(Mensagem.class);

                    if (resultado != null) {
                        final String MENSAGEM = "mensagem";
                        if (resultado.isLido()) {
                            if (mensagemDB.buscaMensagem(resultado.getResposta()).size() == 0) {

                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra(MENSAGEM, resultado.getMensagem());
                                NotificacaoUtil.geraNotificacaoSimples(context, intent,  SalaoVitinhoConstants.INFORMACAO,
                                        "Sua mensagem foi respondida.\n Resposta -> " + resultado.getResposta(), 2);
                                FirebaseUtils.getReferenceChild(caminho).removeEventListener(this);
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


    private String getPhone() {
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, wantPermission) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return phoneMgr.getLine1Number();
    }

    private void requestPermission(String permission){
        ActivityCompat.requestPermissions(activity, new String[]{permission},PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (getPhone() != null && getPhone().length() > 8) {
                        PreferencesUtils.putStringPrefs(context, SalaoVitinhoConstants.PREF_TELEFONE, getPhone().replace("+55", ""));
                        SalaoVitinhoClienteUtils.exibeDialogInformacoesUsuario(context, true, true);
                    }
                    else {
                        SalaoVitinhoClienteUtils.exibeDialogInformacoesUsuario(context, true, true);
                    }
                } else {
                    Toast.makeText(activity,"Permissão negada! Você não pode usar o aplicativo sem essa permissão.", Toast.LENGTH_LONG).show();
                    requestPermission(wantPermission);
                }
                break;
        }
    }

    private boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void exibeDialogUsuarioNaoAutorizado() {
        DialogInterface.OnClickListener acaoBotaoSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        };
        SalaoVitinhoClienteUtils.exibeDialogConfirmacao(activity, "Você não é autorizado a usar este aplicativo. Solicite ao Vitinho ou João Vitor a autorização.", acaoBotaoSim);
    }

    @Override
    public void onBackPressed()
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.conteudo, new InicioBotoesFragment()).commit();
    }
}