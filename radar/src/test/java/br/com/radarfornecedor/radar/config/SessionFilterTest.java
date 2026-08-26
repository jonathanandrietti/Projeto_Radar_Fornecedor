package br.com.radarfornecedor.radar.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpSession;

import br.com.radarfornecedor.radar.model.Usuario;

import static org.junit.jupiter.api.Assertions.*;

public class SessionFilterTest {

    @Test
    public void whenAccessProtectedPageWithoutSession_thenRedirectToLogin() throws Exception {
        SessionFilter filter = new SessionFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.setRequestURI("/pages/fornecedores.html");

        filter.doFilter(request, response, filterChain);

        assertEquals("/login.html", response.getRedirectedUrl());
    }

    @Test
    public void whenAccessProtectedPageWithSession_thenChainContinues() throws Exception {
        SessionFilter filter = new SessionFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.setRequestURI("/pages/fornecedores.html");
        
        MockHttpSession session = new MockHttpSession();
        Usuario mockUsuario = new Usuario();
        mockUsuario.setUsername("admin");
        session.setAttribute("usuario", mockUsuario);
        request.setSession(session);

        filter.doFilter(request, response, filterChain);

        assertNull(response.getRedirectedUrl());
    }

    @Test
    public void whenAccessPublicPageWithoutSession_thenChainContinues() throws Exception {
        SessionFilter filter = new SessionFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        request.setRequestURI("/login.html");

        filter.doFilter(request, response, filterChain);

        assertNull(response.getRedirectedUrl());
    }
}
