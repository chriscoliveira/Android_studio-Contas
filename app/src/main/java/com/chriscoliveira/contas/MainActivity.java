package com.chriscoliveira.contas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chriscoliveira.contas.banco.Banco;
import com.chriscoliveira.contas.banco.ZUtilitarios;
import com.chriscoliveira.contas.cadastro.CadastroActivity;
import com.chriscoliveira.contas.cartoes.CartaoHomeActivity;
import com.chriscoliveira.contas.despesa.DespesaActivity;
import com.chriscoliveira.contas.login.ConfiguracaoFirebase;
import com.chriscoliveira.contas.renda.RendaActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
    Toolbar mToolbar;
    Cursor cursor;
    Banco banco = new Banco();
    ZUtilitarios zutil = new ZUtilitarios();
    TextView tvRSaldo;
    private FirebaseAuth auth;

    protected SQLiteDatabase bancoDados = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);

        auth= ConfiguracaoFirebase.getFirebaseAutenticacao();
		/*
         * codigo exibir banco dados
		 */

        banco.AbreCriaBanco(MainActivity.this);
        int mes = banco.RetornaMes();
        int ano = banco.RetornaAno();
        int dia = banco.RetornaDia();

        final TextView tvmes, tvano, tvmesextenso;
        tvmes = (TextView) findViewById(R.id.tvMesFiltro);
        tvmesextenso = (TextView) findViewById(R.id.tvMesExtenso);
        tvano = (TextView) findViewById(R.id.tvAnoFiltro);
        tvmes.setText(mes + "");
        tvmesextenso.setText(zutil.DataExtenso(mes) + "");
        tvano.setText(ano + "");

        TextView tvData = (TextView) findViewById(R.id.tvData);
        tvData.setText(dia + " de " + zutil.DataExtenso(mes) + " de " + ano);

        tvRSaldo = (TextView) findViewById(R.id.tvSaldo1);

        ImageButton btVoltaMes, btAvancaMes;
        btVoltaMes = (ImageButton) findViewById(R.id.btVoltames);
        btAvancaMes = (ImageButton) findViewById(R.id.btAvancaMes);
        btVoltaMes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {

                    if (Integer.parseInt(tvmes.getText().toString()) <= 1) {
                        tvmes.setText("12");
                        tvano.setText(""
                                + (Integer.parseInt(tvano.getText().toString()) - 1));
                        banco.SomaExibeMainActivity(MainActivity.this, tvmes
                                .getText().toString(), tvano.getText()
                                .toString());
                        String valor = tvRSaldo.getText().toString();
                        MudaCorAlerta(valor);
                    } else {
                        tvmes.setText(""
                                + (Integer.parseInt(tvmes.getText().toString()) - 1));
                        banco.SomaExibeMainActivity(MainActivity.this, tvmes
                                .getText().toString(), tvano.getText()
                                .toString());
                        String valor = tvRSaldo.getText().toString();
                        MudaCorAlerta(valor);
                    }
                } catch (Exception e) {
                    zutil.toast(MainActivity.this, "Erro " + e);
                }
                tvmesextenso.setText("" + zutil.DataExtenso(Integer.parseInt(tvmes.getText().toString())));
            }
        });

        btAvancaMes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {

                    if (Integer.parseInt(tvmes.getText().toString()) >= 12) {
                        tvmes.setText("1");
                        tvano.setText(""
                                + (Integer.parseInt(tvano.getText().toString()) + 1));
                        banco.SomaExibeMainActivity(MainActivity.this, tvmes
                                .getText().toString(), tvano.getText()
                                .toString());
                        String valor = tvRSaldo.getText().toString();
                        MudaCorAlerta(valor);
                    } else {
                        tvmes.setText(""
                                + (Integer.parseInt(tvmes.getText().toString()) + 1));
                        banco.SomaExibeMainActivity(MainActivity.this, tvmes
                                .getText().toString(), tvano.getText()
                                .toString());
                        String valor = tvRSaldo.getText().toString();
                        MudaCorAlerta(valor);
                    }
                } catch (Exception e) {
                    zutil.toast(MainActivity.this, "Erro " + e);
                }
                tvmesextenso.setText("" + zutil.DataExtenso(Integer.parseInt(tvmes.getText().toString())));

            }
        });

        banco.SomaExibeMainActivity(this, tvmes.getText().toString() + "", tvano.getText().toString() + "");
        String valor = tvRSaldo.getText().toString();
        MudaCorAlerta(valor);

        //TODO

        try {
            Date buildDate = new Date(BuildConfig.BUILD_TIME);

            TextView tvVersao = (TextView) findViewById(R.id.tvVersao);

            tvVersao.setText("" + buildDate);
        } catch (Exception e) {
            Log.i("datacompilacao", "Erro " + e);
        }

		/*
         * fim do codigo
		 */

        mToolbar.setTitle("Minhas");
        mToolbar.setSubtitle("Contas 2019");



        setSupportActionBar(mToolbar);

    }

    public void MudaCorAlerta(String valor) {

        valor = valor.replaceAll(",", ".");
        double saldo;

        saldo = Double.parseDouble(valor);

        if (saldo < 0) {
            mToolbar.setBackgroundResource(R.drawable.toolbar_despesa);
        } else {
            mToolbar.setBackgroundResource(R.drawable.toolbar_inicio);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // int id = item.getItemId();
        switch (item.getItemId()) {

            case R.id.action_rendas:
                startActivity(new Intent(this, RendaActivity.class));
                finish();
                break;
            case R.id.action_despesas:
                startActivity(new Intent(this, DespesaActivity.class));
                finish();
                break;
            case R.id.action_cartao:
                startActivity(new Intent(this, CartaoHomeActivity.class));
                finish();
                break;
            case R.id.action_export_email:
                banco.CriaListaParaExporacao(this);
                break;
            case R.id.action_novo:
                startActivity(new Intent(this, CadastroActivity.class));
                finish();
                break;
            case R.id.action_resumo:
                startActivity(new Intent(MainActivity.this, MesAMesActivity.class));
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, AjustesActivity.class));
                finish();
                break;
            case R.id.action_sair:
                deslogarUsuario();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mensagemSaida(this, "Sair da aplicacao", "Deseja sair?");
    }

    public void mensagemSaida(Activity activity, String titulo, String mensagem) {
        AlertDialog.Builder CaixaAlerta = new AlertDialog.Builder(activity);
        CaixaAlerta.setMessage(mensagem);
        CaixaAlerta.setTitle(titulo);
        CaixaAlerta.setPositiveButton("SIM",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                });
        CaixaAlerta.setNegativeButton("NAO", null);
        CaixaAlerta.show();

    }

    public void deslogarUsuario(){
        try{
            auth.signOut();
            finish();

        }catch (Exception e ){
            e.printStackTrace();

        }
    }


}
