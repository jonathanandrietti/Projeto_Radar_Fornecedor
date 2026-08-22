package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Fornecedor;
import br.com.radarfornecedor.radar.repository.FornecedorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FornecedorService {

    private final FornecedorRepository fornecedorRepository;

    public FornecedorService(FornecedorRepository fornecedorRepository) {
        this.fornecedorRepository = fornecedorRepository;
    }

    public Fornecedor cadastrar(Fornecedor fornecedor) {
        Optional<Fornecedor> existente = fornecedorRepository.findByCnpj(fornecedor.getCnpj());
        if (existente.isPresent()) {
            throw new RuntimeException("Já existe um fornecedor cadastrado com este CNPJ.");
        }

        if (fornecedor.getStatus() == null) {
            fornecedor.setStatus("EM_ANALISE");
        }
        if (fornecedor.getPontuacaoRisco() == null) {
            fornecedor.setPontuacaoRisco(0.0);
        }

        return fornecedorRepository.save(fornecedor);
    }

    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.findAll();
    }

    public Optional<Fornecedor> buscarPorId(Long id) {
        return fornecedorRepository.findById(id);
    }

    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        return fornecedorRepository.findByCnpj(cnpj);
    }

    public Fornecedor atualizar(Long id, Fornecedor dadosNovos) {
        Fornecedor existente = fornecedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com o ID: " + id));

        Optional<Fornecedor> comMesmoCnpj = fornecedorRepository.findByCnpj(dadosNovos.getCnpj());
        if (comMesmoCnpj.isPresent() && !comMesmoCnpj.get().getId().equals(id)) {
            throw new RuntimeException("Já existe outro fornecedor cadastrado com este CNPJ.");
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

        return fornecedorRepository.save(existente);
    }

    public void excluir(Long id) {
        if (!fornecedorRepository.existsById(id)) {
            throw new RuntimeException("Fornecedor não encontrado com o ID: " + id);
        }
        fornecedorRepository.deleteById(id);
    }
}
