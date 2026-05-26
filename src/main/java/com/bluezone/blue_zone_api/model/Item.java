package com.bluezone.blue_zone_api.model;

public class Item {
    private String categoriaPrincipal;
    private String subcategoria;
    private String item;
    private String controlado;
    private String quantidade;
    private String estoqueMinimo;
    private String dataVencimento;
    private String status;
    private String observacoes;
    private String ultimaAtualizacao;

    public Item() {
    }

    public String getCategoriaPrincipal() {
        return categoriaPrincipal;
    }

    public void setCategoriaPrincipal(String categoriaPrincipal) {
        this.categoriaPrincipal = categoriaPrincipal;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getControlado() {
        return controlado;
    }

    public void setControlado(String controlado) {
        this.controlado = controlado;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public String getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setEstoqueMinimo(String estoqueMinimo) {
        this.estoqueMinimo = estoqueMinimo;
    }

    public String getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(String ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }
}