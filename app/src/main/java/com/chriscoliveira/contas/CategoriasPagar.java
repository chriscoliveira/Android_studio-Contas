package com.chriscoliveira.contas;

/**
 * Created by 21220 on 27/09/17.
 */

public class CategoriasPagar {
    private String categoria;
    private int id;

    public CategoriasPagar(int id, String categoria){
        this.id = id;
        this.categoria = categoria;
    }

    public int getId(){
        return id;
    }

    public String getCategoria(){
        return categoria;
    }

    @Override
    public String toString()
    {
        return categoria;
    }
}
