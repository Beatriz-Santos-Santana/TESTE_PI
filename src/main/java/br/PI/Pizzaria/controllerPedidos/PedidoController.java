package br.PI.Pizzaria.controllerPedidos;

import br.PI.Pizzaria.modelCarrinho.ItemCarrinho;
import br.PI.Pizzaria.modelPedidos.StatusPedido;

import br.PI.Pizzaria.modelCliente.Cliente;
import br.PI.Pizzaria.modelCliente.Endereco;
import br.PI.Pizzaria.modelPedidos.ItemPedidos;
import br.PI.Pizzaria.modelPedidos.Pedidos;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    // Método para mostrar o resumo do pedido
    @GetMapping("/resumo")
    public String mostrarResumoPedido(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("usuarioLogado");
        Endereco endereco = (Endereco) session.getAttribute("enderecoSelecionado");
        List<ItemCarrinho> itensCarrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
        String metodoPagamento = (String) session.getAttribute("metodoPagamento");

        if (cliente == null || endereco == null || itensCarrinho == null || itensCarrinho.isEmpty()) {
            return "redirect:/carrinho";
        }

        BigDecimal total = BigDecimal.ZERO;
        for (ItemCarrinho item : itensCarrinho) {
            if (item.getProduto() == null) {
                System.out.println("Produto nulo encontrado para o item com ID: " + item.getId());
            } else {
                total = total.add(BigDecimal.valueOf(item.getValor()).multiply(BigDecimal.valueOf(item.getQuantidade())));
            }
        }

        BigDecimal frete = BigDecimal.valueOf(10); // Exemplo de frete fixo
        BigDecimal totalFinal = total.add(frete);

        model.addAttribute("cliente", cliente);
        model.addAttribute("endereco", endereco);
        model.addAttribute("itensPedido", itensCarrinho);
        model.addAttribute("total", total);
        model.addAttribute("frete", frete);
        model.addAttribute("totalFinal", totalFinal);
        model.addAttribute("metodoPagamento", metodoPagamento);

        return "resumoPedido";
    }


    // Página de confirmação do pedido finalizado
    @PostMapping("/concluir")
    public String concluirPedido(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws UnsupportedEncodingException {
        String pedidoIdStr = CookieService.getCookie(request, "pedidoEmAndamento");

        if (pedidoIdStr == null) {
            return "redirect:/cardapio";
        }

        try {
            Long pedidoId = Long.parseLong(pedidoIdStr);
            Pedidos pedido = pedidoRepository.findById(pedidoId).orElse(null);

            if (pedido == null) {
                return "redirect:/cardapio";
            }

            // Buscar dados da sessão para calcular total
            List<ItemCarrinho> itensCarrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");

            if (itensCarrinho == null || itensCarrinho.isEmpty()) {
                return "redirect:/carrinho";
            }

            BigDecimal total = BigDecimal.ZERO;
            for (ItemCarrinho item : itensCarrinho) {
                total = total.add(BigDecimal.valueOf(item.getValor()).multiply(BigDecimal.valueOf(item.getQuantidade())));
            }

            BigDecimal frete = BigDecimal.valueOf(10);
            BigDecimal totalFinal = total.add(frete);

            // Atualiza os dados do pedido
            pedido.setData(LocalDateTime.now());
            pedido.setValorTotal(totalFinal);
            pedido.setStatus(StatusPedido.PENDENTE); // ou outro status que você use
            pedido.setMetodoPagamento((String) session.getAttribute("metodoPagamento"));

            session.removeAttribute("carrinho");
            pedidoRepository.save(pedido);

            // Limpa cookie do pedido
            CookieService.setCookie(response, "pedidoEmAndamento", "", 0);

            return "redirect:/pedido/finalizado?pedidoId=" + pedido.getId();

        } catch (NumberFormatException e) {
            return "redirect:/cardapio";
        }
    }@GetMapping("/finalizado")
    public String pedidoFinalizado(Model model, @RequestParam(required = false) Long pedidoId) {
        try {
            Pedidos pedido = pedidoRepository.findById(pedidoId)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

            List<ItemPedidos> itens = itemPedidoRepository.findByPedidoId(pedidoId);

            BigDecimal total = BigDecimal.ZERO;
            for (ItemPedidos item : itens) {
                total = total.add(item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
            }

            BigDecimal frete = BigDecimal.valueOf(10);
            BigDecimal totalFinal = total.add(frete);

            model.addAttribute("pedido", pedido);
            model.addAttribute("valor", totalFinal); // <- Atributo corrigido para o HTML acessar

            return "pedidoFinalizado";

        } catch (Exception e) {
            model.addAttribute("erro", "Erro ao recuperar o pedido: " + e.getMessage());
            return "pedidoFinalizado";
        }
    }
    @GetMapping("/gerenciar")
    public String listarPedidosParaGerencia(Model model) {
        List<Pedidos> pedidos = pedidoRepository.findAllByOrderByDataDesc(); // vamos criar esse método no repositório

        model.addAttribute("pedidos", pedidos);
        return "gerenciarPedidos"; // Criar um HTML com essa view
    }

    @GetMapping("/editar/{id}")
    public String editarPedido(@PathVariable Long id, Model model) {
        Pedidos pedido = pedidoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        model.addAttribute("pedido", pedido);
        return "editarPedido";
    }

    @PostMapping("/editar/{id}")
    public String salvarEdicaoPedido( HttpSession session,
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam BigDecimal valorTotal,
            @RequestParam String logradouro,
            @RequestParam String numero,
            @RequestParam String bairro,
            @RequestParam String cidade,
            @RequestParam String uf,
            @RequestParam String metodoPagamento
    ) {
        // Busca o pedido no banco de dados
        Pedidos pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));

        // Atualiza o status do pedido
        pedido.setStatus(StatusPedido.valueOf(status));

        // Atualiza o método de pagamento
        pedido.setMetodoPagamento(metodoPagamento);

        // Atualiza o endereço do pedido
        Endereco endereco = pedido.getEndereco();
        endereco.setLogradouro(logradouro);
        endereco.setNumero(numero);
        endereco.setBairro(bairro);
        endereco.setCidade(cidade);
        endereco.setUf(uf);

        // Atualiza o valor total do pedido
        pedido.setValorTotal(valorTotal);

        // Salva o pedido com as modificações
        pedidoRepository.save(pedido);


        // Redireciona para a página de gerenciamento de pedidos
        return "redirect:/pedido/gerenciar";
    }




}
