package br.com.radarfornecedor.radar.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Consulta a tabela de cidades que será preenchida com a carga oficial do IBGE. */
@RestController
@RequestMapping("/api/cidades")
public class CidadeController {

    private final JdbcTemplate jdbcTemplate;

    public CidadeController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/ibge/{codigoIbge}")
    public ResponseEntity<CidadeResponse> buscarPorCodigoIbge(@PathVariable Integer codigoIbge) {
        String sql = "SELECT CodCidade, Cidade, UF, CodCidadeIBGE FROM Cidades WHERE CodCidadeIBGE = ?";
        List<CidadeResponse> cidades = jdbcTemplate.query(sql,
                (rs, linha) -> new CidadeResponse(
                        rs.getLong("CodCidade"), rs.getString("Cidade"),
                        rs.getString("UF"), rs.getInt("CodCidadeIBGE")), codigoIbge);
        return cidades.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(cidades.get(0));
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CidadeResponse {
        private Long codCidade;
        private String cidade;
        private String uf;
        private Integer codCidadeIBGE;
    }
}
