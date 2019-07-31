package com.chriscoliveira.contas.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {
    public static boolean validarPermissoes(Activity activity, String[] permissoes, int RequestCode) {

        if (Build.VERSION.SDK_INT >= 23) {
            List<String> listaPermissoes = new ArrayList<>();

            //verifica se ja tem as permissoes
            for (String permissao : permissoes) {
                //verifica se ja tem
                Boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                //caso nao tenha ele adiciona
                if(!temPermissao)listaPermissoes.add(permissao);
            }
            //caso alista esteja vazia nao solicita
            if(listaPermissoes.isEmpty())return true;
            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //solicita permissao
            ActivityCompat.requestPermissions(activity,novasPermissoes,RequestCode);
        }

        return true;
    }
}
