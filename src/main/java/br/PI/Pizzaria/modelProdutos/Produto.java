package br.PI.Pizzaria.modelProdutos;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produto", schema = "pizzariabhg")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String nome;

    @NotEmpty
    private String descricao;

    @Min(1)
    private int quantidade;

    @DecimalMin("0.01")
    private double valor;

    private Double avaliacao; // Nota de avaliação do produto

    @Column(nullable = false)
    private Boolean ativo = false; // Define um valor padrão para evitar null

    @Column(name = "imagem")
    private String imagem;

    private String categoria;


    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImagemProduto> imagensSecundarias = new ArrayList<>();

    /**
     * Retorna a lista de todas as imagens, com a principal em primeiro lugar.
     */
    public List<String> getTodasImagens() {
        List<String> todas = new ArrayList<>();
        if (imagem != null && !imagem.isEmpty()) {
            todas.add(imagem);
        }
        for (ImagemProduto img : imagensSecundarias) {
            if (img.getCaminhoImagem() != null && !img.getCaminhoImagem().isEmpty()) {
                todas.add(img.getCaminhoImagem());
            }
        }
        return todas;
    }

    /**
     * Adiciona uma nova imagem secundária ao produto.
     */
    public void adicionarImagemSecundaria(String caminhoImagem) {
        if (caminhoImagem != null && !caminhoImagem.isEmpty()) {
            ImagemProduto nova = new ImagemProduto();
            nova.setCaminhoImagem(caminhoImagem);
            nova.setProduto(this);
            imagensSecundarias.add(nova);
        }
    }

    private Boolean finalizado = false;

    public Boolean getAtivo() {
        return ativo != null ? ativo : false;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public List<ImagemProduto> getImagensSecundarias() {
        return imagensSecundarias;
    }

    public void setImagensSecundarias(List<ImagemProduto> imagensSecundarias) {
        this.imagensSecundarias = imagensSecundarias;
    }

    public Boolean getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(Boolean finalizado) {
        this.finalizado = finalizado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
