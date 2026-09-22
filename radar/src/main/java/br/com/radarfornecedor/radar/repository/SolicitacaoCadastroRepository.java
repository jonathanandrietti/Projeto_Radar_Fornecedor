package br.com.radarfornecedor.radar.repository;

import br.com.radarfornecedor.radar.model.SolicitacaoCadastro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitacaoCadastroRepository extends JpaRepository<SolicitacaoCadastro, Long> {
    
    // Buscar por CNPJ ou CPF
    Optional<SolicitacaoCadastro> findByCnpjOuCpf(String cnpjOuCpf);
    
    // Buscar por usuário
    Optional<SolicitacaoCadastro> findByUsuario(String usuario);
    
    // Listar pendentes (aguardando aprovação)
    List<SolicitacaoCadastro> findByAprovadoFalse();
    
    // Listar aprovadas
    List<SolicitacaoCadastro> findByAprovadoTrue();
}
