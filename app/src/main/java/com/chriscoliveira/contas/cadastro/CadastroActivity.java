package com.chriscoliveira.contas.cadastro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.chriscoliveira.contas.AjustesActivity;
import com.chriscoliveira.contas.MainActivity;
import com.chriscoliveira.contas.MesAMesActivity;
import com.chriscoliveira.contas.R;
import com.chriscoliveira.contas.banco.Banco;
import com.chriscoliveira.contas.cartoes.CartaoHomeActivity;
import com.chriscoliveira.contas.despesa.DespesaActivity;
import com.chriscoliveira.contas.renda.RendaActivity;

@SuppressWarnings("deprecation")
public class CadastroActivity extends AppCompatActivity {
    Toolbar mToolbar;

    Banco banco = new Banco();

    Button btGravar;

    EditText etConta, etValor, etParcela, etDia, etMes, etAno;
    Spinner spTipo, spCategoria;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cadastrar_activity);

        ConstraintLayout layout = findViewById(R.id.layoutCadastro);
        layout.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

		/*
         * codigo pagina
		 */

        etConta = (EditText) findViewById(R.id.etConta);
        etValor = (EditText) findViewById(R.id.etValor);
        etParcela = (EditText) findViewById(R.id.etParcela);
        etDia = (EditText) findViewById(R.id.etDia);
        etMes = (EditText) findViewById(R.id.etMes);
        etAno = (EditText) findViewById(R.id.etAno);
        spTipo = (Spinner) findViewById(R.id.spTipo);
        spCategoria = (Spinner) findViewById(R.id.spCategoria);

        try {
            etDia.setText(""+banco.RetornaDia());
            etMes.setText(""+banco.RetornaMes());
            etAno.setText(""+banco.RetornaAno());
        }
        catch (Exception e){
            Log.i("erro","E: "+e);
        }
        // Get reference of widgets from XML layout

        spinnerTipo();



        btGravar = (Button) findViewById(R.id.btGravar);
        btGravar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etParcela.getText().toString().equals("")){
                    etParcela.setText("1");
                }
                banco.cadastarNovo(CadastroActivity.this, etConta.getText().toString(), etValor.getText().toString(),
                        etParcela.getText().toString(), etDia.getText().toString(), etMes.getText().toString(),
                        etAno.getText().toString(), spTipo.getSelectedItem().toString(),
                        spCategoria.getSelectedItem().toString());
                etConta.setText("");
                etValor.setText("");
                etParcela.setText("");
                etConta.setFocusable(true);
                etConta.requestFocus();
            }
        });

		/*
		 * fim do codigo
		 */

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Cadastrar");



        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void spinnerTipo(){
        final Spinner spinnerTipo = (Spinner) findViewById(R.id.spTipo);
        String[] Tipo = new String[]{"Pagar", "Receber"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinneritem, Tipo);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinneritem);
        spinnerTipo.setAdapter(spinnerArrayAdapter);

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerCategoria(spinnerTipo.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void spinnerCategoria(String spinnertipo) {
        Spinner spinnerCategoria = (Spinner) findViewById(R.id.spCategoria);

        

         String[] CategoriaPagar = new String[]{"Casa","Carro","Lazer","Saude","Itau","Bradesco","Caixa","Nubank","Hipercard","Pernambucanas","Marisa","CeA","Riachuelo","Outros"};




        String[] CategoriaReceber = new String[]{"Salario", "13 Salario", "Ferias"};
        if (spinnertipo.equals("Pagar")) {
            ArrayAdapter<String> spinner1ArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinneritem, CategoriaPagar);
            spinner1ArrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            spinnerCategoria.setAdapter(spinner1ArrayAdapter);
        } else {
            ArrayAdapter<String> spinner1ArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinneritem, CategoriaReceber);
            spinner1ArrayAdapter.setDropDownViewResource(R.layout.spinneritem);
            spinnerCategoria.setAdapter(spinner1ArrayAdapter);
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
        item.getItemId();

        switch (item.getItemId()) {

            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;

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
                startActivity(new Intent(this, MesAMesActivity.class));
                finish();
                break;
            case R.id.action_settings:
                startActivity(new Intent(CadastroActivity.this, AjustesActivity.class));
                finish();
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();

    }

}
