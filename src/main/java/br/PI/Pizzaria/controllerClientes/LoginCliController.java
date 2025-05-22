package br.PI.Pizzaria.controllerClientes;

import br.PI.Pizzaria.modelCliente.Cliente;
import br.PI.Pizzaria.modelCliente.Endereco;
import br.PI.Pizzaria.repository.ClienteRepository;
import br.PI.Pizzaria.service.CookieService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


@Controller
public class LoginCliController {

    @Autowired
    private ClienteRepository ur;

    @GetMapping("/loginCliente")
    public String loginCliente() {
        return "loginCliente";
    }

    @GetMapping("/cadastroCliente")
    public String cadastroCliente(@RequestParam(value = "retorno", required = false) String retorno, Model model) {
        model.addAttribute("retorno", retorno); // envia para a view
        return "cadastroCliente";
    }

    @PostMapping("/loginCliente")
    public String processaLogin(@RequestParam String email, @RequestParam String senha, Model model, HttpServletResponse response, HttpSession session) {
        Cliente cliente = ur.findByEmail(email);

        if (cliente != null && passwordEncoder.matches(senha, cliente.getSenha())) {
            try {
                CookieService.setCookie(response, "clienteNome", cliente.getNome(), 3600); // Cookie por 1 hora
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            session.setAttribute("usuarioLogado", cliente);
            return "redirect:/cardapio";
        }

        model.addAttribute("erro", "Email ou senha inválidos");
        return "loginCliente";
    }


    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/cadastroCliente")
    public String cadastroCliente(@Valid Cliente cliente,
                                  BindingResult result,
                                  Model model,
                                  HttpSession session,
                                  @RequestParam(value = "retorno", required = false) String retorno,
                                  @RequestParam("cepEntrega[]") List<String> ceps,
                                  @RequestParam("logradouroEntrega[]") List<String> logradouros,
                                  @RequestParam("numeroEntrega[]") List<String> numeros,
                                  @RequestParam("bairroEntrega[]") List<String> bairros,
                                  @RequestParam("cidadeEntrega[]") List<String> cidades,
                                  @RequestParam("ufEntrega[]") List<String> ufs,
                                  @RequestParam("principalEndereco") int indicePrincipal,
                                  @RequestParam("complementoEntrega[]") List<String> complementos
    )
    {
        if (ur.findByEmail(cliente.getEmail()) != null) {
            model.addAttribute("erroEmail", "Este e-mail já está em uso.");
            return "cadastroCliente";
        }

        if (result.hasErrors()) {
            for (FieldError error : result.getFieldErrors()) {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            }
            return "cadastroCliente";
        }

        if (cliente.getDataNascimento() == null) {
            result.rejectValue("dataNascimento", "error.cliente", "Data de nascimento é obrigatória.");
            return "cadastroCliente";
        }

        if (ur.findByCpf(cliente.getCpf()) != null) {
            model.addAttribute("erroCpf", "Este CPF já está cadastrado.");
            return "cadastroCliente";
        }

        if (!cliente.getSenha().equals(cliente.getConfirmarSenha())) {
            result.rejectValue("confirmarSenha", "erro.confirmarSenha", "As senhas não conferem.");
            return "cadastroCliente";
        }

        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));

        // Criando os endereços de entrega
        List<Endereco> enderecos = new ArrayList<>();
        for (int i = 0; i < ceps.size(); i++) {
            Endereco endereco = new Endereco();
            endereco.setCep(ceps.get(i));
            endereco.setLogradouro(logradouros.get(i));
            endereco.setNumero(numeros.get(i));
            endereco.setBairro(bairros.get(i));
            endereco.setCidade(cidades.get(i));
            endereco.setUf(ufs.get(i));
            endereco.setPrincipal(i == indicePrincipal); // true se for o principal
            endereco.setComplemento(complementos.get(i));
            endereco.setCliente(cliente); // relacionamento bidirecional
            enderecos.add(endereco);
        }

        cliente.setEnderecos(enderecos);

        ur.save(cliente);

        session.setAttribute("usuarioLogado", cliente); // loga o cliente automaticamente

        if ("carrinho".equals(retorno)) {
            return "redirect:/carrinho";
        }

        return "redirect:/loginCliente"; // fallback padrão
    }

    @GetMapping("/logoutCliente")
    public String logoutCliente(HttpServletResponse response, HttpSession session) {
        try {
            CookieService.setCookie(response, "clienteNome", "", 0); // Deleta o cookie
            //CookieService.setCookie(response, "pedidoEmAndamento", "", 0);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        session.invalidate();
        return "redirect:/loginCliente";
    }
}



