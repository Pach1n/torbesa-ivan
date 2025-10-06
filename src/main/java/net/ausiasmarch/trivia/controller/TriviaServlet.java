package net.ausiasmarch.trivia.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet(urlPatterns = {"/trivia"}) // la API va en otro servlet distinto
public class TriviaServlet extends HttpServlet {

    private static class Question {
        final String text; final List<String> options; final String correct;
        Question(String t, List<String> o, String c) { text = t; options = o; correct = c; }
    }

    // Banco LOCAL (respaldo)
    private static final List<Question> BANK = Arrays.asList(
        new Question("¿Capital de Francia?", Arrays.asList("Madrid", "París", "Roma", "Berlín"), "París"),
        new Question("Símbolo químico del sodio", Arrays.asList("So", "Na", "S", "Sn"), "Na"),
        new Question("¿Cuál es el río más largo del mundo?", Arrays.asList("Nilo", "Amazonas", "Yangtsé", "Misisipi"), "Amazonas")
    );

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Requiere login
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionUser") == null) {
            response.sendRedirect(request.getContextPath() + "/shared/login.jsp");
            return;
        }

        // UTF-8 siempre
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // Reset opcional (?reset=1)
        if ("1".equals(request.getParameter("reset"))) {
            session.removeAttribute("trivia.idx");
            session.removeAttribute("trivia.correct");
            session.setAttribute("score", 0);
            session.setAttribute("streak", 0);
        }

        // HUD
        Integer score  = (Integer) session.getAttribute("score");   if (score  == null) score  = 0;
        Integer streak = (Integer) session.getAttribute("streak");  if (streak == null) streak = 0;
        request.setAttribute("score", score);
        request.setAttribute("streak", streak);

        // ¿Modo API? (?api=1) -> el JSP hace fetch a /api/question
        boolean useApi = "1".equals(request.getParameter("api"));
        if (useApi) {
            request.removeAttribute("question");
            request.removeAttribute("options");
            request.getRequestDispatcher("/mijuego/game.jsp").forward(request, response);
            return;
        }

        // Modo LOCAL: pregunta del banco
        Integer idx = (Integer) session.getAttribute("trivia.idx");
        if (idx == null) idx = 0;
        if (idx >= BANK.size()) idx = 0;

        Question q = BANK.get(idx);
        session.setAttribute("trivia.idx", idx);
        session.setAttribute("trivia.correct", q.correct);

        request.setAttribute("question", q.text);
        request.setAttribute("options", q.options);

        request.getRequestDispatcher("/mijuego/game.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Requiere login
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("sessionUser") == null) {
            response.sendRedirect(request.getContextPath() + "/shared/login.jsp");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        Integer score  = (Integer) session.getAttribute("score");   if (score  == null) score  = 0;
        Integer streak = (Integer) session.getAttribute("streak");  if (streak == null) streak = 0;

        String answer  = request.getParameter("answer");
        String correct = (String) session.getAttribute("trivia.correct");

        if (answer != null && answer.equals(correct)) { score++; streak++; }
        else { streak = 0; }

        session.setAttribute("score", score);
        session.setAttribute("streak", streak);

        Integer idx = (Integer) session.getAttribute("trivia.idx");
        if (idx == null) idx = 0;
        idx = (idx + 1) % BANK.size();
        session.setAttribute("trivia.idx", idx);

        response.sendRedirect(request.getContextPath() + "/trivia");
    }
}