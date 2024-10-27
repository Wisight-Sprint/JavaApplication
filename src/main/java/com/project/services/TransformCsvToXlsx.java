package com.project.services;

import com.project.config.Config;
import com.project.model.*;
import com.project.provider.DBConnectionProvider;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransformCsvToXlsx {
    // Instâncias dos processadores de cada coluna
    private final CidadeEstado colunaCidadeEstado = new CidadeEstado();
    private final Departamento colunaDepartamento = new Departamento();
    private final Vitima colunaVitima = new Vitima();
    private final Relatorio colunaRelatorio = new Relatorio();

    DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
    JdbcTemplate connection = dbConnectionProvider.getDatabaseConnection();

    private void inserirLinhaNoBanco(CidadeEstado cidadeEstado, Departamento departamento, Relatorio relatorio, Vitima vitima) {
        //SELECT E INSERT PARA CIDADE_ESTADO
        List<CidadeEstado> cidades = connection.query("SELECT cidade_estado_id FROM cidade_estado WHERE cidade = ? AND estado = ?",
                new BeanPropertyRowMapper<>(CidadeEstado.class), cidadeEstado.getCidade(), cidadeEstado.getEstado());
        if (cidades.isEmpty()) {
            connection.update("INSERT INTO cidade_estado (cidade, estado) VALUES (?, ?)", cidadeEstado.getCidade(), cidadeEstado.getEstado());
            System.out.println("Linha inserida na tabela CidadeEstado com sucesso no banco.");
            cidades = connection.query("SELECT cidade_estado_id FROM cidade_estado WHERE cidade = ? AND estado = ?",
                    new BeanPropertyRowMapper<>(CidadeEstado.class), cidadeEstado.getCidade(), cidadeEstado.getEstado());
        }

        System.out.println("Id de %s, %s: %d".formatted(cidadeEstado.getCidade(), cidadeEstado.getEstado(), cidades.get(0).getCidade_estado_id()));
        //FIM

        //SELECT E INSERT PARA DEPARTAMENTO
        List<Departamento> departamentos = connection.query("SELECT departamento_id FROM departamento WHERE nome = ?",
                new BeanPropertyRowMapper<>(Departamento.class), departamento.getNome());
        if (departamentos.isEmpty()) {
            connection.update("INSERT INTO departamento (nome, fk_cidade_estado) VALUES (?, ?)", departamento.getNome(), cidades.get(0).getCidade_estado_id());
            System.out.println("--------------------------------------------------------------------------------------");
            System.out.println("Linha inserida na tabela Departamento com sucesso no banco. Nome: " + departamento.getNome());
            System.out.println("--------------------------------------------------------------------------------------");
            departamentos = connection.query("SELECT departamento_id FROM departamento WHERE nome = ?",
                    new BeanPropertyRowMapper<>(Departamento.class), departamento.getNome());
        }
        System.out.println("Id de %s: %d".formatted(departamento.getNome(), departamentos.get(0).getDepartamento_id()));
        //FIM

        //SELECT E INSERT PARA RELATORIO
        List<Relatorio> relatorios = connection.query("SELECT relatorio_id FROM relatorio WHERE dt_ocorrencia = ? AND fuga = ? AND camera_corporal = ? AND problemas_mentais = ? AND fk_departamento = ?",
                new BeanPropertyRowMapper<>(Relatorio.class), relatorio.getDataOcorrencia(), relatorio.getFuga(), relatorio.getCameraCorporal(), relatorio.getProblemasMentais(), departamentos.get(0).getDepartamento_id());
        if (relatorios.isEmpty()) {
            connection.update("INSERT INTO relatorio (dt_ocorrencia, fuga, camera_corporal, problemas_mentais, fk_departamento) VALUES (?, ?, ?, ?, ?)", relatorio.getDataOcorrencia(), relatorio.getFuga(), relatorio.getCameraCorporal(), relatorio.getProblemasMentais(), departamentos.get(0).getDepartamento_id());
            System.out.println("Connection update teste" + connection);
            relatorios = connection.query("SELECT relatorio_id FROM relatorio WHERE dt_ocorrencia = ? AND fuga = ? AND camera_corporal = ? AND problemas_mentais = ? AND fk_departamento = ?",
                    new BeanPropertyRowMapper<>(Relatorio.class), relatorio.getDataOcorrencia(), relatorio.getFuga(), relatorio.getCameraCorporal(), relatorio.getProblemasMentais(), departamentos.get(0).getDepartamento_id());
            System.out.println("Linha inserida na tabela Relatório com sucesso no banco. Id: " + relatorios.get(0));
        }
        System.out.printf("Id de relatório: %d%n", relatorios.get(0).getRelatorio_id());
        //FIM

        //SELECT E INSERT PARA VITIMA
        List<Vitima> vitimas = connection.query("SELECT vitima_id FROM vitima WHERE nome = ? AND idade = ? AND etnia = ? AND genero = ? AND armamento = ?",
                new BeanPropertyRowMapper<>(Vitima.class), vitima.getNome(), vitima.getIdade(), vitima.getEtnia(), vitima.getGenero(), vitima.getArmamento());
        if (vitimas.isEmpty()) {
            connection.update("INSERT INTO vitima (nome, idade, etnia, genero, armamento, fk_relatorio, fk_departamento) VALUES (?, ?, ?, ?, ?, ?, ?)", vitima.getNome(), vitima.getIdade(), vitima.getEtnia(), vitima.getGenero(), vitima.getArmamento(), relatorios.get(0).getRelatorio_id(), departamentos.get(0).getDepartamento_id());
            System.out.println("Linha inserida na tabela Vítima com sucesso no banco.");
            vitimas = connection.query("SELECT vitima_id FROM vitima WHERE nome = ? AND idade = ? AND etnia = ? AND genero = ? AND armamento = ?",
                    new BeanPropertyRowMapper<>(Vitima.class), vitima.getNome(), vitima.getIdade(), vitima.getEtnia(), vitima.getGenero(), vitima.getArmamento());
        }
        System.out.println("Id de vítima: %d".formatted(vitimas.get(0).getVitima_id()));
        //FIM

        cidades.clear();
        departamentos.clear();
        relatorios.clear();
        vitimas.clear();
    }

    public void convert() {
        String csvFile = ServiceS3.csvName;

        try (Workbook workbook = new XSSFWorkbook();
             BufferedReader br = new BufferedReader(new FileReader(csvFile))) {

            Sheet spreadsheets = workbook.createSheet("data");
            String line;
            int lineIndex = 0;

            if ((line = br.readLine()) != null) {
                Row header = spreadsheets.createRow(lineIndex++);
                String[] columnNames = line.split(",");
                for (int i = 0; i < columnNames.length; i++) {
                    header.createCell(i).setCellValue(columnNames[i].trim());
                }
            }
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

            while ((line = br.readLine()) != null) {
                Row currentLine = spreadsheets.createRow(lineIndex++);

                String[] column = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                String dataString = column[0].replaceAll("\"", "");
                if (dataString.contains("2024")) {

                    try {
                        Date dataFormatada = formato.parse(dataString);
                        if (column[0] == null || column[0].isBlank()) colunaRelatorio.setDataOcorrencia(new Date());
                        else colunaRelatorio.setDataOcorrencia(dataFormatada);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (column[1] == null || column[1].isBlank()) colunaVitima.setNome("");
                    else colunaVitima.setNome(column[1].replaceAll("\"", ""));
                    if (column[2] == null || column[2].isBlank()) colunaVitima.setIdade(0);
                    else colunaVitima.setIdade(Integer.valueOf(column[2].replaceAll("\"", "")));
                    if (column[3] == null || column[3].isBlank()) colunaVitima.setGenero("");
                    else colunaVitima.setGenero(column[3].trim().toLowerCase().replaceAll("\"", ""));
                    if (column[4] == null || column[4].isBlank()) colunaVitima.setArmamento("");
                    else colunaVitima.setArmamento(column[4].trim().toLowerCase().replaceAll("\"", ""));
                    if (column[5] == null || column[5].isBlank()) colunaVitima.setEtnia("");
                    else colunaVitima.setEtnia(column[5].trim().toLowerCase().replaceAll("\"", ""));
                    if (column[6] == null || column[6].isBlank()) colunaCidadeEstado.setCidade("");
                    else colunaCidadeEstado.setCidade(column[6].trim().toLowerCase().replaceAll("\"", ""));
                    if (column[7] == null || column[7].isBlank()) colunaCidadeEstado.setEstado("");
                    else colunaCidadeEstado.setEstado(column[7].trim().toLowerCase().replaceAll("\"", ""));
                    if (column[8] == null || column[8].isBlank() || column[8].equals("") || column[8].isEmpty()) colunaRelatorio.setFuga("nulo");
                    else colunaRelatorio.setFuga(column[8].trim().toLowerCase().replaceAll("\"", ""));
                    if (column[9] == null || column[9].isBlank()) colunaRelatorio.setCameraCorporal(null);
                    else colunaRelatorio.setCameraCorporal(Boolean.valueOf(column[9].replaceAll("\"", "")));
                    if (column[10] == null || column[10].isBlank()) colunaRelatorio.setProblemasMentais(null);
                    else colunaRelatorio.setProblemasMentais(Boolean.valueOf(column[10].replaceAll("\"", "")));

                    String [] coluna11 = new String[]{column[11]};
                    coluna11 = line.split(",");

                    if (coluna11[1] == null || coluna11[1].isBlank()) colunaDepartamento.setNome("");
                    else colunaDepartamento.setNome(coluna11[1].trim().toLowerCase().replaceAll("\"", ""));

                    inserirLinhaNoBanco(colunaCidadeEstado, colunaDepartamento, colunaRelatorio, colunaVitima);

                    for (int i = 0; i < column.length; i++) {
                        if (i == 2) i++;
                        currentLine.createCell(i).setCellValue(column[i].trim());
                    }
                }
            }

            System.out.println("-----------");
            System.out.println("Inserção finalizada");

        } catch (IOException e) {
            e.printStackTrace();
        }

        Config configPath = new Config();
        String ambiente = Config.getEnvironment();

        Path caminhoArquivo;

        if (ambiente.equals("prod"))
            caminhoArquivo = Paths.get("/home/ubuntu/base-de-dados-wisight.csv");
        else
            caminhoArquivo = Paths.get(Config.get("DELETE.FILE.URL"));

        try {
            Files.delete(caminhoArquivo);
            System.out.println("Arquivo deletado com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao deletar o arquivo: " + e.getMessage());
        }
    }
}

