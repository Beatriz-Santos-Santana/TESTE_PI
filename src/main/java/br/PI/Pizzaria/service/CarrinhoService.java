package br.PI.Pizzaria.service;

import br.PI.Pizzaria.modelCarrinho.ItemCarrinho;
import br.PI.Pizzaria.modelProdutos.Produto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class CarrinhoService {

    public List<ItemCarrinho> obterCarrinho(HttpSession session) {
        List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
            session.setAttribute("carrinho", carrinho);
        }
        return carrinho;
    }

    // No CarrinhoService onde o produto é adicionado
    public void adicionarProduto(Produto produto, HttpSession session) {
        List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new ArrayList<>();
            session.setAttribute("carrinho", carrinho);
        }

        // Aqui você pode adicionar ou atualizar o ItemCarrinho
        ItemCarrinho itemCarrinho = new ItemCarrinho();
        itemCarrinho.setProduto(produto);  // Garantir que o produto é atribuído corretamente
        itemCarrinho.setNome(produto.getNome());
        itemCarrinho.setQuantidade(1);
        itemCarrinho.setValor(produto.getValor());  // ou outro atributo de preço

        carrinho.add(itemCarrinho);
    }


    public void removerProduto(Long produtoId, HttpSession session) {
        List<ItemCarrinho> carrinho = obterCarrinho(session);
        carrinho.removeIf(item -> item.getProduto().getId().equals(produtoId));
    }

    public void incrementarQuantidade(Long produtoId, HttpSession session) {
        List<ItemCarrinho> carrinho = obterCarrinho(session);
        for (ItemCarrinho item : carrinho) {
            if (item.getProduto().getId().equals(produtoId)) {
                item.setQuantidade(item.getQuantidade() + 1);
                break;
            }
        }
    }

    public void decrementarQuantidade(Long produtoId, HttpSession session) {
        List<ItemCarrinho> carrinho = obterCarrinho(session);
        Iterator<ItemCarrinho> iterator = carrinho.iterator();
        while (iterator.hasNext()) {
            ItemCarrinho item = iterator.next();
            if (item.getProduto().getId().equals(produtoId)) {
                if (item.getQuantidade() > 1) {
                    item.setQuantidade(item.getQuantidade() - 1);
                } else {
                    iterator.remove();
                }
                break;
            }
        }
    }

    public double calcularTotal(List<ItemCarrinho> carrinho) {
        return carrinho.stream()
                .mapToDouble(item -> item.getValor() * item.getQuantidade())
                .sum();
    }
}
