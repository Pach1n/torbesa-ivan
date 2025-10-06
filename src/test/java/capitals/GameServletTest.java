package capitals;

import net.ausiasmarch.capitals.controller.GameServlet;
import net.ausiasmarch.shared.model.UserBean;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameServletTest {
    private GameServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher dispatcher;
    private ServletContext context;

    @Before
    public void setUp() {
        servlet = new GameServlet();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        dispatcher = mock(RequestDispatcher.class);
        context = mock(ServletContext.class);
        when(request.getSession()).thenReturn(session);
        when(request.getServletContext()).thenReturn(context);
    }

    @Test
    public void testDoGetRedirectsIfNoSessionUser() throws Exception {
        when(session.getAttribute("sessionUser")).thenReturn(null);
        servlet.doGet(request, response);
        verify(response).sendRedirect("../shared/login.jsp");
    }

    @Test
    public void testDoGetForwardsIfSessionUser() throws Exception {
        UserBean user = new UserBean(1, "testuser", "hash");
        when(session.getAttribute("sessionUser")).thenReturn(user);
        when(request.getRequestDispatcher("game.jsp")).thenReturn(dispatcher);
        servlet.doGet(request, response);
        verify(request).setAttribute(eq("sessionUser"), eq(user));
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void testDoPostRedirectsIfNoSessionUser() throws Exception {
        when(session.getAttribute("sessionUser")).thenReturn(null);
        servlet.doPost(request, response);
        verify(response).sendRedirect("../shared/login.jsp");
    }

    @Test
    public void testDoPostSetsAttributesAndForwards() throws Exception {
        UserBean user = new UserBean(1, "testuser", "hash");
        when(session.getAttribute("sessionUser")).thenReturn(user);
        when(request.getParameter("country")).thenReturn("Spain");
        when(request.getParameter("capitalGuess")).thenReturn("");
        when(request.getRequestDispatcher("scores.jsp")).thenReturn(dispatcher);
        // El resto de dependencias (servicios, DAOs) deberÃ­an ser mockeadas para un test mÃ¡s robusto
        servlet.doPost(request, response);
        verify(request).setAttribute(eq("username"), eq("testuser"));
        verify(dispatcher).forward(request, response);
    }
}
