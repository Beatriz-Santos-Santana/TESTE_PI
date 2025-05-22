package br.PI.Pizzaria.controllerCarrinho;

import br.PI.Pizzaria.modelCarrinho.ItemCarrinho;
import br.PI.Pizzaria.modelPedidos.ItemPedidos;
import br.PI.Pizzaria.modelPedidos.Pedidos;
import br.PI.Pizzaria.modelProdutos.Produto;
import br.PI.Pizzaria.repository.ItemPedidoRepository;
import br.PI.Pizzaria.repository.PedidoRepository;
import br.PI.Pizzaria.repository.ProdutoRepository;
import br.PI.Pizzaria.service.CarrinhoService;
import br.PI.Pizzaria.service.CookieService;
import br.PI.Pizzaria.service.ProdutoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @PostMapping("/adicionar/{id}")
    public String adicionarAoCarrinho(@PathVariable Long id,
                                      HttpSession session) {
        Produto produto = produtoService.buscarPorId(id);  // Verifique esse serviço de busca

        if (produto == null) {
            System.out.println("Produto não encontrado, id: " + id);  // Log de depuração
        } else {
            System.out.println("Produto adicionado: " + produto.getNome());
        }

        if (produto != null) {
            carrinhoService.adicionarProduto(produto, session);  // Adiciona ao carrinho
        }

        return "redirect:/carrinho";
    }

    @GetMapping
    public String verCarrinho(Model model, HttpSession session) {
        List<ItemCarrinho> carrinho = carrinhoService.obterCarrinho(session);
        double totalCompra = carrinhoService.calcularTotal(carrinho);

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCompra", totalCompra);

        return "carrinho";
    }

    @PostMapping("/remover")
    public String removerProduto(@RequestParam("produtoId") Long produtoId, HttpSession session) {
        carrinhoService.removerProduto(produtoId, session);
        return "redirect:/carrinho";
    }

    @PostMapping("/incrementar")
    public String incrementarQuantidade(@RequestParam("produtoId") Long produtoId, HttpSession session) {
        carrinhoService.incrementarQuantidade(produtoId, session);
        return "redirect:/carrinho";  // Redireciona para a página do carrinho
    }

    @PostMapping("/decrementar")
    public String decrementarQuantidade(@RequestParam("produtoId") Long produtoId, HttpSession session) {
        carrinhoService.decrementarQuantidade(produtoId, session);
        return "redirect:/carrinho";
    }

    @PostMapping("/finalizar")
    public String finalizarCarrinho(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        List<ItemCarrinho> itensCarrinho = carrinhoService.obterCarrinho(session);

        if (itensCarrinho == null || itensCarrinho.isEmpty()) {
            return "redirect:/carrinho";
        }

        String pedidoIdStr = CookieService.getCookie(request, "pedidoEmAndamento");
        if (pedidoIdStr == null) return "redirect:/cardapio";

        Long pedidoId = Long.parseLong(pedidoIdStr);
        Pedidos pedido = pedidoRepository.findById(pedidoId).orElse(null);
        if (pedido == null) return "redirect:/cardapio";

        BigDecimal total = BigDecimal.ZERO;

        for (ItemCarrinho itemCarrinho : itensCarrinho) {
            // A correção é aqui: procure o produto com base no produto associado ao itemCarrinho
            Produto produto = produtoRepository.findById(itemCarrinho.getProduto().getId()).orElse(null); // A correção está aqui
            if (produto == null) continue;

            ItemPedidos itemPedido = new ItemPedidos();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setNomeProduto(itemCarrinho.getNome());
            itemPedido.setQuantidade(itemCarrinho.getQuantidade());
            itemPedido.setPrecoUnitario(BigDecimal.valueOf(itemCarrinho.getValor()));
            itemPedido.atualizarSubTotal();

            itemPedidoRepository.save(itemPedido);
            total = total.add(itemPedido.getSubTotal());
        }

        pedido.setValorTotal(total);
        pedidoRepository.save(pedido);

        // Limpa o carrinho da sessão
        session.removeAttribute("carrinho");

        return "redirect:/pedido/resumo";
    }


}
