package br.PI.Pizzaria.modelCarrinho;

import br.PI.Pizzaria.modelCliente.Endereco;

import java.util.List;

public class ResumoPedido {
    private List<ItemCarrinho> itens;
    private Endereco enderecoEntrega;
    private String metodoPagamento;
    private double frete;
    private double totalProdutos;
    private double totalFinal;

    // Getters e Setters

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public void setItens(List<ItemCarrinho> itens) {
        this.itens = itens;
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(Endereco enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public double getFrete() {
        return frete;
    }

    public void setFrete(double frete) {
        this.frete = frete;
    }

    public double getTotalProdutos() {
        return totalProdutos;
    }

    public void setTotalProdutos(double totalProdutos) {
        this.totalProdutos = totalProdutos;
    }

    public double getTotalFinal() {
        return totalFinal;
    }

    public void setTotalFinal(double totalFinal) {
        this.totalFinal = totalFinal;
    }
}
