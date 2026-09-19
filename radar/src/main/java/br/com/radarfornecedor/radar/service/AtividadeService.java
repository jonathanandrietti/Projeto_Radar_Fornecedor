package br.com.radarfornecedor.radar.service;

import br.com.radarfornecedor.radar.model.Atividade;
import br.com.radarfornecedor.radar.repository.AtividadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AtividadeService {

    @Autowired
    private AtividadeRepository atividadeRepository;

    public Atividade cadastrar(Atividade atividade) {
        return atividadeRepository.save(atividade);
    }

    public List<Atividade> listarTodas() {
        return atividadeRepository.findAll();
    }

    public List<Atividade> listarAtivas() {
        return atividadeRepository.findByAtivaTrue();
    }

    public Optional<Atividade> buscarPorId(Long id) {
        return atividadeRepository.findById(id);
    }

    public Optional<Atividade> buscarPorCnae(String cnae) {
        return atividadeRepository.findByCnae(cnae);
    }

    public Optional<Atividade> buscarPorDescricao(String descricao) {
        return atividadeRepository.findByDescricao(descricao);
    }

    public List<Atividade> buscarPorSecao(String secao) {
        return atividadeRepository.findBySecao(secao);
    }

    public Atividade atualizar(Long id, Atividade atividade) {
        return atividadeRepository.findById(id).map(ativ -> {
            ativ.setCnae(atividade.getCnae());
            ativ.setDescricao(atividade.getDescricao());
            ativ.setSecao(atividade.getSecao());
            ativ.setDivisao(atividade.getDivisao());
            ativ.setAtiva(atividade.getAtiva());
            return atividadeRepository.save(ativ);
        }).orElseThrow(() -> new RuntimeException("Atividade não encontrada com id: " + id));
    }

    public void excluir(Long id) {
        atividadeRepository.deleteById(id);
    }

    public void desativar(Long id) {
        atividadeRepository.findById(id).ifPresent(ativ -> {
            ativ.setAtiva(false);
            atividadeRepository.save(ativ);
        });
    }
}
