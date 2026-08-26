package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Cliente;
import br.com.radarfornecedor.radar.model.Usuario;
import br.com.radarfornecedor.radar.model.TipoUsuario;
import br.com.radarfornecedor.radar.model.Fornecedor;
import br.com.radarfornecedor.radar.model.Representante;
import br.com.radarfornecedor.radar.repository.ClienteRepository;
import br.com.radarfornecedor.radar.repository.FornecedorRepository;
import br.com.radarfornecedor.radar.repository.RepresentanteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final FornecedorRepository fornecedorRepository;
    private final RepresentanteRepository representanteRepository;

    public ClienteService(ClienteRepository clienteRepository, FornecedorRepository fornecedorRepository, RepresentanteRepository representanteRepository) {
        this.clienteRepository = clienteRepository;
        this.fornecedorRepository = fornecedorRepository;
        this.representanteRepository = representanteRepository;
    }

    public Cliente cadastrar(Cliente cliente) {
        if (cliente.getCpfCnpj() == null || cliente.getCpfCnpj().replaceAll("\\D", "").length() != 11) {
            throw new RuntimeException("Clientes permitidos apenas com CPF (11 dígitos). CNPJ não é permitido.");
        }
        cliente.setCpfCnpj(cliente.getCpfCnpj().replaceAll("\\D", ""));
        cliente.setTipoPessoa("PF");
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarTodos(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            if (usuario.getTipo() == TipoUsuario.COMPRADOR) {
                return java.util.Collections.emptyList();
            }
            if (usuario.getTipo() == TipoUsuario.CLIENTE) {
                String cpfClean = usuario.getUsername().replaceAll("\\D", "");
                // Find Client with matching CPF
                Optional<Cliente> clienteOpt = clienteRepository.findAll().stream()
                        .filter(c -> c.getCpfCnpj() != null && c.getCpfCnpj().replaceAll("\\D", "").equals(cpfClean))
                        .findFirst();
                
                // Fallback for default 'cliente' login
                if (clienteOpt.isEmpty()) {
                    clienteOpt = clienteRepository.findByTipoPessoa("PF").stream().findFirst();
                }
                
                // Ultimate fallback
                if (clienteOpt.isEmpty()) {
                    clienteOpt = clienteRepository.findAll().stream().findFirst();
                }

                if (clienteOpt.isPresent()) {
                    return List.of(clienteOpt.get());
                } else {
                    return List.of();
                }
            }
            if (usuario.getTipo() == TipoUsuario.FORNECEDOR) {
                String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
                Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjClean);
                
                // Fallback for default 'fornecedor' login
                if (fornecedorOpt.isEmpty()) {
                    fornecedorOpt = fornecedorRepository.findAll().stream().findFirst();
                }

                if (fornecedorOpt.isPresent()) {
                    Fornecedor fornecedor = fornecedorOpt.get();
                    // If the supplier accepts CPF, return all clients.
                    // Otherwise, return only PJ clients (excluding CPF/PF clients).
                    if (Boolean.TRUE.equals(fornecedor.getAceitaCpf())) {
                        return clienteRepository.findAll();
                    } else {
                        return clienteRepository.findByTipoPessoa("PJ");
                    }
                }
            } else if (usuario.getTipo() == TipoUsuario.REPRESENTANTE) {
                String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
                // Find Representative matching CNPJ
                Optional<Representante> repOpt = representanteRepository.findAll().stream()
                        .filter(r -> r.getCnpj() != null && r.getCnpj().replaceAll("\\D", "").equals(cnpjClean))
                        .findFirst();
                
                // Fallback for default 'representante' login
                if (repOpt.isEmpty()) {
                    repOpt = representanteRepository.findAll().stream().findFirst();
                }

                if (repOpt.isPresent()) {
                    Representante rep = repOpt.get();
                    String cnpjFornecedor = rep.getCnpjFornecedor();
                    if (cnpjFornecedor != null) {
                        Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpj(cnpjFornecedor.replaceAll("\\D", ""));
                        
                        // Fallback to first Supplier if not found
                        if (fornecedorOpt.isEmpty()) {
                            fornecedorOpt = fornecedorRepository.findAll().stream().findFirst();
                        }

                        if (fornecedorOpt.isPresent()) {
                            Fornecedor fornecedor = fornecedorOpt.get();
                            // If represented supplier accepts CPF, return all clients.
                            // Otherwise, return only PJ clients (excluding CPF/PF).
                            if (Boolean.TRUE.equals(fornecedor.getAceitaCpf())) {
                                return clienteRepository.findAll();
                            } else {
                                return clienteRepository.findByTipoPessoa("PJ");
                            }
                        }
                    }
                }
                // Fallback if no representative/supplier details can be parsed
                return clienteRepository.findByTipoPessoa("PJ");
            }
        }
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Cliente atualizar(Long id, Cliente dadosNovos) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));

        if (dadosNovos.getCpfCnpj() == null || dadosNovos.getCpfCnpj().replaceAll("\\D", "").length() != 11) {
            throw new RuntimeException("Clientes permitidos apenas com CPF (11 dígitos). CNPJ não é permitido.");
        }

        existente.setNome(dadosNovos.getNome());
        existente.setStatus(dadosNovos.getStatus());
        existente.setCpfCnpj(dadosNovos.getCpfCnpj().replaceAll("\\D", ""));
        existente.setTipoPessoa("PF");

        return clienteRepository.save(existente);
    }

    public void excluir(Long id) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
        existente.setStatus("INATIVO");
        clienteRepository.save(existente);
    }
}
