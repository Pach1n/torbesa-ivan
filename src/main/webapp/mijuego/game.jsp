<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
String question  = (String) request.getAttribute("question");
java.util.List options = (java.util.List) request.getAttribute("options");

Integer score  = (Integer) request.getAttribute("score");
if (score == null) score = 0;

Integer streak = (Integer) request.getAttribute("streak");
if (streak == null) streak = 0;

boolean serverHasQuestion = (question != null && options != null && !options.isEmpty());
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Trivia</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
<style>
:root{
  /* Paleta accesible (alto contraste) */
  --bg:#0a0f1a;
  --fg:#eef3ff;           /* texto principal */
  --muted:#b8c7e6;        /* texto secundario */
  --card:#111a2b;
  --surface:#0e1830;      /* chips / HUD */
  --border:#2b3c66;

  --primary:#4ea1ff;      /* acción/links */
  --primary-900:#0e3259;  /* hover en oscuro */

  --success:#2ec27e;
  --danger:#ff6b6b;
}

html, body { background:var(--bg); color:var(--fg); }

a { color:var(--primary); text-decoration: none; }
a:hover { text-decoration: underline; }

.text-muted { color: var(--muted) !important; }

.navbar{
  background: linear-gradient(180deg, rgba(16,26,48,.82), rgba(16,26,48,.66));
  border-bottom:1px solid var(--border);
  backdrop-filter: blur(6px);
}

.card{
  background:var(--card);
  border:1px solid var(--border);
  border-radius:16px;
  box-shadow: 0 10px 24px rgba(0,0,0,.35);
}

.hud .item{
  background:var(--surface);
  padding:6px 12px;
  border-radius:10px;
  border:1px solid var(--border);
  color:var(--fg);
}
.hud strong{ color:var(--fg); }

.badge-mode{
  background:rgba(78,161,255,.18);
  border:1px solid rgba(78,161,255,.45);
  color:#d9e8ff;
}

/* Botones principales (mejor contraste que el default de Bootstrap en oscuro) */
.btn-primary{
  background:var(--primary);
  border-color:var(--primary);
  color:#071021;
}
.btn-primary:hover, .btn-primary:focus{
  background: #74b7ff;
  border-color:#74b7ff;
  color:#05101d;
}

/* Botón contorneado info -> más visible en oscuro */
.btn-outline-info{
  color: var(--fg);
  border-color: var(--primary);
}
.btn-outline-info:hover, .btn-outline-info:focus{
  background: var(--primary);
  color:#071021;
  border-color: var(--primary);
}

/* Botones de opciones */
.btn-option{
  width:100%;
  text-align:left;
  margin-bottom:12px;
  border:1px solid var(--border);
  color:var(--fg);
  background: #0f1a31;
}
.btn-option:hover{
  background: var(--primary-900);
  border-color: var(--primary);
}
.btn-option[disabled]{ opacity:.65; pointer-events:none; }

/* Estados correcto/incorrecto más legibles */
.btn-success{
  background: var(--success);
  border-color: var(--success);
  color:#061315;
}
.btn-danger{
  background: var(--danger);
  border-color: var(--danger);
  color:#1c070a;
}

/* Títulos ligeramente más luminosos */
h1,h2,h3,h4{ color:#f3f7ff; }

/* Área de feedback visible pero discreta */
.feedback{
  min-height:1.5rem;
  color:#d9e8ff;
}

/* Accesibilidad: foco claro al navegar con teclado */
:focus-visible{
  outline:3px solid var(--primary);
  outline-offset:2px;
  border-radius:10px;
}
</style>
</head>

<body class="pb-5"
      data-ctx="<%= request.getContextPath() %>"
      data-hasq="<%= serverHasQuestion %>">

<!-- NAV -->
<nav class="navbar navbar-expand-lg sticky-top">
  <div class="container">
    <a class="navbar-brand text-light" href="<%= request.getContextPath() %>/">torbesa</a>
    <div class="ms-auto d-flex gap-2">
      <a class="btn btn-sm btn-outline-light"
   href="<%= request.getContextPath() %>/shared/welcome.jsp">Inicio</a>
      <a class="btn btn-sm btn-outline-info" id="btnReiniciarApi" href="#">Nueva (API)</a>
      <a class="btn btn-sm btn-outline-light" href="<%= request.getContextPath() %>/trivia?reset=1">Reiniciar Local</a>
    </div>
  </div>
</nav>

<div class="container" style="max-width:900px;">
  <div class="d-flex justify-content-between align-items-center my-3">
    <div class="hud d-flex gap-2 flex-wrap align-items-center">
      <span class="item">Puntuaci&oacute;n: <strong id="hudScore"><%= score %></strong></span>
      <span class="item">Racha: <strong id="hudStreak"><%= streak %></strong></span>
      <span class="item">
        Modo:
        <span class="badge badge-mode rounded-pill ms-1"><%= serverHasQuestion ? "Local" : "API" %></span>
      </span>

      <!-- Selector de modo -->
      <div class="btn-group ms-2" role="group" aria-label="Modo">
        <a class="btn btn-sm <%= serverHasQuestion ? "btn-primary" : "btn-outline-primary" %>"
           href="<%= request.getContextPath() %>/trivia">Local</a>
        <a class="btn btn-sm <%= !serverHasQuestion ? "btn-primary" : "btn-outline-primary" %>"
           href="<%= request.getContextPath() %>/trivia?api=1">API externa</a>
      </div>
    </div>
    <div class="text-muted small">
      Consejo: pulsa <kbd>N</kbd> para nueva pregunta (API)
    </div>
  </div>

  <div class="card p-4 mb-3">
    <h3 class="mb-3" id="tituloPregunta"><%= serverHasQuestion ? question : "Cargando pregunta..." %></h3>
    <div class="feedback mb-2" id="feedback"></div>

    <!-- LOCAL -->
    <form id="formLocal" method="post" action="<%= request.getContextPath() %>/trivia" <%= serverHasQuestion ? "" : "style='display:none'" %>>
      <div class="row g-2">
        <% if (serverHasQuestion) {
             for (int i=0;i<options.size();i++) { String opt = String.valueOf(options.get(i)); %>
          <div class="col-12 col-md-6">
            <button type="submit" class="btn btn-outline-info btn-option" name="answer" value="<%= opt %>"><%= opt %></button>
          </div>
        <% }} %>
      </div>
    </form>

    <!-- API -->
    <div id="apiZone" <%= serverHasQuestion ? "style='display:none'" : "" %>>
      <div id="apiOptions" class="row g-2 mb-3" data-click-delegate="true"></div>

      <div class="d-flex gap-2 align-items-end flex-wrap">
        <div>
          <label class="form-label mb-1">Categor&iacute;a</label>
          <select class="form-select form-select-sm" id="apiCat">
            <option value="">Cualquiera</option>
            <option value="9">General Knowledge</option>
            <option value="11">Entertainment: Film</option>
            <option value="12">Entertainment: Music</option>
            <option value="17">Science &amp; Nature</option>
            <option value="18">Science: Computers</option>
            <option value="22">Geography</option>
            <option value="23">History</option>
            <option value="27">Animals</option>
          </select>
        </div>
        <div>
          <label class="form-label mb-1">Dificultad</label>
          <select class="form-select form-select-sm" id="apiDiff">
            <option value="">Cualquiera</option>
            <option value="easy">F&aacute;cil</option>
            <option value="medium">Media</option>
            <option value="hard">Dif&iacute;cil</option>
          </select>
        </div>
        <button class="btn btn-primary btn-sm" id="btnNueva">Nueva pregunta</button>
      </div>
    </div>
  </div>

  <div class="text-center text-secondary small">
    Hecho con &#x2764;&#xFE0F; para 2&ordm; DAW &middot; Modo Local + API (OpenTDB)
  </div>
</div>

<script>
(function () {
  const ctx   = document.body.getAttribute('data-ctx') || '';
  const hasQ  = (document.body.getAttribute('data-hasq') === 'true');

  const hudScore  = document.getElementById('hudScore');
  const hudStreak = document.getElementById('hudStreak');
  const titulo    = document.getElementById('tituloPregunta');

  if (hasQ) return; // modo LOCAL: ya est\u00E1 pintado por el servidor

  const apiOptions = document.getElementById('apiOptions');
  const apiCat     = document.getElementById('apiCat');
  const apiDiff    = document.getElementById('apiDiff');
  const btnNueva   = document.getElementById('btnNueva');

  let lastButtons = [];

  function disableAll(disabled=true){
    lastButtons.forEach(b => b.disabled = disabled);
  }

  function renderOptions(opts) {
    apiOptions.innerHTML = '';
    lastButtons = [];
    (opts || []).forEach((text, idx) => {
      const col = document.createElement('div');
      col.className = 'col-12 col-md-6';

      const btn = document.createElement('button');
      btn.type = 'button';
      btn.className = 'btn btn-outline-info btn-option';
      btn.textContent = text;

      btn.addEventListener('click', async (ev) => {
        ev.preventDefault();
        disableAll(true);

        // ENVIO URL-ENCODED (no FormData)
        const body = 'choice=' + encodeURIComponent(idx);
        const r = await fetch(ctx + '/api/answer', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
          body
        });

        let j = null;
        try { j = await r.json(); } catch(_) {}

        const ok = !!(j && j.ok);
        if (j && typeof j.score === 'number')  hudScore.textContent  = j.score;
        if (j && typeof j.streak === 'number') hudStreak.textContent = j.streak;

        // feedback visual
        btn.classList.remove('btn-outline-info');
        btn.classList.add(ok ? 'btn-success' : 'btn-danger');

        // pasar a la siguiente SIEMPRE (aciertes o no)
        setTimeout(() => loadQuestion(), 600);
      });

      col.appendChild(btn);
      apiOptions.appendChild(col);
      lastButtons.push(btn);
    });
  }

  async function loadQuestion() {
    titulo.textContent = 'Cargando...';
    apiOptions.innerHTML = '';
    lastButtons = [];

    const params = new URLSearchParams();
    const cat  = (apiCat?.value || '').trim();
    const diff = (apiDiff?.value || '').trim();
    if (cat)  params.set('cat', cat);
    if (diff) params.set('difficulty', diff);

    const url = ctx + '/api/question' + (params.toString() ? ('?' + params.toString()) : '');
    try {
      const r = await fetch(url, { cache: 'no-store' });
      const j = await r.json();
      if (j && !j.error && j.question && j.options) {
        titulo.textContent = j.question;
        renderOptions(j.options);
        disableAll(false);
      } else {
        titulo.textContent = 'Sin resultados. Cambia categor\u00EDa/dificultad y prueba de nuevo.';
      }
    } catch (e) {
      titulo.textContent = 'Error al cargar la pregunta.';
    }
  }

  if (btnNueva) btnNueva.addEventListener('click', (e) => { e.preventDefault(); loadQuestion(); });
  // primera carga
  loadQuestion();
})();
</script>
</body>
</html>