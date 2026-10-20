-- =====================================================================
-- PROJETO MOTIVA / GreenAV - Sprint 3
-- Script de CRIACAO das tabelas (Oracle)
-- Executar ANTES de script-dados.sql
-- =====================================================================
-- Modelo derivado das classes das Sprints 1 e 2:
--   EquipeManutencao                          -> MTV_EQUIPE_MANUTENCAO
--   TrechoRodovia (+ TrechoUmido / TrechoSeco) -> MTV_TRECHO_RODOVIA
--   IntervencaoOperacional (+ RocadaMecanizada / Pulverizacao)
--                                             -> MTV_INTERVENCAO_OPERACIONAL
--   GeradorRelatorio (novo historico)         -> MTV_RELATORIO_PRIORIDADE
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) LIMPEZA (torna o script re-executavel; ignora "tabela nao existe")
-- ---------------------------------------------------------------------
BEGIN EXECUTE IMMEDIATE 'DROP TABLE MTV_INTERVENCAO_OPERACIONAL CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE MTV_RELATORIO_PRIORIDADE CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE MTV_TRECHO_RODOVIA CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE MTV_EQUIPE_MANUTENCAO CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_MTV_EQUIPE';      EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_MTV_TRECHO';      EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_MTV_INTERVENCAO'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_MTV_RELATORIO';   EXCEPTION WHEN OTHERS THEN NULL; END;
/

-- ---------------------------------------------------------------------
-- 2) TABELA: MTV_EQUIPE_MANUTENCAO
--    Origem: classe com.greenav.model.EquipeManutencao
-- ---------------------------------------------------------------------
CREATE TABLE MTV_EQUIPE_MANUTENCAO (
    ID_EQUIPE        NUMBER(6)       NOT NULL,
    NM_EQUIPE        VARCHAR2(60)    NOT NULL,
    DS_TURNO         VARCHAR2(10)    NOT NULL,
    QT_INTEGRANTES   NUMBER(3)       DEFAULT 1 NOT NULL,
    ST_ATIVA         CHAR(1)         DEFAULT 'S' NOT NULL,
    CONSTRAINT PK_MTV_EQUIPE        PRIMARY KEY (ID_EQUIPE),
    CONSTRAINT UK_MTV_EQUIPE_NOME   UNIQUE (NM_EQUIPE),
    CONSTRAINT CK_MTV_EQUIPE_TURNO  CHECK (DS_TURNO IN ('MANHA','TARDE','NOITE')),
    CONSTRAINT CK_MTV_EQUIPE_QTD    CHECK (QT_INTEGRANTES > 0),
    CONSTRAINT CK_MTV_EQUIPE_ATIVA  CHECK (ST_ATIVA IN ('S','N'))
);

COMMENT ON TABLE  MTV_EQUIPE_MANUTENCAO           IS 'Equipes de manutencao da faixa de dominio';
COMMENT ON COLUMN MTV_EQUIPE_MANUTENCAO.NM_EQUIPE IS 'Nome/identificador da equipe (ex: Alfa-01)';
COMMENT ON COLUMN MTV_EQUIPE_MANUTENCAO.ST_ATIVA  IS 'S = equipe disponivel / N = equipe inativa';

-- ---------------------------------------------------------------------
-- 3) TABELA: MTV_TRECHO_RODOVIA
--    Origem: TrechoRodovia + subclasses TrechoUmido / TrechoSeco.
--    A heranca foi mapeada com a estrategia "tabela unica + coluna
--    discriminadora" (TP_TRECHO).
-- ---------------------------------------------------------------------
CREATE TABLE MTV_TRECHO_RODOVIA (
    ID_TRECHO            NUMBER(6)       NOT NULL,
    NM_RODOVIA           VARCHAR2(20)    NOT NULL,
    NR_KM_INICIAL        NUMBER(5)       NOT NULL,
    NR_KM_FINAL          NUMBER(5)       NOT NULL,
    VL_NIVEL_VEGETACAO   NUMBER(6,2)     DEFAULT 0 NOT NULL,
    TP_TRECHO            VARCHAR2(10)    NOT NULL,
    DT_ULTIMA_LEITURA    DATE            DEFAULT SYSDATE,
    CONSTRAINT PK_MTV_TRECHO        PRIMARY KEY (ID_TRECHO),
    CONSTRAINT UK_MTV_TRECHO_KM     UNIQUE (NM_RODOVIA, NR_KM_INICIAL, NR_KM_FINAL),
    CONSTRAINT CK_MTV_TRECHO_TIPO   CHECK (TP_TRECHO IN ('UMIDO','SECO')),
    CONSTRAINT CK_MTV_TRECHO_KM     CHECK (NR_KM_FINAL > NR_KM_INICIAL),
    CONSTRAINT CK_MTV_TRECHO_VEG    CHECK (VL_NIVEL_VEGETACAO >= 0)
);

COMMENT ON TABLE  MTV_TRECHO_RODOVIA                    IS 'Trechos monitorados da malha rodoviaria';
COMMENT ON COLUMN MTV_TRECHO_RODOVIA.TP_TRECHO          IS 'UMIDO = TrechoUmido (MonitoravelViaIoT) / SECO = TrechoSeco';
COMMENT ON COLUMN MTV_TRECHO_RODOVIA.VL_NIVEL_VEGETACAO IS 'Altura da vegetacao em centimetros';

-- ---------------------------------------------------------------------
-- 4) TABELA: MTV_INTERVENCAO_OPERACIONAL
--    Origem: IntervencaoOperacional (abstrata) + RocadaMecanizada /
--    Pulverizacao. Guarda o HISTORICO de servicos executados.
-- ---------------------------------------------------------------------
CREATE TABLE MTV_INTERVENCAO_OPERACIONAL (
    ID_INTERVENCAO     NUMBER(8)       NOT NULL,
    ID_TRECHO          NUMBER(6)       NOT NULL,
    ID_EQUIPE          NUMBER(6),
    TP_INTERVENCAO     VARCHAR2(20)    NOT NULL,
    DT_EXECUCAO        DATE            DEFAULT SYSDATE NOT NULL,
    VL_NIVEL_ANTES     NUMBER(6,2)     NOT NULL,
    VL_NIVEL_DEPOIS    NUMBER(6,2)     NOT NULL,
    VL_CUSTO           NUMBER(10,2)    DEFAULT 0 NOT NULL,
    CONSTRAINT PK_MTV_INTERVENCAO PRIMARY KEY (ID_INTERVENCAO),
    CONSTRAINT FK_INTERV_TRECHO   FOREIGN KEY (ID_TRECHO)
        REFERENCES MTV_TRECHO_RODOVIA (ID_TRECHO),
    CONSTRAINT FK_INTERV_EQUIPE   FOREIGN KEY (ID_EQUIPE)
        REFERENCES MTV_EQUIPE_MANUTENCAO (ID_EQUIPE),
    CONSTRAINT CK_INTERV_TIPO     CHECK (TP_INTERVENCAO IN ('ROCADA_MECANIZADA','PULVERIZACAO')),
    CONSTRAINT CK_INTERV_NIVEIS   CHECK (VL_NIVEL_ANTES >= 0 AND VL_NIVEL_DEPOIS >= 0),
    CONSTRAINT CK_INTERV_CUSTO    CHECK (VL_CUSTO >= 0)
);

COMMENT ON TABLE  MTV_INTERVENCAO_OPERACIONAL                IS 'Historico de intervencoes executadas nos trechos';
COMMENT ON COLUMN MTV_INTERVENCAO_OPERACIONAL.TP_INTERVENCAO IS 'Subclasse concreta executada (RocadaMecanizada / Pulverizacao)';

CREATE INDEX IX_INTERV_TRECHO ON MTV_INTERVENCAO_OPERACIONAL (ID_TRECHO);
CREATE INDEX IX_INTERV_EQUIPE ON MTV_INTERVENCAO_OPERACIONAL (ID_EQUIPE);

-- ---------------------------------------------------------------------
-- 5) TABELA: MTV_RELATORIO_PRIORIDADE
--    Origem: nova funcionalidade da Sprint 3 - o GeradorRelatorio passa
--    a gravar o historico de cada relatorio emitido.
-- ---------------------------------------------------------------------
CREATE TABLE MTV_RELATORIO_PRIORIDADE (
    ID_RELATORIO     NUMBER(8)      NOT NULL,
    DT_GERACAO       TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    QT_URGENTE       NUMBER(5)      DEFAULT 0 NOT NULL,
    QT_CRITICO       NUMBER(5)      DEFAULT 0 NOT NULL,
    QT_ATENCAO       NUMBER(5)      DEFAULT 0 NOT NULL,
    QT_NORMAL        NUMBER(5)      DEFAULT 0 NOT NULL,
    DS_RESUMO        VARCHAR2(500),
    CONSTRAINT PK_MTV_RELATORIO  PRIMARY KEY (ID_RELATORIO),
    CONSTRAINT CK_MTV_RELAT_QTDS CHECK (QT_URGENTE >= 0 AND QT_CRITICO >= 0
                                    AND QT_ATENCAO >= 0 AND QT_NORMAL  >= 0)
);

COMMENT ON TABLE  MTV_RELATORIO_PRIORIDADE           IS 'Historico dos relatorios gerados pelo GeradorRelatorio';
COMMENT ON COLUMN MTV_RELATORIO_PRIORIDADE.DS_RESUMO IS 'Texto-resumo impresso no console no momento da geracao';

-- ---------------------------------------------------------------------
-- 6) SEQUENCES + TRIGGERS (auto-incremento)
--    Nao usamos GENERATED AS IDENTITY para o script rodar tambem em
--    instancias Oracle 11g do laboratorio.
-- ---------------------------------------------------------------------
CREATE SEQUENCE SEQ_MTV_EQUIPE      START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_MTV_TRECHO      START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_MTV_INTERVENCAO START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE SEQ_MTV_RELATORIO   START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE OR REPLACE TRIGGER TRG_MTV_EQUIPE_ID
BEFORE INSERT ON MTV_EQUIPE_MANUTENCAO
FOR EACH ROW
WHEN (NEW.ID_EQUIPE IS NULL)
BEGIN
    :NEW.ID_EQUIPE := SEQ_MTV_EQUIPE.NEXTVAL;
END;
/

CREATE OR REPLACE TRIGGER TRG_MTV_TRECHO_ID
BEFORE INSERT ON MTV_TRECHO_RODOVIA
FOR EACH ROW
WHEN (NEW.ID_TRECHO IS NULL)
BEGIN
    :NEW.ID_TRECHO := SEQ_MTV_TRECHO.NEXTVAL;
END;
/

CREATE OR REPLACE TRIGGER TRG_MTV_INTERVENCAO_ID
BEFORE INSERT ON MTV_INTERVENCAO_OPERACIONAL
FOR EACH ROW
WHEN (NEW.ID_INTERVENCAO IS NULL)
BEGIN
    :NEW.ID_INTERVENCAO := SEQ_MTV_INTERVENCAO.NEXTVAL;
END;
/

CREATE OR REPLACE TRIGGER TRG_MTV_RELATORIO_ID
BEFORE INSERT ON MTV_RELATORIO_PRIORIDADE
FOR EACH ROW
WHEN (NEW.ID_RELATORIO IS NULL)
BEGIN
    :NEW.ID_RELATORIO := SEQ_MTV_RELATORIO.NEXTVAL;
END;
/

COMMIT;

-- ---------------------------------------------------------------------
-- 7) CONFERENCIA
-- ---------------------------------------------------------------------
SELECT TABLE_NAME
  FROM USER_TABLES
 WHERE TABLE_NAME LIKE 'MTV\_%' ESCAPE '\'
 ORDER BY TABLE_NAME;
