package br.com.radarfornecedor.radar.repository;

import br.com.radarfornecedor.radar.model.Atividade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtividadeRepository extends JpaRepository<Atividade, Long> {
    Optional<Atividade> findByCnae(String cnae);
    Optional<Atividade> findByDescricao(String descricao);
    List<Atividade> findByAtivaTrue();
    List<Atividade> findBySecao(String secao);
}
