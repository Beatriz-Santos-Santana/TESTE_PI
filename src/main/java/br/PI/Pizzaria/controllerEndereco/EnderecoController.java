package br.PI.Pizzaria.controllerEndereco;

import br.PI.Pizzaria.modelCliente.Cliente;
import br.PI.Pizzaria.modelCliente.Endereco;
import br.PI.Pizzaria.modelPedidos.Pedidos;
import br.PI.Pizzaria.repository.ClienteRepository;
import br.PI.Pizzaria.repository.EnderecoRepository;
import br.PI.Pizzaria.repository.PedidoRepository;
import br.PI.Pizzaria.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

@Controller
public class EnderecoController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @GetMapping("/enderecos")
    public String mostrarEnderecos(Model model, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("usuarioLogado");

        if (cliente == null) {
            return "redirect:/loginCliente";
        }

        cliente = clienteRepository.findByEmailWithEnderecos(cliente.getEmail());

        Endereco enderecoSelecionado = (Endereco) session.getAttribute("enderecoSelecionado");

        model.addAttribute("enderecos", cliente.getEnderecos());
        model.addAttribute("enderecoSelecionado", enderecoSelecionado); // <-- esta linha é nova

        return "selecionarEndereco";
    }


    @PostMapping("/enderecos/selecionar")
    public String selecionarEndereco(@RequestParam(value = "enderecoSelecionado", required = false) Long enderecoId,
                                     HttpSession session,
                                     HttpServletRequest request,
                                     HttpServletResponse response) throws UnsupportedEncodingException {

        Cliente cliente = (Cliente) session.getAttribute("usuarioLogado");

        if (cliente == null) {
            System.out.println("Cliente não logado, redirecionando para login.");
            return "redirect:/loginCliente";
        }

        if (enderecoId != null) {
            Endereco enderecoSelecionado = enderecoRepository.findById(enderecoId).orElse(null);
            if (enderecoSelecionado != null) {
                session.setAttribute("enderecoSelecionado", enderecoSelecionado);
                System.out.println("Endereço selecionado: " + enderecoSelecionado);
            } else {
                System.out.println("Endereço não encontrado: " + enderecoId);
            }
        }

        Endereco enderecoNaSessao = (Endereco) session.getAttribute("enderecoSelecionado");
        if (enderecoNaSessao == null) {
            session.setAttribute("erroEndereco", "Por favor, selecione um endereço para continuar.");
            return "redirect:/enderecos";
        }

        return "redirect:/selecionarPagamento"; // Vai gerar nova requisição onde o cookie estará presente
    }

    @PostMapping("/enderecos/adicionar")
    public String adicionarNovoEndereco(@RequestParam("cepNovo") String cep,
                                        @RequestParam("logradouroNovo") String logradouro,
                                        @RequestParam("numeroNovo") String numero,
                                        @RequestParam("bairroNovo") String bairro,
                                        @RequestParam("complementoNovo") String complemento,
                                        @RequestParam("cidadeNovo") String cidade,
                                        @RequestParam("ufNovo") String uf,
                                        @RequestParam(value = "principal", defaultValue = "false") boolean principal,
                                        HttpSession session,
                                        HttpServletResponse response) throws UnsupportedEncodingException {

        Cliente cliente = (Cliente) session.getAttribute("usuarioLogado");

        if (cliente == null) {
            return "redirect:/loginCliente";
        }

        Endereco novoEndereco = new Endereco();
        novoEndereco.setCep(cep);
        novoEndereco.setLogradouro(logradouro);
        novoEndereco.setNumero(numero);
        novoEndereco.setBairro(bairro);
        novoEndereco.setComplemento(complemento);
        novoEndereco.setCidade(cidade);
        novoEndereco.setUf(uf);
        novoEndereco.setPrincipal(principal);
        novoEndereco.setCliente(cliente);

        enderecoRepository.save(novoEndereco);

        if (principal) {
            session.setAttribute("enderecoSelecionado", novoEndereco);
        }
        return "redirect:/selecionarPagamento";
    }
}