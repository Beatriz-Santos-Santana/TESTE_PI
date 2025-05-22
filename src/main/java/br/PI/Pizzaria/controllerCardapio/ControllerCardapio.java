package br.PI.Pizzaria.controllerCardapio;

import br.PI.Pizzaria.modelProdutos.Produto;
import br.PI.Pizzaria.repository.ProdutoRepository;
import br.PI.Pizzaria.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Controller
public class ControllerCardapio {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping("/cardapio")
    public String cardapio(
            @RequestParam(required = false) String pesquisa,
            @RequestParam(required = false) String categoria,
            HttpServletRequest request,
            Model model) {

        List<Produto> produtos;

        if (pesquisa != null && !pesquisa.isBlank()) {
            if (categoria != null && !categoria.isBlank()) {
                // Filtra por nome E categoria
                produtos = produtoRepository.findByNomeContainingIgnoreCaseAndNomeContainingIgnoreCaseAndAtivoTrue(pesquisa, categoria);
            } else {
                // Só pesquisa
                produtos = produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(pesquisa);
            }
        } else if (categoria != null && !categoria.isBlank()) {
            // Só categoria
            produtos = produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(categoria);
        } else {
            produtos = produtoRepository.findByAtivoTrue();
        }

        model.addAttribute("produtos", produtos);

        try {
            String nomeCliente = CookieService.getCookie(request, "clienteNome");
            model.addAttribute("nomeCliente", nomeCliente);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "cardapio";
    }



    @GetMapping("/detalheProduto")
    public String detalheProduto() {
        return "detalheProduto";
    }

    @GetMapping("/frete")
    public String frete() {
        return "frete";
    }
}
