package br.PI.Pizzaria.controllerPagamento;

import br.PI.Pizzaria.modelCarrinho.ItemCarrinho;
import br.PI.Pizzaria.modelCliente.Cliente;
import br.PI.Pizzaria.modelCliente.Endereco;
import br.PI.Pizzaria.modelPedidos.ItemPedidos;
import br.PI.Pizzaria.modelPedidos.Pedidos;
import br.PI.Pizzaria.modelProdutos.Produto;
import br.PI.Pizzaria.repository.ClienteRepository;
import br.PI.Pizzaria.repository.ItemPedidoRepository;
import br.PI.Pizzaria.repository.PedidoRepository;
import br.PI.Pizzaria.repository.ProdutoRepository;
import br.PI.Pizzaria.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class PagamentoController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    // Exibe a página de pagamento
    @GetMapping("/pagamento/pagamento")
    public String exibirPagamento(HttpServletRequest request, org.springframework.ui.Model model) throws UnsupportedEncodingException {
        String pedidoIdStr = CookieService.getCookie(request, "pedidoEmAndamento");

        if (pedidoIdStr == null) {
            return "redirect:/selecionarPagamento";
        }

        Long pedidoId = Long.parseLong(pedidoIdStr);
        Pedidos pedido = pedidoRepository.findById(pedidoId).orElse(null);

        if (pedido == null) {
            return "redirect:/selecionarPagamento";
        }

        model.addAttribute("pedido", pedido);

        Cliente cliente = pedido.getCliente();
        if (cliente != null) {
            model.addAttribute("metodoSelecionado", cliente.getMetodoPagamento());
        }

        return "selecionarPagamento";
    }


    @PostMapping("/pagamento/finalizar")
    public String processarPagamento(@RequestParam("metodoPagamento") String metodoPagamento,
                                     HttpSession session,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {

        Cliente cliente = (Cliente) session.getAttribute("usuarioLogado");
        Endereco endereco = (Endereco) session.getAttribute("enderecoSelecionado");
        List<ItemCarrinho> itensCarrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");

        if (cliente == null || endereco == null || itensCarrinho == null || itensCarrinho.isEmpty()) {
            return "redirect:/carrinho";
        }

        try {
            String pedidoIdStr = CookieService.getCookie(request, "pedidoEmAndamento");
            Pedidos pedido;

            if (pedidoIdStr != null) {
                pedido = pedidoRepository.findById(Long.parseLong(pedidoIdStr)).orElse(null);
            } else {
                pedido = new Pedidos();
                pedido.setCliente(cliente);
                pedido.setEndereco(endereco);
                pedido.setDataHora(LocalDateTime.now());
                pedido.setValorTotal(BigDecimal.ZERO);
                pedidoRepository.save(pedido);

                CookieService.setCookie(response, "pedidoEmAndamento", String.valueOf(pedido.getId()), 3600);
            }

            if (pedido == null) return "redirect:/carrinho";

            // Salva no banco
            cliente.setMetodoPagamento(metodoPagamento);
            clienteRepository.save(cliente);

            // Salva também na sessão
            session.setAttribute("metodoPagamento", metodoPagamento);

            session.setAttribute("pedidoEmAndamento", pedido);
            return "redirect:/pedido/resumo";

        } catch (Exception e) {
            return "redirect:/carrinho";
        }
    }


}
