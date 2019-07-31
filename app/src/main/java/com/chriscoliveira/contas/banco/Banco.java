package com.chriscoliveira.contas.banco;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chriscoliveira.contas.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;


public class Banco extends Activity {

    protected SQLiteDatabase bancoDados = null;
    public static String NOME_BANCO = "MinhasContas";
    public static String NOME_TABELA_CONTAS = "Contas";
    public static String NOME_TABELA_CATEGORIAS = "Categorias";
    public static String ID = "_id";
    public static String CONTA = "conta";
    public static String VALOR = "valor";
    public static String ANO = "ano";
    public static String MES = "mes";
    public static String DIA = "dia";
    public static String PARCELA = "parcela";
    public static String SITUACAO = "situacao";
    public static String TIPO = "tipo";
    public static String CATEGORIA = "categoria";


    //ListView MostraDados;
    //SimpleCursorAdapter adapterLista;
    Cursor cursor;
    String Valores = "";
    Dialog dialog;
    CheckBox cbSituacao;
    Button btAcaoDialog, btAcaoApagar;
    String txtcb;
    Toolbar mToolbar;


    int diA = RetornaDia();
    int meS = RetornaMes();
    int anO = RetornaAno();

    /*
     *
     * TODO cria o banco de dados
     *
     */

    SQL sqlClass = new SQL();

    public void AbreCriaBanco(Activity activity) {


        // uso do context devido a classe ser simples e nao poder
        // executar o comando em outra classe
        bancoDados = activity.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
        bancoDados.execSQL(sqlClass.criaTabela);
        bancoDados.execSQL(sqlClass.criaTabelaListadeCategorias);
        bancoDados.close();

    }

    /*
     * TODO abre a conexao com o banco
     */

    public void AbreBanco(Activity activity) {
        bancoDados = activity.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
    }

    /*
     * TODO Fecha a conexao com o banco
     */

    public void FechaBanco() {
        bancoDados.close();
    }

    /*
     * TODO cadastro de novos registros
     */

    public void cadastarNovo(final Activity activity, String conta, String valor, String parcela, String dia,
                             String mes, String ano, String tipo, String categoria) {
        AbreBanco(activity);

        if (parcela.equals(""))
            parcela = "1";
        if (parcela.equals("0"))
            parcela = "1";
        if (valor.equals(""))
            valor = "1";
        if (valor.equals("0"))
            valor = "1";

        ContentValues contentValuesCampos = new ContentValues();
        Integer Parcela = Integer.parseInt(parcela), Mes = Integer.parseInt(mes), Ano = Integer.parseInt(ano);
        String parcelas;

        int anual = Ano, mensal = Mes;
        for (int i = 0; i < Parcela; i++) {
            int numeroParcela = i + 1;
            parcelas = "Parcela " + numeroParcela + "-" + parcela;
            if (i != 0) {
                if (mensal == 0) {
                    mensal = Mes + numeroParcela - 1;
                    anual = Ano;
                } else
                    mensal++;
            }
            if (mensal == 13) {
                mensal = 1;
                anual++;
            }

            contentValuesCampos.put(CONTA, conta);
            contentValuesCampos.put(VALOR, valor);
            contentValuesCampos.put(PARCELA, parcelas);
            contentValuesCampos.put(ANO, anual);
            contentValuesCampos.put(DIA, dia);
            contentValuesCampos.put(MES, mensal);
            contentValuesCampos.put(TIPO, tipo);
            contentValuesCampos.put(CATEGORIA, categoria);
            contentValuesCampos.put(SITUACAO, "_");

            try {
                bancoDados.insert(NOME_TABELA_CONTAS, null, contentValuesCampos);
                Toast.makeText(activity, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(activity, "Erro ao efetuar o cadastro: " + e, Toast.LENGTH_SHORT).show();
            }

        }
    }

    /*
     * TODO Atualiza reg
     */

    public void atualiza(Activity activity, String tabela, String conta, String categoria, String valor, String parcela,
                         String dia, String mes, String ano, String situacao, String id, String tipo) {
        // Log.i("aviso", "atz");

        String texto = ID + " = " + id;
        AbreBanco(activity);
        ContentValues contentValuesCampos = new ContentValues();
        contentValuesCampos.put(CONTA, conta);
        contentValuesCampos.put(CATEGORIA, categoria);
        contentValuesCampos.put(VALOR, valor);
        contentValuesCampos.put(PARCELA, parcela);
        contentValuesCampos.put(ANO, ano);
        contentValuesCampos.put(DIA, dia);
        contentValuesCampos.put(MES, mes);
        contentValuesCampos.put(SITUACAO, situacao);
        contentValuesCampos.put(TIPO, tipo);
        bancoDados.update(tabela, contentValuesCampos, texto, null);
        FechaBanco();


    }

    /*
     * TODO deleta
     */
    public void delete(Activity activity, String tabela, int id, String tipo) {
        try {
            String texto = ID + " = " + id;
            AbreBanco(activity);
            bancoDados.delete(tabela, texto, null);
            FechaBanco();
            carregaDados(activity, tipo, "nao", "", "");

            //int dia = RetornaDia();
            int mes = RetornaMes();
            int ano = RetornaAno();

            SomaExibe(activity, mes + "", ano + "");
        } catch (Exception e) {
            // Log.i("aviso", "erro " + e);
        }
    }

    /*
     * TODO Verifica a query e exibe o retorno na tela
     */

    private boolean VerificaRegistro(Activity activity, String tipo, String cartaoNaoOuQual, String vMes, String vAno) {
        int mes = 0, ano = 0;
        try {
            if (vMes.equals("")) {

                mes = RetornaMes();
                ano = RetornaAno();
            }
            if (!vMes.equals("")) {
                mes = Integer.parseInt(vMes);
                ano = Integer.parseInt(vAno);
            }
            AbreBanco(activity);

            if (cartaoNaoOuQual.equals("Itau")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "='Itau'",
                        null, null, null, SITUACAO + " ASC, " + DIA + " ASC");
            }
            if (cartaoNaoOuQual.equals("Bradesco")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "='Bradesco'",
                        null, null, null, SITUACAO + " ASC, " + DIA + " ASC");
            }
            if (cartaoNaoOuQual.equals("Caixa")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "='Caixa'",
                        null, null, null, SITUACAO + " ASC, " + DIA + " ASC");
            }
            if (cartaoNaoOuQual.equals("Marisa")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "='Marisa'",
                        null, null, null, SITUACAO + " ASC, " + DIA + " ASC");
            }

            if (cartaoNaoOuQual.equals("Cea")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "='Cea'",
                        null, null, null, SITUACAO + " ASC, " + DIA + " ASC");
            }

            if (cartaoNaoOuQual.equals("Riachuelo")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "='Riachuelo'",
                        null, null, null, SITUACAO + " ASC, " + DIA + " ASC");
            }
            if (cartaoNaoOuQual.equals("Nubank")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "='Nubank'",
                        null, null, null, SITUACAO + " ASC, " + DIA + " ASC");
            }

            if (cartaoNaoOuQual.equals("Hipercard")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "='Hipercard'",
                        null, null, null, SITUACAO + " ASC, " + DIA + " ASC");
            }

            if (cartaoNaoOuQual.equals("Pernambucanas")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "='Pernambucanas'",
                        null, null, null, SITUACAO + " ASC, " + DIA + " ASC");
            }

            if (cartaoNaoOuQual.equals("nao")) {
                cursor = bancoDados.query(
                        NOME_TABELA_CONTAS, null, MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
                                + "='" + tipo + "' AND " + CATEGORIA + "!='Itau' AND " + CATEGORIA + " !='Nubank' AND "
                                + CATEGORIA + " !='Bradesco' AND " + CATEGORIA + " !='Caixa' AND " + CATEGORIA + " !='Marisa' AND "
                                + CATEGORIA + " !='Riachuelo' AND " + CATEGORIA + " !='Cea' AND " + CATEGORIA + " !='Hipercard' AND "
                                + CATEGORIA + "!='Pernambucanas'",null, null, null, SITUACAO + " ASC, " + DIA + " ASC");

            }


            /*
            + "' AND " + ()
             */
            if (cursor.getCount() != 0) // se existir registro
            {
                cursor.moveToFirst(); // movimenta para o 1º registro
                return true;
            } else
                return false;

        } catch (Exception er) {
            return false;
        } finally {
            FechaBanco();
        }

    }

    /*
     * TODO Carrega os dados para o listview com base no mes e ano consultado. *
     */

    @SuppressWarnings("deprecation")
    public void carregaDados(Activity activity, String tipo, String cartaoNaoOuQual, String mes, String ano) {

        ListView MostraDados = (ListView) activity.findViewById(R.id.lvListagem);
        if (VerificaRegistro(activity, tipo, cartaoNaoOuQual, mes, ano)) {
            SimpleCursorAdapter adapterLista = new SimpleCursorAdapter(activity, R.layout.tela_listagem_itens, cursor,
                    new String[]{ID, CONTA, VALOR, SITUACAO, CATEGORIA, PARCELA, DIA, MES, ANO},
                    new int[]{R.id.tvId, R.id.tvConta, R.id.tvValor, R.id.tvPago, R.id.tvCategoria, R.id.tvParcel,
                            R.id.tvDia, R.id.tvMes, R.id.tvAno});
            MostraDados.setAdapter(adapterLista); // executa a ação

        } else {
            MostraDados.setAdapter(null);

        }

    }

    @SuppressLint("DefaultLocale")
    public void SomaExibe(Activity activity, String mes, String ano) {
        String valorP, valorR, valorF, valorC;
        double totalP = 0, totalR = 0, totalS, totalF = 0, totalC = 0;
        AbreBanco(activity);
        // Pagar
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + mes + "'", null, null, null, null);
        while (cursor.moveToNext()) {
            valorP = cursor.getString(cursor.getColumnIndex(VALOR));
            valorP = valorP.replaceAll(",", ".");
            totalP += Double.parseDouble(valorP);
        }
        TextView tvRPagar = (TextView) activity.findViewById(R.id.tvPagar);
        tvRPagar.setText("Pagar R$ " + String.format("%.2f", totalP));

        // falta Pagar
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + mes + "' and situacao ='_'", null, null, null,
                null);
        while (cursor.moveToNext()) {
            valorF = cursor.getString(cursor.getColumnIndex(VALOR));
            valorF = valorF.replaceAll(",", ".");
            totalF += Double.parseDouble(valorF);
        }
        TextView tvFalta = (TextView) activity.findViewById(R.id.tvFalta);
        tvFalta.setText("Falta R$" + String.format("%.2f", totalF));

        // Cartao

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                "("+CATEGORIA + "='Itau' OR " + CATEGORIA + " ='Nubank' OR "
                        + CATEGORIA + " ='Bradesco' OR " + CATEGORIA + " ='Caixa' OR " + CATEGORIA + " ='Marisa' OR "
                        + CATEGORIA + " ='Riachuelo' OR " + CATEGORIA + " ='Cea' OR " + CATEGORIA + " ='Hipercard' OR "
                        + CATEGORIA + "='Pernambucanas') and ano ='" + ano + "' and mes='" + mes + "'",
                null, null, null, null);
                //null, null, null, null);
        while (cursor.moveToNext()) {
            valorC = cursor.getString(cursor.getColumnIndex(VALOR));

            valorC = valorC.replaceAll(",", "."); // troca a , por .

            totalC += Double.parseDouble(valorC);

        }
        TextView tvCartao = (TextView) activity.findViewById(R.id.tvItau);

        tvCartao.setText("Cartão R$ "+String.format("%.2f", totalC));

        // Receber
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Receber' AND ano ='" + ano + "' and mes='" + mes + "'", null, null, null, null);
        while (cursor.moveToNext()) {
            valorR = cursor.getString(cursor.getColumnIndex(VALOR));
            valorR = valorR.replaceAll(",", "."); // troca a , por .

            totalR += Double.parseDouble(valorR);

        }
        TextView tvRReceber = (TextView) activity.findViewById(R.id.tvReceber);
        tvRReceber.setText("Receber R$" + String.format("%.2f", totalR));


        TextView tvSaldo = (TextView) activity.findViewById(R.id.tvSaldo1);
        tvSaldo.setText("Saldo R$" + String.format("%.2f", totalR-totalP));



        FechaBanco();

    }

    /*
     * TODO soma e exibe o mes a mes
     */

    @SuppressLint("DefaultLocale")
    public void SomaExibeMM(Activity activity, String ano) {

        AbreBanco(activity);

        String ValorPagar[] = new String[15], ValorReceber[] = new String[15];
        double TotalPagar[] = new double[15], TotalReceber[] = new double[15];
        TextView tvRPagar1,tvRPagar2,tvRPagar3,tvRPagar4,tvRPagar5,tvRPagar6,tvRPagar7,tvRPagar8,tvRPagar9,tvRPagar10,tvRPagar11,tvRPagar12,
                tvRReceber1,tvRReceber2,tvRReceber3,tvRReceber4,tvRReceber5,tvRReceber6,tvRReceber7,tvRReceber8,tvRReceber9,tvRReceber10,tvRReceber11,tvRReceber12;

        for (int Mes = 1; Mes < 13; Mes++) {
            try {
                cursor = bancoDados.query(NOME_TABELA_CONTAS, null, TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + Mes + "'",
                        null, null, null, null);
                while (cursor.moveToNext()) {
                    ValorPagar[Mes] = cursor.getString(cursor.getColumnIndex(VALOR));
                    ValorPagar[Mes] = ValorPagar[Mes].replaceAll(",", "."); // troca a , por .
                    TotalPagar[Mes] += Double.parseDouble(ValorPagar[Mes]);
                }

                cursor = bancoDados.query(NOME_TABELA_CONTAS, null, TIPO + "='Receber' AND ano ='" + ano + "' and mes='" + Mes + "'",
                        null, null, null, null);
                while (cursor.moveToNext()) {
                    ValorReceber[Mes] = cursor.getString(cursor.getColumnIndex(VALOR));
                    ValorReceber[Mes] = ValorReceber[Mes].replaceAll(",", "."); // troca a , por .
                    TotalReceber[Mes] += Double.parseDouble(ValorReceber[Mes]);
                }
                Log.i("resumo", "valorReceber" + TotalReceber[Mes]);
            } catch (Exception e) {
                Log.i("resumo", "erro " + e);
            }
        }

        tvRPagar1 = (TextView) activity.findViewById(R.id.tvPagar1);
        tvRPagar1.setText("Pagar R$" + String.format("%.2f", TotalPagar[1]));
        tvRPagar2 = (TextView) activity.findViewById(R.id.tvPagar2);
        tvRPagar2.setText("Pagar R$" + String.format("%.2f", TotalPagar[2]));
        tvRPagar3 = (TextView) activity.findViewById(R.id.tvPagar3);
        tvRPagar3.setText("Pagar R$" + String.format("%.2f", TotalPagar[3]));
        tvRPagar4 = (TextView) activity.findViewById(R.id.tvPagar4);
        tvRPagar4.setText("Pagar R$" + String.format("%.2f", TotalPagar[4]));
        tvRPagar5 = (TextView) activity.findViewById(R.id.tvPagar5);
        tvRPagar5.setText("Pagar R$" + String.format("%.2f", TotalPagar[5]));
        tvRPagar6 = (TextView) activity.findViewById(R.id.tvPagar6);
        tvRPagar6.setText("Pagar R$" + String.format("%.2f", TotalPagar[6]));
        tvRPagar7 = (TextView) activity.findViewById(R.id.tvPagar7);
        tvRPagar7.setText("Pagar R$" + String.format("%.2f", TotalPagar[7]));
        tvRPagar8 = (TextView) activity.findViewById(R.id.tvPagar8);
        tvRPagar8.setText("Pagar R$" + String.format("%.2f", TotalPagar[8]));
        tvRPagar9 = (TextView) activity.findViewById(R.id.tvPagar9);
        tvRPagar9.setText("Pagar R$" + String.format("%.2f", TotalPagar[9]));
        tvRPagar10 = (TextView) activity.findViewById(R.id.tvPagar10);
        tvRPagar10.setText("Pagar R$" + String.format("%.2f", TotalPagar[10]));
        tvRPagar11 = (TextView) activity.findViewById(R.id.tvPagar11);
        tvRPagar11.setText("Pagar R$" + String.format("%.2f", TotalPagar[11]));
        tvRPagar12 = (TextView) activity.findViewById(R.id.tvPagar12);
        tvRPagar12.setText("Pagar R$" + String.format("%.2f", TotalPagar[12]));

        tvRReceber1 = (TextView) activity.findViewById(R.id.tvReceber1);
        tvRReceber1.setText("Receber R$" + String.format("%.2f", TotalReceber[1]));
        tvRReceber2 = (TextView) activity.findViewById(R.id.tvReceber2);
        tvRReceber2.setText("Receber R$" + String.format("%.2f", TotalReceber[2]));
        tvRReceber3 = (TextView) activity.findViewById(R.id.tvReceber3);
        tvRReceber3.setText("Receber R$" + String.format("%.2f", TotalReceber[3]));
        tvRReceber4 = (TextView) activity.findViewById(R.id.tvReceber4);
        tvRReceber4.setText("Receber R$" + String.format("%.2f", TotalReceber[4]));
        tvRReceber5 = (TextView) activity.findViewById(R.id.tvReceber5);
        tvRReceber5.setText("Receber R$" + String.format("%.2f", TotalReceber[5]));
        tvRReceber6 = (TextView) activity.findViewById(R.id.tvReceber6);
        tvRReceber6.setText("Receber R$" + String.format("%.2f", TotalReceber[6]));
        tvRReceber7 = (TextView) activity.findViewById(R.id.tvReceber7);
        tvRReceber7.setText("Receber R$" + String.format("%.2f", TotalReceber[7]));
        tvRReceber8 = (TextView) activity.findViewById(R.id.tvReceber8);
        tvRReceber8.setText("Receber R$" + String.format("%.2f", TotalReceber[8]));
        tvRReceber9 = (TextView) activity.findViewById(R.id.tvReceber9);
        tvRReceber9.setText("Receber R$" + String.format("%.2f", TotalReceber[9]));
        tvRReceber10 = (TextView) activity.findViewById(R.id.tvReceber10);
        tvRReceber10.setText("Receber R$" + String.format("%.2f", TotalReceber[10]));
        tvRReceber11 = (TextView) activity.findViewById(R.id.tvReceber11);
        tvRReceber11.setText("Receber R$" + String.format("%.2f", TotalReceber[11]));
        tvRReceber12 = (TextView) activity.findViewById(R.id.tvReceber12);
        tvRReceber12.setText("Receber R$" + String.format("%.2f", TotalReceber[12]));

        /*
        LinearLayout layout= (LinearLayout)activity.findViewById(R.id.janeiro);;

        
        Log.i("aviso","valor "+TotalReceber[1]);
        if (TotalReceber[1] == 0.0 && TotalPagar[1] == 0.0) {
            layout = (LinearLayout)activity.findViewById(R.id.janeiro);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        if(TotalReceber[2] == 0.0 && TotalPagar[2] == 0.0){
            layout = (LinearLayout)activity.findViewById(R.id.fevereiro);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        if(TotalReceber[3] == 0.0 && TotalPagar[3] == 0.0){
            layout = (LinearLayout)activity.findViewById(R.id.marco);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        if(TotalReceber[4] == 0.0 && TotalPagar[4] == 0.0){
            layout = (LinearLayout)activity.findViewById(R.id.abril);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        if(TotalReceber[5] == 0.0 && TotalPagar[5] == 0.0){
            layout = (LinearLayout)activity.findViewById(R.id.maio);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        if(TotalReceber[6] == 0.0 && TotalPagar[6] == 0.0){
            layout = (LinearLayout)activity.findViewById(R.id.junho);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        if(TotalReceber[7] == 0.0 && TotalPagar[7] == 0.0){
            layout = (LinearLayout)activity.findViewById(R.id.julho);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        if(TotalReceber[8] == 0.0 && TotalPagar[8] == 0.0){
            layout = (LinearLayout)activity.findViewById(R.id.agosto);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        if(TotalReceber[9] == 0.0 && TotalPagar[9] == 0.0){
            layout = (LinearLayout)activity.findViewById(R.id.setembro);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        if(TotalReceber[10] == 0.0 && TotalPagar[10] == 0.0){
            layout = (LinearLayout)activity.findViewById(R.id.outubro);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        if(TotalReceber[11] == 0.0 && TotalPagar[11] == 0.0){
            layout = (LinearLayout)activity.findViewById(R.id.novembro);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        if(TotalReceber[12] == 0.0 && TotalPagar[12] == 0.0){
            layout = (LinearLayout)activity.findViewById(R.id.dezembro);
            layout.setVisibility(View.GONE);

        }else{
            layout.setVisibility(View.VISIBLE);
        }
        */
        FechaBanco();
    }

    /*
     * TODO Dialog
     */

    public void dialog(final Activity activity, final String acao, final String tipo, String texto,
                       final int posicao, final String cartaoNaoOuQual) {

        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.cadastrar_activity);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setTitle(texto);
        mToolbar = (Toolbar) dialog.findViewById(R.id.tb_main);
        final TextView tvId = (TextView) dialog.findViewById(R.id.tvId);
        final EditText etConta = (EditText) dialog.findViewById(R.id.etConta);
        final EditText etValor = (EditText) dialog.findViewById(R.id.etValor);
        final EditText etParcela = (EditText) dialog.findViewById(R.id.etParcela);
        final EditText etDia = (EditText) dialog.findViewById(R.id.etDia);
        final EditText etMes = (EditText) dialog.findViewById(R.id.etMes);
        final EditText etAno = (EditText) dialog.findViewById(R.id.etAno);
        final EditText etTipo = (EditText) dialog.findViewById(R.id.etTipo);
        final EditText etCategoria = (EditText) dialog.findViewById(R.id.etCategoria);
        final Spinner spinnerTipo = (Spinner) dialog.findViewById(R.id.spTipo);
        final Spinner spinnerCategoria = (Spinner) dialog.findViewById(R.id.spCategoria);
        final TextView txtTitulo = (TextView) dialog.findViewById(R.id.txtTitulo);

        txtTitulo.setText("Editar");
        mToolbar.setVisibility(View.GONE);
        etCategoria.setVisibility(View.VISIBLE);
        etTipo.setVisibility(View.VISIBLE);
        spinnerTipo.setVisibility(View.GONE);
        spinnerCategoria.setVisibility(View.GONE);
        etDia.setText("" + diA);
        etMes.setText("" + meS);
        etAno.setText("" + anO);

        cbSituacao = (CheckBox) dialog.findViewById(R.id.cbPago);
        btAcaoApagar = (Button) dialog.findViewById(R.id.btApagar);
        btAcaoApagar.setVisibility(View.VISIBLE);
        btAcaoApagar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                delete(activity, NOME_TABELA_CONTAS, posicao, tipo);
                carregaDados(activity, tipo, cartaoNaoOuQual, "", "");
                dialog.dismiss();
            }
        });

        btAcaoDialog = (Button) dialog.findViewById(R.id.btGravar);
        btAcaoDialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbSituacao.isChecked()) {
                    txtcb = "ok";
                } else
                    txtcb = "_";
                atualiza(activity, NOME_TABELA_CONTAS, etConta.getText().toString(), etCategoria.getText().toString(),
                        etValor.getText().toString(), etParcela.getText().toString(), etDia.getText().toString(),
                        etMes.getText().toString(), etAno.getText().toString(), txtcb, tvId.getText().toString(), tipo);
                dialog.cancel();

                carregaDados(activity, tipo, cartaoNaoOuQual, "", "");
                SomaExibe(activity, meS + "", anO + "");
            }
        });

        if (acao.equals("atz")) {
            btAcaoDialog.setText("Atualizar");

        }
        dialog.show();

        enviaDadosDialog(activity, NOME_TABELA_CONTAS, posicao);

    }

    /*
     * TODO envia dialog
     */

    public void enviaDadosDialog(Activity activity, String tabela, int Posicao) {
        AbreBanco(activity);
        cursor = bancoDados.query(tabela, null, ID + "=" + Posicao, null, null, null, null);
        // sql(tabela, ID+"="+Posicao,tipo);
        final EditText etConta = (EditText) dialog.findViewById(R.id.etConta);
        final EditText etCategoria = (EditText) dialog.findViewById(R.id.etCategoria);
        final EditText etValor = (EditText) dialog.findViewById(R.id.etValor);
        final EditText etParcela = (EditText) dialog.findViewById(R.id.etParcela);
        final EditText etDia = (EditText) dialog.findViewById(R.id.etDia);
        final EditText etMes = (EditText) dialog.findViewById(R.id.etMes);
        final EditText etAno = (EditText) dialog.findViewById(R.id.etAno);
        final TextView tvId = (TextView) dialog.findViewById(R.id.tvId);
        final CheckBox cbPago = (CheckBox) dialog.findViewById(R.id.cbPago);
        final EditText etTipo = (EditText) dialog.findViewById(R.id.etTipo);

        while (cursor.moveToNext()) {
            etConta.setText(cursor.getString(cursor.getColumnIndex(CONTA)));
            etValor.setText(cursor.getString(cursor.getColumnIndex(VALOR)));
            etParcela.setText(cursor.getString(cursor.getColumnIndex(PARCELA)));
            etDia.setText(cursor.getString(cursor.getColumnIndex(DIA)));
            etMes.setText(cursor.getString(cursor.getColumnIndex(MES)));
            etAno.setText(cursor.getString(cursor.getColumnIndex(ANO)));
            tvId.setText(cursor.getString(cursor.getColumnIndex(ID)));
            etTipo.setText(cursor.getString(cursor.getColumnIndex(TIPO)));
            etCategoria.setText(cursor.getString(cursor.getColumnIndex(CATEGORIA)));
            if (cursor.getString(cursor.getColumnIndex(SITUACAO)).equals("ok")) {
                cbPago.setChecked(true);
            }
        }

        FechaBanco();

    }

    /*
     * TODO show alert dialog
     */

    @SuppressWarnings("unused")
    private void createAndShowAlertDialog(final Activity activity, final int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção: Deseja excluir o registro??");
        builder.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                delete(activity, NOME_TABELA_CONTAS, pos, "Receber");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /*
     * TODO importar lista
     */


    @SuppressWarnings("resource")
    public void importarLista(Activity activity) {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "/Download/bancoContas.txt");
        AbreBanco(activity);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line, sql;
            while ((line = br.readLine()) != null) {
                // Log.i("sql", "" + line);
                sql = line;
                bancoDados.execSQL(sql);
            }
            Toast.makeText(activity, "Dados importados com sucesso! ", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity, "Erro ao importar! " + e, Toast.LENGTH_SHORT).show();
        }
        FechaBanco();
    }

    /*
        TODO criar lista exportacao
     */
    public void CriaListaParaExporacao(Activity activity) {
        @SuppressWarnings("unused")
        int contagem = 0;
        AbreBanco(activity);
        Cursor Cea = bancoDados.query(NOME_TABELA_CONTAS, null, null, null, null, null, null);

        while (Cea.moveToNext()) {
            Valores += "INSERT INTO Contas (conta,valor,parcela,ano,mes,dia,situacao,tipo,categoria) VALUES ('";
            Valores += Cea.getString(Cea.getColumnIndex("conta")) + "','"
                    + Cea.getString(Cea.getColumnIndex("valor")) + "','"
                    + Cea.getString(Cea.getColumnIndex("parcela")) + "','"
                    + Cea.getString(Cea.getColumnIndex("ano")) + "','"
                    + Cea.getString(Cea.getColumnIndex("mes")) + "',"
                    + Cea.getString(Cea.getColumnIndex("dia")) + ",'"
                    + Cea.getString(Cea.getColumnIndex("situacao")) + "','" + Cea.getString(Cea.getColumnIndex("tipo"))
                    + "','" + Cea.getString(Cea.getColumnIndex("categoria")) + "'),; \n";

            contagem++;
        }

        Valores = Valores.replaceAll(",;", ";");

        SalvarArquivo(Valores, activity);
        Cea.close();
        Toast.makeText(activity, "Exportação dos dados realizada com sucesso!", Toast.LENGTH_SHORT).show();
        FechaBanco();
    }

    /*
    TODO salvar arquivo
     */

    private void SalvarArquivo(String valor, Activity activity) {

        File arq;
        FileOutputStream fos;
        byte[] dados;
        try {
            arq = new File(Environment.getExternalStorageDirectory(), "/Download/bancoContas.txt");
            fos = new FileOutputStream(arq.toString());

            dados = valor.getBytes();

            fos.write(dados);
            fos.flush();
            fos.close();

            //enviarEmail(activity);
        } catch (IOException e) {
            Toast.makeText(activity, "Exportação com erro! " + e, Toast.LENGTH_SHORT).show();

        }
    }


    /*
    TODO ENVIAR EMAIL
     */
    public void enviarEmail(Activity activity) {
        String subject = "Controle de Contas - backup";
        String message = "Segue em anexo um backup dos dados ";

        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "/Download/bancoContas.txt"));

        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, message);
        i.putExtra(Intent.EXTRA_STREAM, uri);
        i.setType("text/plain");
        activity.startActivity(Intent.createChooser(i, "Send mail"));

    }

    /*
    TODO DELETAR
     */

    public void deletar(Activity activity) {
        AbreBanco(activity);
        try {
            bancoDados.delete(NOME_TABELA_CONTAS, null, null);
            Toast.makeText(activity, "Dados apagados com sucesso", Toast.LENGTH_SHORT).show();
        } catch (Exception er) {
            Toast.makeText(activity, "Erro! " + er, Toast.LENGTH_SHORT).show();
        }
        FechaBanco();
    }

    /* Pagar contas cartao do mes selecionado
     */

    public void pagarCartao(Activity activity, String mes, String ano, String acao, String cartao) {
        String texto = CATEGORIA + " = '" + cartao + "' AND " + MES + "='" + mes + "' AND " + ANO + "= '" + ano + "'";
        AbreBanco(activity);
        ContentValues contentValuesCampos = new ContentValues();
        contentValuesCampos.put(SITUACAO, acao);

        bancoDados.update(NOME_TABELA_CONTAS, contentValuesCampos, texto, null);
        FechaBanco();
        carregaDados(activity, "Pagar", cartao, "", "");
        SomaExibe(activity, meS + "", anO + "");
    }


	/*
 TODO faz a soma dos valores a pagar/receber/saldo/falta pagar e retorna
	 * no metodo verificaregistro()
	 */


    @SuppressLint("DefaultLocale")
    public void SomaExibeMainActivity(Activity activity, String mes, String ano) {
        String valorPagar, valorPagarMeu, valorReceber, valorFalta, valorFaltaMeu, valorMarisa, valorItau,
                ValorCaixa, valorBradesco, valorCea, valorNubank, valorHipercard, valorPernambucanas,
                valorRiachuelo, valorate20, valordepois20;
        double totalPagar = 0, totalPagarMeu = 0, totalReceber = 0, totalSaldo, totalFalta = 0,
                totalFaltaMeu = 0, totalMarisa = 0, totalItau = 0, totalCaixa = 0, totalBradesco = 0,
                totalRiachuelo = 0, totalCea = 0, totalNubank = 0, totalHipercard = 0, totalPernambucanas = 0,
                totalate20 = 0, totaldepois20 = 0;
        AbreBanco(activity);

        // até dia 15
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + mes + "' AND dia < 21",
                null, null, null, null);
        while (cursor.moveToNext()) {
            valorate20 = cursor.getString(cursor.getColumnIndex(VALOR));
            valorate20 = valorate20.replaceAll(",", ".");
            totalate20 += Double.parseDouble(valorate20);
        }
        TextView tvAte20 = (TextView) activity.findViewById(R.id.tvtotalantes20);
        tvAte20.setText("Antes 20 R$" + String.format("%.2f", totalate20));

        // depois dia 20
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + mes + "' AND dia > 20",
                null, null, null, null);
        while (cursor.moveToNext()) {
            valordepois20 = cursor.getString(cursor.getColumnIndex(VALOR));
            valordepois20 = valordepois20.replaceAll(",", ".");
            totaldepois20 += Double.parseDouble(valordepois20);
        }
        TextView tvdepois20 = (TextView) activity.findViewById(R.id.tvtotaldepois20);
        tvdepois20.setText("Depois 20 R$ " + String.format("%.2f", totaldepois20));

        // Pagar meu
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + mes + "'",
                null, null, null, null);
        while (cursor.moveToNext()) {
            valorPagarMeu = cursor.getString(cursor.getColumnIndex(VALOR));
            valorPagarMeu = valorPagarMeu.replaceAll(",", ".");
            totalPagarMeu += Double.parseDouble(valorPagarMeu);
        }
        TextView tvRPagarMeu = (TextView) activity.findViewById(R.id.tvAPagar);
        tvRPagarMeu.setText("Pagar R$ " + String.format("%.2f", totalPagarMeu));


        // Receber
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Receber' AND ano ='" + ano + "' and mes='" + mes + "'", null, null, null, null);
        while (cursor.moveToNext()) {
            valorReceber = cursor.getString(cursor.getColumnIndex(VALOR));
            valorReceber = valorReceber.replaceAll(",", "."); // troca a ,
            totalReceber += Double.parseDouble(valorReceber);
        }
        TextView tvRReceber = (TextView) activity.findViewById(R.id.tvReceber);
        tvRReceber.setText("Receber R$ " + String.format("%.2f", totalReceber));

        // Falta
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND ano ='" + ano + "' and mes='" + mes + "' and situacao ='_'", null, null, null,
                null);
        while (cursor.moveToNext()) {
            valorFalta = cursor.getString(cursor.getColumnIndex(VALOR));
            valorFalta = valorFalta.replaceAll(",", "."); // troca a , por .
            totalFalta += Double.parseDouble(valorFalta);
        }
        TextView tvRFalta = (TextView) activity.findViewById(R.id.tvFaltaPagar);
        tvRFalta.setText("Falta Pagar R$ " + String.format("%.2f", totalFalta));


        // itau
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria ='Itau' AND ano ='" + ano + "' and mes='" + mes + "'",
                null, null, null, null);
        while (cursor.moveToNext()) {
            valorItau = cursor.getString(cursor.getColumnIndex(VALOR));
            valorItau = valorItau.replaceAll(",", "."); // troca a , por
            totalItau += Double.parseDouble(valorItau);
        }
        TextView tvItau = (TextView) activity.findViewById(R.id.tvItau);
        tvItau.setText("Cartao Itau R$ " + String.format("%.2f", totalItau));

        if (totalItau == 0) {
            tvItau.setVisibility(View.GONE);
        } else {
            tvItau.setVisibility(View.VISIBLE);
        }

        // Caixa

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria='Caixa' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            ValorCaixa = cursor.getString(cursor.getColumnIndex(VALOR));
            ValorCaixa = ValorCaixa.replaceAll(",", "."); // troca a , por .
            totalCaixa += Double.parseDouble(ValorCaixa);
        }

        TextView tvcaixa = (TextView) activity.findViewById(R.id.tvCaixa);
        tvcaixa.setText("Cartao Caixa R$" + String.format("%.2f", totalCaixa));
        if (totalCaixa == 0) {
            tvcaixa.setVisibility(View.GONE);
        } else {
            tvcaixa.setVisibility(View.VISIBLE);
        }
        // Nubank

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria='Nubank' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorNubank = cursor.getString(cursor.getColumnIndex(VALOR));
            valorNubank = valorNubank.replaceAll(",", "."); // troca a , por .
            totalNubank += Double.parseDouble(valorNubank);
        }

        TextView tvnubank = (TextView) activity.findViewById(R.id.tvNubank);
        tvnubank.setText("Cartao Nubank R$ " + String.format("%.2f", totalNubank));
        if (totalNubank == 0) {
            tvnubank.setVisibility(View.GONE);
        } else {
            tvnubank.setVisibility(View.VISIBLE);
        }

        // Hipercard

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria='Hipercard' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorHipercard = cursor.getString(cursor.getColumnIndex(VALOR));
            valorHipercard = valorHipercard.replaceAll(",", "."); // troca a , por .
            totalHipercard += Double.parseDouble(valorHipercard);
        }

        TextView tvhipercard = (TextView) activity.findViewById(R.id.tvHipercard);
        tvhipercard.setText("Cartao Hipercard R$ " + String.format("%.2f", totalHipercard));
        if (totalHipercard == 0) {
            tvhipercard.setVisibility(View.GONE);
        } else {
            tvhipercard.setVisibility(View.VISIBLE);
        }


        // Bradesco

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria='Bradesco' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorBradesco = cursor.getString(cursor.getColumnIndex(VALOR));
            valorBradesco = valorBradesco.replaceAll(",", "."); // troca a , por .
            totalBradesco += Double.parseDouble(valorBradesco);
        }


        TextView tvbradesco = (TextView) activity.findViewById(R.id.tvBradesco);
        tvbradesco.setText("Cartao Bradesco R$ " + String.format("%.2f", totalBradesco));
        if (totalBradesco == 0) {
            tvbradesco.setVisibility(View.GONE);
        } else {
            tvbradesco.setVisibility(View.VISIBLE);
        }
        // marisa

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria = 'Marisa' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorMarisa = cursor.getString(cursor.getColumnIndex(VALOR));
            valorMarisa = valorMarisa.replaceAll(",", "."); // troca a , por .
            totalMarisa += Double.parseDouble(valorMarisa);
        }


        TextView tvmarisa = (TextView) activity.findViewById(R.id.tvMarisa);
        tvmarisa.setText("Cartao Marisa R$ " + String.format("%.2f", totalMarisa));
        if (totalMarisa == 0) {
            tvmarisa.setVisibility(View.GONE);
        } else {
            tvmarisa.setVisibility(View.VISIBLE);
        }
        // Riachuelo

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria = 'Riachuelo' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorRiachuelo = cursor.getString(cursor.getColumnIndex(VALOR));
            valorRiachuelo = valorRiachuelo.replaceAll(",", "."); // troca a , por .
            totalRiachuelo += Double.parseDouble(valorRiachuelo);
        }


        TextView tvriachuelo = (TextView) activity.findViewById(R.id.tvRiachuelo);
        tvriachuelo.setText("Cartao Riachuelo R$ " + String.format("%.2f", totalRiachuelo));
        if (totalRiachuelo == 0) {
            tvriachuelo.setVisibility(View.GONE);
        } else {
            tvriachuelo.setVisibility(View.VISIBLE);
        }

        // Cea

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria = 'Cea' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorCea = cursor.getString(cursor.getColumnIndex(VALOR));
            valorCea = valorCea.replaceAll(",", "."); // troca a , por .
            totalCea += Double.parseDouble(valorCea);
        }
        TextView tvCea = (TextView) activity.findViewById(R.id.tvCea);
        tvCea.setText("Cartao C&a R$ " + String.format("%.2f", totalCea));
        if (totalCea == 0) {
            tvCea.setVisibility(View.GONE);
        } else {
            tvCea.setVisibility(View.VISIBLE);
        }
        // Pernambucanas

        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                TIPO + "='Pagar' AND categoria = 'Pernambucanas' AND ano ='" + ano + "' and mes='" + mes + "'", null, null,
                null, null);
        while (cursor.moveToNext()) {
            valorPernambucanas = cursor.getString(cursor.getColumnIndex(VALOR));
            valorPernambucanas = valorPernambucanas.replaceAll(",", "."); // troca a , por .
            totalPernambucanas += Double.parseDouble(valorPernambucanas);
        }


        TextView tvPernambucanas = (TextView) activity.findViewById(R.id.tvPernambucanas);
        tvPernambucanas.setText("Cartao Pernambucanas R$ " + String.format("%.2f", totalPernambucanas));
        if (totalPernambucanas == 0) {
            tvPernambucanas.setVisibility(View.GONE);
        } else {
            tvPernambucanas.setVisibility(View.VISIBLE);
        }

        // saldo
        totalSaldo = totalReceber - totalPagarMeu;
        TextView tvRSaldo = (TextView) activity.findViewById(R.id.tvSaldo1);
        tvRSaldo.setText(String.format("%.2f", totalSaldo));

        FechaBanco();

    }

    /*
    TODO RETORNA DIA
     */
    public int RetornaDia() {

        Calendar _calendar = Calendar.getInstance(Locale.getDefault());
        return _calendar.get(Calendar.DAY_OF_MONTH);
    }

    /*
    TODO RETORNA MES
     */
    public int RetornaMes() {

        Calendar _calendar = Calendar.getInstance(Locale.getDefault());
        return _calendar.get(Calendar.MONTH) + 1;
    }

    /*
    TODO RETORNA ANO
     */
    public int RetornaAno() {

        Calendar _calendar = Calendar.getInstance(Locale.getDefault());
        return _calendar.get(Calendar.YEAR);
    }


    public void toast(Activity activity, String aviso) {
        Toast.makeText(activity, aviso, Toast.LENGTH_SHORT).show();
    }
    //



    /*
    TODO exibe os botoes referentes aos cartoes cadastrados com contas
     */

    public void ExibeBotaoCartoes(Activity activity) {
        String itau = "", bradesco = "", caixa = "", nubank = "", marisa = "", riachuelo = "", cea = "", hipercard = "",pernambucanas = "";

        AbreBanco(activity);

        // itau
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                CATEGORIA + "='Itau'", null, null, null, null);
        while (cursor.moveToNext()) {
            itau = cursor.getString(cursor.getColumnIndex(VALOR));
        }
        if (itau.isEmpty()) {
            Button btItau = (Button) activity.findViewById(R.id.btItau);
            btItau.setVisibility(View.GONE);
        }

        // bradesco
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                CATEGORIA + "='Bradesco'", null, null, null, null);
        while (cursor.moveToNext()) {
            bradesco = cursor.getString(cursor.getColumnIndex(VALOR));
        }
        if (bradesco.isEmpty()) {
            Button btBradesco = (Button) activity.findViewById(R.id.btBradesco);
            btBradesco.setVisibility(View.GONE);
        }
        // caixa
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                CATEGORIA + "='Caixa'", null, null, null, null);
        while (cursor.moveToNext()) {
            caixa = cursor.getString(cursor.getColumnIndex(VALOR));
        }
        if (caixa.isEmpty()) {
            Button btCaixa = (Button) activity.findViewById(R.id.btCaixa);
            btCaixa.setVisibility(View.GONE);
        }

        // Nubank
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                CATEGORIA + "='Nubank'", null, null, null, null);
        while (cursor.moveToNext()) {
            nubank = cursor.getString(cursor.getColumnIndex(VALOR));
        }
        if (nubank.isEmpty()) {
            Button btNubank = (Button) activity.findViewById(R.id.btNubank);
            btNubank.setVisibility(View.GONE);
        }

        // Hipercard
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                CATEGORIA + "='Hipercard'", null, null, null, null);
        while (cursor.moveToNext()) {
            hipercard = cursor.getString(cursor.getColumnIndex(VALOR));
        }
        if (hipercard.isEmpty()) {
            Button btHipercard = (Button) activity.findViewById(R.id.btHipercard);
            btHipercard.setVisibility(View.GONE);
        }

        // marisa
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                CATEGORIA + "='Marisa'", null, null, null, null);
        while (cursor.moveToNext()) {
            marisa = cursor.getString(cursor.getColumnIndex(VALOR));
        }
        if (marisa.isEmpty()) {
            Button btMarisa = (Button) activity.findViewById(R.id.btMarisa);
            btMarisa.setVisibility(View.GONE);
        }

        // riachuelo
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                CATEGORIA + "='Riachuelo'", null, null, null, null);
        while (cursor.moveToNext()) {
            riachuelo = cursor.getString(cursor.getColumnIndex(VALOR));
        }
        if (riachuelo.isEmpty()) {
            Button btRiachuelo = (Button) activity.findViewById(R.id.btRiachuelo);
            btRiachuelo.setVisibility(View.GONE);
        }

        // cea
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                CATEGORIA + "='Cea'", null, null, null, null);
        while (cursor.moveToNext()) {
            cea = cursor.getString(cursor.getColumnIndex(VALOR));
        }
        if (cea.isEmpty()) {
            Button btCea = (Button) activity.findViewById(R.id.btCea);
            btCea.setVisibility(View.GONE);
        }
        // Pernambucanas
        cursor = bancoDados.query(NOME_TABELA_CONTAS, null,
                CATEGORIA + "='Pernambucanas'", null, null, null, null);
        while (cursor.moveToNext()) {
            pernambucanas = cursor.getString(cursor.getColumnIndex(VALOR));
        }
        if (pernambucanas.isEmpty()) {
            Button btpernambucanas = (Button) activity.findViewById(R.id.btPernambucanas);
            btpernambucanas.setVisibility(View.GONE);
        }

    }


}
