package net.ausiasmarch.trivia.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@WebServlet(urlPatterns = { "/api/question", "/api/answer" })
public class TriviaApiServlet extends HttpServlet {

    private final Gson gson = new Gson();

    // Desescapa lo más común que devuelve OpenTDB (acentos y comillas incluidas)
    private static String unescape(String s) {
        if (s == null) return null;
        return s.replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&ldquo;","\"").replace("&rdquo;","\"")
                .replace("&lsquo;","'").replace("&rsquo;","'")
                .replace("&hellip;","...")
                .replace("&Aacute;","Á").replace("&Eacute;","É").replace("&Iacute;","Í")
                .replace("&Oacute;","Ó").replace("&Uacute;","Ú").replace("&Ntilde;","Ñ")
                .replace("&aacute;","á").replace("&eacute;","é").replace("&iacute;","í")
                .replace("&oacute;","ó").replace("&uacute;","ú").replace("&ntilde;","ñ")
                .replace("&uuml;","ü").replace("&Uuml;","Ü");
    }

    private void sendJson(HttpServletResponse resp, String json) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json; charset=UTF-8");
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        try (PrintWriter w = resp.getWriter()) {
            w.write(json);
        }
    }

    // GET /api/question?cat=18&difficulty=easy
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        // Si alguien llama GET a /api/answer, responde error amable
        if ("/api/answer".equals(req.getServletPath())) {
            sendJson(resp, "{\"error\":\"Use POST en /api/answer\"}");
            return;
        }

        String cat  = req.getParameter("cat");
        String diff = req.getParameter("difficulty");

        StringBuilder api = new StringBuilder("https://opentdb.com/api.php?amount=1&type=multiple");
        if (cat != null && !cat.isBlank()) {
            api.append("&category=").append(URLEncoder.encode(cat, StandardCharsets.UTF_8));
        }
        if (diff != null && !diff.isBlank()) {
            api.append("&difficulty=").append(URLEncoder.encode(diff, StandardCharsets.UTF_8));
        }

        JsonObject out;
        try {
            URL url = new URL(api.toString());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(8000);
            con.setReadTimeout(8000);
            con.setRequestMethod("GET");

            int code = con.getResponseCode();
            if (code != 200) throw new IOException("HTTP " + code);

            try (InputStream is = con.getInputStream();
                 InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                 BufferedReader br = new BufferedReader(isr)) {

                JsonObject apiJson = gson.fromJson(br, JsonObject.class);
                JsonArray results = apiJson.getAsJsonArray("results");
                if (results == null || results.size() == 0) {
                    throw new IllegalStateException("Sin resultados");
                }

                JsonObject q = results.get(0).getAsJsonObject();
                String question = unescape(q.get("question").getAsString());
                String correct  = unescape(q.get("correct_answer").getAsString());

                List<String> options = new ArrayList<>();
                JsonArray incorrect = q.getAsJsonArray("incorrect_answers");
                for (int i = 0; i < incorrect.size(); i++) {
                    options.add(unescape(incorrect.get(i).getAsString()));
                }
                options.add(correct);
                Collections.shuffle(options, new Random());
                int correctIndex = options.indexOf(correct);

                // Guarda solo el índice correcto en sesión
                HttpSession session = req.getSession(true);
                session.setAttribute("api.correctIndex", correctIndex);

                // Incluye puntuación actual (si existe)
                Integer score  = (Integer) session.getAttribute("score");  if (score == null) score = 0;
                Integer streak = (Integer) session.getAttribute("streak"); if (streak == null) streak = 0;

                out = new JsonObject();
                out.addProperty("question", question);
                out.add("options", gson.toJsonTree(options));
                out.addProperty("score",  score);
                out.addProperty("streak", streak);
            }
        } catch (Exception ex) {
            out = new JsonObject();
            out.addProperty("error", "No se pudo obtener pregunta (" + ex.getMessage() + ")");
        }

        sendJson(resp, gson.toJson(out));
    }

    // POST /api/answer (body: choice=0..3)
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!"/api/answer".equals(req.getServletPath())) {
            sendJson(resp, "{\"error\":\"Endpoint no válido\"}");
            return;
        }

        HttpSession session = req.getSession(true);

        Integer correctIndex = (Integer) session.getAttribute("api.correctIndex");
        if (correctIndex == null) correctIndex = -1;

        String choiceStr = req.getParameter("choice");
        int choice = -1;
        try { choice = Integer.parseInt(choiceStr); } catch (Exception ignore) {}

        Integer score  = (Integer) session.getAttribute("score");  if (score == null) score = 0;
        Integer streak = (Integer) session.getAttribute("streak"); if (streak == null) streak = 0;

        boolean ok = (choice == correctIndex);
        if (ok) { score++; streak++; } else { streak = 0; }

        session.setAttribute("score", score);
        session.setAttribute("streak", streak);

        JsonObject out = new JsonObject();
        out.addProperty("ok", ok);
        out.addProperty("score", score);
        out.addProperty("streak", streak);

        sendJson(resp, gson.toJson(out));
    }
}