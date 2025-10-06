package net.ausiasmarch.trivia.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TriviaService {

    private static final String API_URL = "https://opentdb.com/api.php?amount=%d&type=multiple";
    private static final String LOCAL_RESOURCE = "/trivia/preguntas.json";

    private final Gson gson = new Gson();

    public static class TriviaQuestion implements Serializable {
        private static final long serialVersionUID = 1L;
        public final String question;
        public final List<String> options;
        public final String correct;

        public TriviaQuestion(String question, List<String> options, String correct) {
            this.question = question;
            this.options = options;
            this.correct = correct;
        }
    }

    public TriviaQuestion fetchQuestion() throws Exception {
        List<TriviaQuestion> batch = fetchBatch(1);
        if (batch.isEmpty()) throw new IllegalStateException("No se pudo obtener ninguna pregunta");
        return batch.get(0);
    }

    public List<TriviaQuestion> fetchBatch(int requestedAmount) throws Exception {
        int amount = Math.max(1, Math.min(10, requestedAmount));
        try {
            List<TriviaQuestion> apiQuestions = fetchFromApi(amount);
            if (!apiQuestions.isEmpty()) return apiQuestions;
        } catch (Exception ignored) {}
        List<TriviaQuestion> localQuestions = fetchFromLocal();
        if (localQuestions.isEmpty()) {
            throw new IllegalStateException("No hay preguntas disponibles (API y respaldo fallidos)");
        }
        Collections.shuffle(localQuestions);
        return new ArrayList<>(localQuestions.subList(0, Math.min(amount, localQuestions.size())));
    }

    private List<TriviaQuestion> fetchFromApi(int amount) throws Exception {
        String body = httpGet(String.format(API_URL, amount));
        JsonObject root = gson.fromJson(body, JsonObject.class);
        JsonArray results = (root != null) ? root.getAsJsonArray("results") : null;
        if (results == null || results.size() == 0) return Collections.emptyList();

        List<TriviaQuestion> questions = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            JsonObject obj = results.get(i).getAsJsonObject();
            String question = htmlUnescape(obj.get("question").getAsString());
            String correct  = htmlUnescape(obj.get("correct_answer").getAsString());

            List<String> options = new ArrayList<>();
            JsonArray incorrect = obj.getAsJsonArray("incorrect_answers");
            if (incorrect != null) {
                for (int j = 0; j < incorrect.size(); j++) {
                    options.add(htmlUnescape(incorrect.get(j).getAsString()));
                }
            }
            options.add(correct);
            Collections.shuffle(options);
            questions.add(new TriviaQuestion(question, options, correct));
        }
        return questions;
    }

    private List<TriviaQuestion> fetchFromLocal() throws IOException {
        InputStream in = TriviaService.class.getResourceAsStream(LOCAL_RESOURCE);
        if (in == null) return Collections.emptyList();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            for (String line; (line = reader.readLine()) != null; ) sb.append(line);

            JsonArray results = gson.fromJson(sb.toString(), JsonArray.class);
            if (results == null || results.size() == 0) return Collections.emptyList();

            List<TriviaQuestion> questions = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                JsonObject obj = results.get(i).getAsJsonObject();
                String question = obj.get("question").getAsString();
                String correct  = obj.get("correct").getAsString();

                List<String> options = new ArrayList<>();
                JsonArray incorrect = obj.getAsJsonArray("incorrect");
                if (incorrect != null) {
                    for (int j = 0; j < incorrect.size(); j++) {
                        options.add(incorrect.get(j).getAsString());
                    }
                }
                options.add(correct);
                Collections.shuffle(options);
                questions.add(new TriviaQuestion(question, options, correct));
            }
            return questions;
        }
    }

    private String httpGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setConnectTimeout(8000);
        con.setReadTimeout(8000);
        con.setRequestMethod("GET");
        int code = con.getResponseCode();
        if (code != 200) throw new IOException("API HTTP " + code);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            for (String line; (line = br.readLine()) != null; ) sb.append(line);
            return sb.toString();
        }
    }

    private String htmlUnescape(String s) {
        if (s == null) return null;
        return s.replace("&quot;", "\"")
                .replace("&#039;", "'")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&ldquo;", "\"")
                .replace("&rdquo;", "\"")
                .replace("&lsquo;", "'")
                .replace("&rsquo;", "'")
                .replace("&hellip;", "...")
                .replace("&eacute;", "e")
                .replace("&aacute;", "a")
                .replace("&iacute;", "i")
                .replace("&oacute;", "o")
                .replace("&uacute;", "u")
                .replace("&ntilde;", "n");
    }
}
