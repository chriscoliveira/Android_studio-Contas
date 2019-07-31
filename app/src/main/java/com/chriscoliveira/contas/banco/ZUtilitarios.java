package com.chriscoliveira.contas.banco;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;


@SuppressWarnings("unused")
public class ZUtilitarios extends Activity {

    public String DataExtenso(int mes) {
        String mesExtenso = "";

        switch (mes) {
            case 1:
                mesExtenso = "Janeiro";
                break;
            case 2:
                mesExtenso = "Fevereiro";
                break;
            case 3:
                mesExtenso = "Março";
                break;
            case 4:
                mesExtenso = "Abril";
                break;
            case 5:
                mesExtenso = "Maio";
                break;
            case 6:
                mesExtenso = "Junho";
                break;
            case 7:
                mesExtenso = "Julho";
                break;
            case 8:
                mesExtenso = "Agosto";
                break;
            case 9:
                mesExtenso = "Setembro";
                break;
            case 10:
                mesExtenso = "Outubro";
                break;
            case 11:
                mesExtenso = "Novembro";
                break;
            case 12:
                mesExtenso = "Dezembro";
                break;
        }
        return mesExtenso;
    }


    public void mensagem(Activity activity, String titulo, String mensagem) {
        AlertDialog.Builder CaixaAlerta = new AlertDialog.Builder(activity);
        CaixaAlerta.setMessage(mensagem);
        CaixaAlerta.setTitle(titulo);
        CaixaAlerta.setNeutralButton("OK", null);
        CaixaAlerta.show();
    }

    public void toast(Activity activity, String aviso) {
        Toast.makeText(activity, aviso, Toast.LENGTH_SHORT).show();
    }


}
