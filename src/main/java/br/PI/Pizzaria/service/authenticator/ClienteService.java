package br.PI.Pizzaria.service.authenticator;

import br.PI.Pizzaria.modelCliente.Cliente;
import br.PI.Pizzaria.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    public void atualizarCliente(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Atualiza os campos necessários
        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());
        clienteExistente.setGenero(clienteAtualizado.getGenero());

        // Só atualiza a senha se ela for diferente da atual
        if (clienteAtualizado.getSenha() != null && !clienteAtualizado.getSenha().isEmpty()) {
            // Verifica se a senha informada é diferente da já criptografada
            if (!passwordEncoder.matches(clienteAtualizado.getSenha(), clienteExistente.getSenha())) {
                String senhaCriptografada = passwordEncoder.encode(clienteAtualizado.getSenha());
                clienteExistente.setSenha(senhaCriptografada);
            }
            // Se for a mesma, mantém a senha existente (não recriptografa!)
        }

        clienteRepository.save(clienteExistente);
    }

    public void salvar(Cliente cliente) {
        // Criptografa a senha antes de salvar no banco de dados
        if (cliente.getSenha() != null && !cliente.getSenha().isEmpty()) {
            cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        }
        clienteRepository.save(cliente);
    }
}
