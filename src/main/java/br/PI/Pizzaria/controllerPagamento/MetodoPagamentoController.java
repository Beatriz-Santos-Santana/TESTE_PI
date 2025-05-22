package br.PI.Pizzaria.controllerPagamento;

import br.PI.Pizzaria.modelCliente.Cliente;
import br.PI.Pizzaria.repository.ClienteRepository;
import ch.qos.logback.core.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class MetodoPagamentoController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/selecionarPagamento")
    public String selecionarPagamento(org.springframework.ui.Model model) {
        return "selecionarPagamento";
    }

    @PostMapping("/selecionarPagamento")
    public String salvarMetodoPagamento(@RequestParam("metodo") String metodo,
                                        @RequestParam("clienteId") Long clienteId,
                                        @RequestParam(required = false) String numeroCartao,
                                        @RequestParam(required = false) String validade,
                                        @RequestParam(required = false) String cvv,
                                        org.springframework.ui.Model model) {

        Optional<Cliente> clienteOptional = clienteRepository.findById(clienteId);
        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();

            cliente.setMetodoPagamento(metodo);

            if ("CREDITO".equalsIgnoreCase(metodo)) {
                cliente.setNomeTitular(numeroCartao); // (Corrija se necessário)
                cliente.setNumeroCartao(numeroCartao);
                cliente.setValidadeCartao(validade);
                cliente.setCodigoSeguranca(cvv);
            }

            clienteRepository.save(cliente);
        }

        return "redirect:/pedido/resumo";
    }
}
