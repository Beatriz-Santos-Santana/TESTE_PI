package br.PI.Pizzaria.controllerClientes;

import br.PI.Pizzaria.modelCliente.Cliente;
import br.PI.Pizzaria.repository.ClienteRepository;
import br.PI.Pizzaria.service.authenticator.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class EditarClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/editarCliente/{id}")
    public String editarCliente(@PathVariable("id") Long id, Model model) {
        Optional<Cliente> clienteOptional = clienteRepository.findById(id);
        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();

            if (cliente.getEnderecos() == null || cliente.getEnderecos().isEmpty()) {
                cliente.adicionarEnderecoVazio(); // <-- veja mais abaixo
            }

            model.addAttribute("cliente", cliente);
            return "editarCliente";
        } else {
            return "redirect:/login";
        }
    }


    @PostMapping("/editar")
    public String salvarEdicaoCliente(@ModelAttribute Cliente cliente) {
        Optional<Cliente> clienteOriginalOpt = clienteRepository.findById(cliente.getId());

        if (clienteOriginalOpt.isPresent()) {
            Cliente clienteOriginal = clienteOriginalOpt.get();

            // Atualiza os campos básicos
            clienteOriginal.setNome(cliente.getNome());
            clienteOriginal.setDataNascimento(cliente.getDataNascimento());
            clienteOriginal.setGenero(cliente.getGenero());

            // Atualiza a senha somente se foi alterada
            if (cliente.getSenha() != null && !cliente.getSenha().isBlank()) {
                if (!cliente.getSenha().equals(cliente.getConfirmarSenha())) {
                    return "redirect:/editarCliente/" + cliente.getId(); // Tratamento simples
                }
                clienteOriginal.setSenha(passwordEncoder.encode(cliente.getSenha()));
            }

            // Atualiza endereços
            if (cliente.getEnderecos() != null) {
                // Remove endereços antigos (se for o caso)
                clienteOriginal.getEnderecos().clear();

                // Associa novamente os novos endereços
                cliente.getEnderecos().forEach(e -> {
                    e.setCliente(clienteOriginal);
                    clienteOriginal.getEnderecos().add(e);
                });
            }

            clienteRepository.save(clienteOriginal);
        }

        return "redirect:/cardapio";
    }

    private String criptografarSenha(String senha) {
        return passwordEncoder.encode(senha);
    }

}
