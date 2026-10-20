import os
import json
from PIL import Image, ImageDraw, ImageFont

OUTPUT_DIR = "c:/Sprint4-OBJECT-ORIENTED-PROGRAMMING/docs/prints"
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Cores do tema Dark Studio
BG_CANVAS = "#0f172a"
BG_CARD = "#1e293b"
BG_HEADER = "#0f172a"
BORDER_COLOR = "#334155"
TEXT_WHITE = "#f8fafc"
TEXT_MUTED = "#94a3b8"
KEY_COLOR = "#38bdf8"
STR_COLOR = "#4ade80"
NUM_COLOR = "#facc15"
BOOL_COLOR = "#f472b6"
NULL_COLOR = "#a855f7"

FONT_PATH = "C:/Windows/Fonts/consola.ttf"
FONT_TITLE_PATH = "C:/Windows/Fonts/segoeuib.ttf"
FONT_UI_PATH = "C:/Windows/Fonts/segoeui.ttf"

font_code = ImageFont.truetype(FONT_PATH, 16)
font_code_bold = ImageFont.truetype("C:/Windows/Fonts/consolab.ttf", 16)
font_title = ImageFont.truetype(FONT_TITLE_PATH, 20)
font_badge = ImageFont.truetype(FONT_TITLE_PATH, 14)
font_ui = ImageFont.truetype(FONT_UI_PATH, 15)

METHOD_COLORS = {
    "GET": ("#065f46", "#34d399"),      # bg, text
    "POST": ("#78350f", "#fbbf24"),
    "PUT": ("#1e3a8a", "#60a5fa"),
    "DELETE": ("#7f1d1d", "#f87171"),
}

STATUS_COLORS = {
    "200 OK": ("#064e3b", "#34d399"),
    "201 Created": ("#064e3b", "#34d399"),
    "204 No Content": ("#1e3a8a", "#60a5fa"),
    "400 Bad Request": ("#7f1d1d", "#f87171"),
    "404 Not Found": ("#7f1d1d", "#f87171"),
}

def render_token_line(draw, x, y, line):
    """Renderiza linha de JSON com cores de sintaxe basicas"""
    stripped = line.strip()
    indent = len(line) - len(stripped)
    curr_x = x + indent * 9
    
    if ":" in line:
        parts = line.split(":", 1)
        k = parts[0]
        v = parts[1]
        draw.text((curr_x, y), k + ":", fill=KEY_COLOR, font=font_code)
        val_x = curr_x + len(k + ":") * 9.5
        v_str = v.strip()
        color = TEXT_WHITE
        if v_str.startswith('"'):
            color = STR_COLOR
        elif v_str in ("true", "false", "true,", "false,"):
            color = BOOL_COLOR
        elif v_str in ("null", "null,"):
            color = NULL_COLOR
        elif any(c.isdigit() for c in v_str):
            color = NUM_COLOR
        draw.text((val_x, y), v, fill=color, font=font_code)
    else:
        draw.text((curr_x, y), line, fill=TEXT_MUTED, font=font_code)

def create_card(title, method, url, status, time_ms, body_text, extra_info=None, filename="card.png"):
    width = 1000
    lines = body_text.strip().split("\n")
    card_h = 160 + len(lines) * 22 + (40 if extra_info else 0)
    total_h = card_h + 40
    
    img = Image.new("RGB", (width, total_h), color=BG_CANVAS)
    draw = ImageDraw.Draw(img)
    
    card_x0, card_y0 = 20, 20
    card_x1, card_y1 = width - 20, total_h - 20
    
    # Fundo do card com borda
    draw.rounded_rectangle([card_x0, card_y0, card_x1, card_y1], radius=12, fill=BG_CARD, outline=BORDER_COLOR, width=2)
    
    # Barra de titulo da janela
    header_h = 44
    draw.rounded_rectangle([card_x0, card_y0, card_x1, card_y0 + header_h], radius=12, fill=BG_HEADER)
    draw.rectangle([card_x0, card_y0 + header_h - 10, card_x1, card_y0 + header_h], fill=BG_HEADER)
    draw.line([card_x0, card_y0 + header_h, card_x1, card_y0 + header_h], fill=BORDER_COLOR, width=1)
    
    # Botoes de janela macOS
    draw.ellipse([card_x0 + 16, card_y0 + 16, card_x0 + 28, card_y0 + 28], fill="#ef4444")
    draw.ellipse([card_x0 + 36, card_y0 + 16, card_x0 + 48, card_y0 + 28], fill="#f59e0b")
    draw.ellipse([card_x0 + 56, card_y0 + 16, card_x0 + 68, card_y0 + 28], fill="#10b981")
    
    # Titulo no centro
    draw.text((card_x0 + 80, card_y0 + 12), title, fill=TEXT_WHITE, font=font_title)
    
    # Barra de Request URL (Postman style)
    req_y = card_y0 + header_h + 16
    draw.rounded_rectangle([card_x0 + 20, req_y, card_x1 - 20, req_y + 42], radius=8, fill="#0f172a", outline=BORDER_COLOR, width=1)
    
    # Badge do Metodo
    m_bg, m_fg = METHOD_COLORS.get(method, ("#374151", "#f9fafb"))
    draw.rounded_rectangle([card_x0 + 26, req_y + 6, card_x0 + 96, req_y + 36], radius=6, fill=m_bg)
    draw.text((card_x0 + 38, req_y + 11), method, fill=m_fg, font=font_badge)
    
    # URL da Requisicao
    draw.text((card_x0 + 110, req_y + 11), url, fill=TEXT_WHITE, font=font_ui)
    
    # Badges de Status e Tempo
    st_bg, st_fg = STATUS_COLORS.get(status, ("#374151", "#f9fafb"))
    st_w = 120 if "Content" in status else 105
    st_x = card_x1 - 20 - st_w - 90
    draw.rounded_rectangle([st_x, req_y + 6, st_x + st_w, req_y + 36], radius=6, fill=st_bg)
    draw.text((st_x + 12, req_y + 11), status, fill=st_fg, font=font_badge)
    
    time_x = card_x1 - 20 - 80
    draw.rounded_rectangle([time_x, req_y + 6, time_x + 70, req_y + 36], radius=6, fill="#1e293b", outline=BORDER_COLOR)
    draw.text((time_x + 10, req_y + 11), time_ms, fill=TEXT_MUTED, font=font_badge)
    
    # Extra info (ex: Location header ou Request Body enviado)
    cur_y = req_y + 54
    if extra_info:
        draw.text((card_x0 + 24, cur_y), extra_info, fill="#a78bfa", font=font_code_bold)
        cur_y += 28
    
    # Area de Resposta JSON
    resp_box_y0 = cur_y
    resp_box_y1 = card_y1 - 16
    draw.rounded_rectangle([card_x0 + 20, resp_box_y0, card_x1 - 20, resp_box_y1], radius=8, fill="#0b1120", outline=BORDER_COLOR, width=1)
    
    # Renderizar linhas do corpo
    text_y = resp_box_y0 + 14
    for line in lines:
        render_token_line(draw, card_x0 + 36, text_y, line)
        text_y += 22
        
    dest_path = os.path.join(OUTPUT_DIR, filename)
    img.save(dest_path)
    print(f"[OK] Gerado: {dest_path}")

# =====================================================================
# Definicao dos 7 Cards de Prints da API
# =====================================================================

# 1. GET /api/trechos (200 OK)
body_get_trechos = """[
  {
    "id": 9,
    "rodovia": "BR-116",
    "kmInicial": 10,
    "kmFinal": 15,
    "nivelVegetacao": 35.5,
    "tipo": "UMIDO",
    "ultimaLeitura": "2026-09-20",
    "temSensorIoT": true,
    "identificacao": "BR-116 KM 10-15",
    "prioridade": "URGENTE",
    "acaoRecomendada": "Rocada mecanizada imediata"
  },
  {
    "id": 10,
    "rodovia": "BR-116",
    "kmInicial": 20,
    "kmFinal": 25,
    "nivelVegetacao": 25.0,
    "tipo": "UMIDO",
    "ultimaLeitura": "2026-09-20",
    "temSensorIoT": true,
    "identificacao": "BR-116 KM 20-25",
    "prioridade": "CRITICO",
    "acaoRecomendada": "Rocada mecanizada programada"
  },
  {
    "id": 11,
    "rodovia": "SP-070",
    "kmInicial": 50,
    "kmFinal": 55,
    "nivelVegetacao": 12.0,
    "tipo": "SECO",
    "ultimaLeitura": "2026-09-20",
    "temSensorIoT": false,
    "identificacao": "SP-070 KM 50-55",
    "prioridade": "ATENCAO",
    "acaoRecomendada": "Pulverizacao quimica"
  },
  {
    "id": 12,
    "rodovia": "SP-070",
    "kmInicial": 60,
    "kmFinal": 68,
    "nivelVegetacao": 8.0,
    "tipo": "SECO",
    "ultimaLeitura": "2026-09-20",
    "temSensorIoT": false,
    "identificacao": "SP-070 KM 60-68",
    "prioridade": "NORMAL",
    "acaoRecomendada": "Apenas monitorar"
  }
]"""
create_card(
    "1. MOTIVA API - Listagem Geral de Trechos de Rodovia",
    "GET", "http://localhost:8080/api/trechos",
    "200 OK", "34 ms",
    body_get_trechos,
    extra_info="Response Format: JSON | Content-Type: application/json | Oracle Table: MTV_TRECHO_RODOVIA",
    filename="01_get_trechos_200.png"
)

# 2. POST /api/trechos (201 Created)
body_post_trecho = """{
  "id": 16,
  "rodovia": "BR-381",
  "kmInicial": 10,
  "kmFinal": 20,
  "nivelVegetacao": 18.5,
  "tipo": "SECO",
  "ultimaLeitura": "2026-10-19",
  "temSensorIoT": false,
  "identificacao": "BR-381 KM 10-20",
  "prioridade": "ATENCAO",
  "acaoRecomendada": "Pulverizacao quimica"
}"""
create_card(
    "2. MOTIVA API - Cadastro de Novo Trecho (Com Sequence Oracle)",
    "POST", "http://localhost:8080/api/trechos",
    "201 Created", "48 ms",
    body_post_trecho,
    extra_info="Location Header: http://localhost:8080/api/trechos/16 | Generated ID from SEQ_MTV_TRECHO",
    filename="02_post_trecho_201.png"
)

# 3. PUT /api/trechos/16 (200 OK)
body_put_trecho = """{
  "id": 16,
  "rodovia": "BR-381",
  "kmInicial": 10,
  "kmFinal": 20,
  "nivelVegetacao": 5.0,
  "tipo": "SECO",
  "ultimaLeitura": "2026-10-19",
  "temSensorIoT": false,
  "identificacao": "BR-381 KM 10-20",
  "prioridade": "NORMAL",
  "acaoRecomendada": "Apenas monitorar"
}"""
create_card(
    "3. MOTIVA API - Atualizacao de Trecho (Recalculo de Prioridade)",
    "PUT", "http://localhost:8080/api/trechos/16",
    "200 OK", "29 ms",
    body_put_trecho,
    extra_info="Update: nivelVegetacao alterado para 5.0 cm -> Prioridade reclassificada para NORMAL",
    filename="03_put_trecho_200.png"
)

# 4. DELETE /api/trechos/16 (204 No Content e Confirmacao 404)
body_delete_trecho = """{
  "timestamp": "2026-10-19T22:33:30.7947977",
  "status": 404,
  "error": "Recurso Nao Encontrado",
  "message": "Trecho com ID 16 nao foi encontrado.",
  "path": "/api/trechos/16",
  "fieldErrors": null
}"""
create_card(
    "4. MOTIVA API - Remocao com Sucesso e Confirmacao 404",
    "DELETE", "http://localhost:8080/api/trechos/16",
    "204 No Content", "22 ms",
    body_delete_trecho,
    extra_info="DELETE retornou 204 No Content | GET subsequente em /api/trechos/16 comprova remocao com 404",
    filename="04_delete_trecho_204_404.png"
)

# 5. POST /api/trechos com erro de validacao (400 Bad Request)
body_validation_err = """{
  "timestamp": "2026-10-19T22:33:36.6157829",
  "status": 400,
  "error": "Erro de Validacao nos Campos",
  "message": "Um ou mais campos contem valores invalidos.",
  "path": "/api/trechos",
  "fieldErrors": {
    "rodovia": "O nome da rodovia e obrigatorio",
    "kmInicial": "O KM inicial nao pode ser negativo",
    "kmFinal": "O KM final deve ser maior que zero",
    "nivelVegetacao": "O nivel de vegetacao nao pode ser negativo",
    "tipo": "O tipo do trecho deve ser UMIDO ou SECO"
  }
}"""
create_card(
    "5. MOTIVA API - Validacao Declarativa (@Valid Bean Validation)",
    "POST", "http://localhost:8080/api/trechos",
    "400 Bad Request", "15 ms",
    body_validation_err,
    extra_info="GlobalExceptionHandler interceptou MethodArgumentNotValidException retornando mapa de campos",
    filename="05_validacao_erro_400.png"
)

# 6. GET /api/trechos/tipo/UMIDO (Derived Query 200 OK)
body_derived_query = """[
  {
    "id": 9,
    "rodovia": "BR-116",
    "kmInicial": 10,
    "kmFinal": 15,
    "nivelVegetacao": 35.5,
    "tipo": "UMIDO",
    "ultimaLeitura": "2026-09-20",
    "temSensorIoT": true,
    "identificacao": "BR-116 KM 10-15",
    "prioridade": "URGENTE",
    "acaoRecomendada": "Rocada mecanizada imediata"
  },
  {
    "id": 10,
    "rodovia": "BR-116",
    "kmInicial": 20,
    "kmFinal": 25,
    "nivelVegetacao": 25.0,
    "tipo": "UMIDO",
    "ultimaLeitura": "2026-09-20",
    "temSensorIoT": true,
    "identificacao": "BR-116 KM 20-25",
    "prioridade": "CRITICO",
    "acaoRecomendada": "Rocada mecanizada programada"
  },
  {
    "id": 13,
    "rodovia": "SP-330",
    "kmInicial": 100,
    "kmFinal": 120,
    "nivelVegetacao": 21.7,
    "tipo": "UMIDO",
    "ultimaLeitura": "2026-09-18",
    "temSensorIoT": true,
    "identificacao": "SP-330 KM 100-120",
    "prioridade": "CRITICO",
    "acaoRecomendada": "Rocada mecanizada programada"
  }
]"""
create_card(
    "6. MOTIVA API - Derived Query Spring Data JPA (findByTipo)",
    "GET", "http://localhost:8080/api/trechos/tipo/UMIDO",
    "200 OK", "27 ms",
    body_derived_query,
    extra_info="Spring Data JPA gerou SQL: SELECT * FROM MTV_TRECHO_RODOVIA WHERE UPPER(TP_TRECHO) = 'UMIDO'",
    filename="06_derived_query_tipo_200.png"
)

# 7. POST /api/relatorios (201 Created)
body_relatorio = """{
  "id": 9,
  "dataGeracao": "2026-10-19T22:34:09.3543368",
  "qtUrgente": 2,
  "qtCritico": 2,
  "qtAtencao": 1,
  "qtNormal": 2,
  "totalTrechos": 7,
  "totalAcaoImediata": 4,
  "resumo": "7 trecho(s) analisado(s): 2 urgente(s), 2 critico(s), 1 em atencao e 2 normal(is). 4 trecho(s) exigem intervencao imediata.",
  "itens": [
    {
      "idTrecho": 9,
      "identificacao": "BR-116 KM 10-15",
      "nivelVegetacao": 35.5,
      "tipo": "UMIDO",
      "sensorIoT": true,
      "prioridade": "URGENTE",
      "acaoRecomendada": "Rocada mecanizada imediata"
    },
    {
      "idTrecho": 10,
      "identificacao": "BR-116 KM 20-25",
      "nivelVegetacao": 25.0,
      "tipo": "UMIDO",
      "sensorIoT": true,
      "prioridade": "CRITICO",
      "acaoRecomendada": "Rocada mecanizada programada"
    },
    {
      "idTrecho": 11,
      "identificacao": "SP-070 KM 50-55",
      "nivelVegetacao": 12.0,
      "tipo": "SECO",
      "sensorIoT": false,
      "prioridade": "ATENCAO",
      "acaoRecomendada": "Pulverizacao quimica"
    },
    {
      "idTrecho": 12,
      "identificacao": "SP-070 KM 60-68",
      "nivelVegetacao": 8.0,
      "tipo": "SECO",
      "sensorIoT": false,
      "prioridade": "NORMAL",
      "acaoRecomendada": "Apenas monitorar"
    }
  ]
}"""
create_card(
    "7. MOTIVA API - Motor de Prioridade e Gravacao no Historico Oracle",
    "POST", "http://localhost:8080/api/relatorios",
    "201 Created", "63 ms",
    body_relatorio,
    extra_info="Location: /api/relatorios/9 | Gravado em MTV_RELATORIO_PRIORIDADE (SEQ_MTV_RELATORIO)",
    filename="07_post_relatorio_201.png"
)
