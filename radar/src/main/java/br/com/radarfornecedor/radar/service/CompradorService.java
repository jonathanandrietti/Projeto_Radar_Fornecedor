package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Comprador;
import br.com.radarfornecedor.radar.repository.CompradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompradorService {

    private final CompradorRepository compradorRepository;

    public CompradorService(CompradorRepository compradorRepository) {
        this.compradorRepository = compradorRepository;
    }

    public Comprador cadastrar(Comprador comprador) {
        Optional<Comprador> existente = compradorRepository.findByCnpj(comprador.getCnpj());
        if (existente.isPresent()) {
            throw new RuntimeException("Já existe um comprador cadastrado com este CNPJ.");
        }

        if (comprador.getStatus() == null) {
            comprador.setStatus("EM_ANALISE");
        }
        if (comprador.getPontuacaoRisco() == null) {
            comprador.setPontuacaoRisco(0.0);
        }

        return compradorRepository.save(comprador);
    }

    public List<Comprador> listarTodos() {
        return compradorRepository.findAll();
    }

    public List<Comprador> listarTodos(jakarta.servlet.http.HttpSession session) {
        br.com.radarfornecedor.radar.model.Usuario usuario = (br.com.radarfornecedor.radar.model.Usuario) session.getAttribute("usuario");
        if (usuario != null) {
            if (usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.CLIENTE) {
                return java.util.Collections.emptyList();
            }
            if (usuario.getTipo() == br.com.radarfornecedor.radar.model.TipoUsuario.COMPRADOR) {
                String cnpjClean = usuario.getUsername().replaceAll("\\D", "");
                Optional<Comprador> compradorOpt = compradorRepository.findByCnpj(cnpjClean);
                
                // Fallback for default 'comprador' login
                if (compradorOpt.isEmpty()) {
                    compradorOpt = compradorRepository.findAll().stream().findFirst();
                }

                if (compradorOpt.isPresent()) {
                    return List.of(compradorOpt.get());
                } else {
                    return List.of();
                }
            }
        }
        return compradorRepository.findAll();
    }

    public Optional<Comprador> buscarPorId(Long id) {
        return compradorRepository.findById(id);
    }

    public Optional<Comprador> buscarPorCnpj(String cnpj) {
        return compradorRepository.findByCnpj(cnpj);
    }

    public Comprador atualizar(Long id, Comprador dadosNovos) {
        Comprador existente = compradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprador não encontrado com o ID: " + id));

        Optional<Comprador> comMesmoCnpj = compradorRepository.findByCnpj(dadosNovos.getCnpj());
        if (comMesmoCnpj.isPresent() && !comMesmoCnpj.get().getId().equals(id)) {
            throw new RuntimeException("Já existe outro comprador cadastrado com este CNPJ.");
        }

        existente.setNome(dadosNovos.getNome());
        existente.setCnpj(dadosNovos.getCnpj());
        existente.setStatus(dadosNovos.getStatus());
        existente.setPontuacaoRisco(dadosNovos.getPontuacaoRisco());
        
        // Atualizar campos de endereço
        existente.setCep(dadosNovos.getCep());
        existente.setLogradouro(dadosNovos.getLogradouro());
        existente.setNumero(dadosNovos.getNumero());
        existente.setComplemento(dadosNovos.getComplemento());
        existente.setBairro(dadosNovos.getBairro());
        existente.setCidade(dadosNovos.getCidade());
        existente.setEstado(dadosNovos.getEstado());
        existente.setLatitude(dadosNovos.getLatitude());
        existente.setLongitude(dadosNovos.getLongitude());
        existente.setCodCidade(dadosNovos.getCodCidade());

        return compradorRepository.save(existente);
    }

    public void excluir(Long id) {
        Comprador existente = compradorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comprador não encontrado com o ID: " + id));
        existente.setStatus("INATIVO");
        compradorRepository.save(existente);
    }
}
