package model;

import java.util.ArrayList;

public class Comida extends ItemCardapio {
    private String descricao;
    private ArrayList<String> ingredientes; // Adicionado Generic <String>

    // Construtores
    public Comida() {
        super();
        this.ingredientes = new ArrayList<>();
    }

    public Comida(String nome, double preco, String descricao, ArrayList<String> ingredientes) {
        super(nome, preco);
        this.descricao = descricao;
        this.ingredientes = ingredientes;
    }

    // Getters e Setters
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public ArrayList<String> getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(ArrayList<String> ingredientes) {
        this.ingredientes = ingredientes;
    }
    public void adicionarIngrediente(String ingrediente) {
        ingredientes.add(ingrediente);
    }
}