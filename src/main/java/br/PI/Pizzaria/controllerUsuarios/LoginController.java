package br.PI.Pizzaria.controllerUsuarios;

import br.PI.Pizzaria.modelUsuarios.Role;
import br.PI.Pizzaria.modelUsuarios.Usuario;
import br.PI.Pizzaria.repository.UsuarioRepository;
import br.PI.Pizzaria.service.CookieService;
import br.PI.Pizzaria.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@Controller //define que a tela de Login é a principal - primeira de acesso
public class LoginController {

    @Autowired
    private UsuarioRepository ur;
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String dashboard() {
        return "index";

    }

    @PostMapping("/logar")
    public String loginUsuario(Usuario usuario, Model model, HttpSession session, HttpServletResponse response) {
        try {
            Usuario usuarioLogado = usuarioService.validarLogin(usuario.getEmail(), usuario.getSenha());

            session.setAttribute("usuarioId", usuarioLogado.getId());
            session.setAttribute("nomeUsuario", usuarioLogado.getNome());
            session.setAttribute("role", usuarioLogado.getRole());

            if (usuarioLogado.getRole() == Role.ADMIN) {
                return "redirect:/adminDashboard";
            } else if (usuarioLogado.getRole() == Role.ESTOQUISTA) {
                return "redirect:/estoqueDashboard";
            }

            return "redirect:/"; // ou alguma tela padrão

        } catch (Exception e) {
            model.addAttribute("erro", e.getMessage());
            return "login";
        }
    }


    @GetMapping("/adminDashboard")
    public String adminDashboard(HttpSession session) {
        Role role = (Role) session.getAttribute("role");

        if (role != null && role == Role.ADMIN) {
            return "adminDashboard"; // Página do admin
        } else {
            return "adminDashboard"; // Página de erro
        }

    }

    @GetMapping("/estoqueDashboard")
    public String estoqueDashboard(HttpSession session) {
        Role role = (Role) session.getAttribute("role");

        if (role != null && (role == Role.ADMIN || role == Role.ESTOQUISTA)) {
            return "estoqueDashboard"; // Página do estoque
        } else {
            return "estoqueDashboard";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate(); // limpa tudo
        return "redirect:/login";
    }

    @GetMapping("/cadastroUsuario")
    public String cadastro() {
        return "cadastroUsuario";
    }

    @RequestMapping(value = "/cadastroUsuario", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> cadastroUsuario(@RequestBody @Valid Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Dados inválidos");
        }

        usuarioService.salvarUsuario(usuario); // Aqui é o ponto chave
        return ResponseEntity.ok("Usuário cadastrado com sucesso!");
    }

}






