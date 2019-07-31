package com.chriscoliveira.contas.banco;

/**
 * Created by 21220 on 12/01/2017.
 */

public class SQL {

    public static String NOME_BANCO = "MinhasContas2014";
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

    int dia, mes, ano;
    String tipo;

    String criaTabela = "CREATE TABLE IF NOT EXISTS " + NOME_TABELA_CONTAS + "(" + ID + " INTEGER PRIMARY KEY, "
            + CONTA + " TEXT, " + VALOR + " TEXT, " + PARCELA + " TEXT," + DIA + " INTEGER, " + MES + " TEXT," + ANO
            + " TEXT," + SITUACAO + " TEXT," + TIPO + " TEXT," + CATEGORIA + " TEXT)";

    String criaTabelaListadeCategorias = "CREATE TABLE IF NOT EXISTS " + NOME_TABELA_CATEGORIAS + "(" + ID + " INTEGER PRIMARY KEY, "
            + CATEGORIA + " TEXT)";

    String sqlCartao = MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
            + "='" + tipo + "' AND " + CATEGORIA + "='Cartao'";

    String sqlSemCartao = MES + "='" + mes + "' AND " + ANO + "='" + ano + "' AND " + TIPO
            + "='" + tipo + "' AND " + CATEGORIA + "!='Cartao'";

    String OrdemSituacaoDiaASC = SITUACAO + " ASC, " + DIA + " ASC";


}
