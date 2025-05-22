package br.PI.Pizzaria.controllerPedidos;

import br.PI.Pizzaria.modelPedidos.ItemPedidos;
import br.PI.Pizzaria.modelPedidos.Pedidos;
import br.PI.Pizzaria.modelProdutos.Produto;
import br.PI.Pizzaria.repository.ItemPedidoRepository;
import br.PI.Pizzaria.repository.PedidoRepository;
import br.PI.Pizzaria.repository.ProdutoRepository;
import br.PI.Pizzaria.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class ItemPedidoController {

    @Autowired
    PedidoRepository pedidoRepository;

    @Autowired
    ProdutoRepository produtoRepository;

    @Autowired
    ItemPedidoRepository itemPedidoRepository;

    @PostMapping("/adicionar")
    public String adicionarItem(@RequestParam Long produtoId,
                                @RequestParam Integer quantidade,
                                HttpServletRequest request,
                                HttpServletResponse response) throws UnsupportedEncodingException {

        String pedidoIdStr = CookieService.getCookie(request, "pedidoEmAndamento");
        Pedidos pedido;

        if (pedidoIdStr == null) {
            pedido = new Pedidos();
            pedido.setValorTotal(BigDecimal.ZERO);
            pedidoRepository.save(pedido);
            CookieService.setCookie(response, "pedidoEmAndamento", String.valueOf(pedido.getId()), 3600);
        } else {
            pedido = pedidoRepository.findById(Long.parseLong(pedidoIdStr)).orElse(null);
            if (pedido == null) return "redirect:/cardapio";
        }

        Produto produto = produtoRepository.findById(produtoId).orElse(null);
        if (produto == null) return "redirect:/cardapio";

        // Cria e salva item diretamente
        ItemPedidos item = new ItemPedidos();
        item.setProduto(produto);
        item.setNomeProduto(produto.getNome());
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(BigDecimal.valueOf(produto.getValor()));
        item.atualizarSubTotal();
        item.setPedido(pedido);

        itemPedidoRepository.save(item); // ✅ salva o item no banco

        // Recarrega todos os itens do pedido e recalcula o total
        List<ItemPedidos> todosItens = itemPedidoRepository.findByPedidoId(pedido.getId());
        pedido.setItens(todosItens); // garantir consistência
        BigDecimal novoTotal = todosItens.stream()
                .map(ItemPedidos::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        pedido.setValorTotal(novoTotal);

        pedidoRepository.save(pedido); // salvar com valor total atualizado

        System.out.println("Pedido ID: " + pedido.getId());
        System.out.println("Itens adicionados:");
        todosItens.forEach(i -> System.out.println(i.getNomeProduto() + " - Qtd: " + i.getQuantidade()));


        return "redirect:/pedido/resumo";


    }



}
