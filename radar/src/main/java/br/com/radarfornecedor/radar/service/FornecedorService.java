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

    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        return fornecedorRepository.findByCnpj(cnpj);
    }
}
