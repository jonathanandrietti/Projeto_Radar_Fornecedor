package br.com.radarfornecedor.radar.config;

import br.com.radarfornecedor.radar.model.TipoUsuario;
import br.com.radarfornecedor.radar.model.Usuario;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SessionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        // Permitir acesso a H2 Console e arquivos públicos SEM autenticação
        if (uri.startsWith("/h2-console") || uri.startsWith("/api/login") || uri.startsWith("/api/validacao")) {
            chain.doFilter(request, response);
            return;
        }

        // Protect any request under /pages/, EXCEPT cadastro.html (public registration page)
        if (uri.startsWith("/pages/") && !uri.endsWith("cadastro.html")) {
            HttpSession session = httpRequest.getSession(false);
            if (session == null || session.getAttribute("usuario") == null) {
                // Not logged in, redirect to login page
                httpResponse.sendRedirect("/login.html");
                return;
            }

            // Check role authorization for admin page
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            if (uri.endsWith("admin.html") && usuario.getTipo() != TipoUsuario.ADMIN) {
                // Non-admin trying to access admin page, redirect to their home page
                httpResponse.sendRedirect(getRedirectUrlForTipo(usuario.getTipo()));
                return;
            }

            if (uri.endsWith("clientes.html") && usuario.getTipo() == TipoUsuario.COMPRADOR) {
                // Comprador trying to access clientes page, redirect to their home page
                httpResponse.sendRedirect(getRedirectUrlForTipo(usuario.getTipo()));
                return;
            }

            if (uri.endsWith("compradores.html") && usuario.getTipo() == TipoUsuario.CLIENTE) {
                // Cliente trying to access compradores page, redirect to their home page
                httpResponse.sendRedirect(getRedirectUrlForTipo(usuario.getTipo()));
                return;
            }

            if (uri.endsWith("clientes.html") && usuario.getTipo() == TipoUsuario.CLIENTE) {
                httpResponse.sendRedirect(getRedirectUrlForTipo(usuario.getTipo()));
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String getRedirectUrlForTipo(TipoUsuario tipo) {
        if (tipo == null) return "/login.html";
        switch (tipo) {
            case FORNECEDOR:
                return "/pages/fornecedores.html";
            case COMPRADOR:
                return "/pages/compradores.html";
            case REPRESENTANTE:
                return "/pages/representantes.html";
            case CLIENTE:
                return "/pages/produtos.html";
            case ADMIN:
                return "/pages/admin.html";
            default:
                return "/pages/fornecedores.html";
        }
    }
}
