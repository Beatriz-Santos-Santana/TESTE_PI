package br.PI.Pizzaria.modelPedidos;

import br.PI.Pizzaria.modelProdutos.Produto;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "item_pedido", schema = "pizzariabhg")
public class ItemPedidos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedidos pedido;

    @ManyToOne
    private Produto produto; // novo campo

    private String nomeProduto;
    private int quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subTotal;

    // Getters e setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pedidos getPedido() {
        return pedido;
    }

    public void setPedido(Pedidos pedido) {
        this.pedido = pedido;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public void atualizarSubTotal() {
        if (precoUnitario != null && quantidade > 0) {
            this.subTotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        }
    }
}
