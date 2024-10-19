package com.project.services;

import com.project.model.*;
import com.project.provider.DBConnectionProvider;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
        List<CidadeEstado> cidades = connection.query("SELECT cidade_estado_id FROM cidade_estado WHERE cidade = ?",
                new BeanPropertyRowMapper<>(CidadeEstado.class), cidadeEstado.getCidade());
        if (cidades.isEmpty()) {
            connection.update("INSERT INTO cidade_estado (cidade, estado) VALUES (?, ?)", cidadeEstado.getCidade(), cidadeEstado.getEstado());
            System.out.println("Linha inserida na tabela CidadeEstado com sucesso no banco.");
            cidades = connection.query("SELECT cidade_estado_id FROM cidade_estado WHERE cidade = ?",
                    new BeanPropertyRowMapper<>(CidadeEstado.class), cidadeEstado.getCidade());
        }
        System.out.println("Id de %s, %s: %d".formatted(cidadeEstado.getCidade(), cidadeEstado.getEstado(), cidadeEstado.getCidade_estado_id()));
        //FIM

        //SELECT E INSERT PARA DEPARTAMENTO
        List<Departamento> departamentos = connection.query("SELECT departamento_id FROM departamento WHERE nome = ?",
                new BeanPropertyRowMapper<>(Departamento.class), departamento.getNome());
        if (departamentos.isEmpty()) {
            connection.update("INSERT INTO departamento (nome, fk_cidade_estado) VALUES (?, ?)", departamento.getNome(), cidadeEstado.getCidade_estado_id());
            System.out.println("Linha inserida na tabela Departamento com sucesso no banco.");
            departamentos = connection.query("SELECT departamento_id FROM departamento WHERE nome = ?",
                    new BeanPropertyRowMapper<>(Departamento.class), departamento.getNome());
        }
        System.out.println("Id de %s: %d".formatted(departamento.getNome(), departamento.getDepartamento_id()));
        //FIM

        //SELECT E INSERT PARA RELATORIO
        List<Relatorio> relatorios = connection.query("SELECT relatorio_id FROM relatorio WHERE dt_ocorrencia = ? AND fuga = ? AND camera_corporal = ? AND problemas_mentais = ? AND fk_departamento = ?",
                new BeanPropertyRowMapper<>(Relatorio.class), relatorio.getDataOcorrencia(), relatorio.getFuga(), relatorio.getCameraCorporal(), relatorio.getProblemasMentais(), departamento.getDepartamento_id());
        if (relatorios.isEmpty()) {
            connection.update("INSERT INTO relatorio (dt_ocorrencia, fuga, camera_corporal, problemas_mentais, fk_departamento) VALUES (?, ?, ?, ?, ?)", relatorio.getDataOcorrencia(), relatorio.getFuga(), relatorio.getCameraCorporal(), relatorio.getProblemasMentais(), departamento.getDepartamento_id());
            System.out.println("Linha inserida na tabela Relatório com sucesso no banco.");
            relatorios = connection.query("SELECT relatorio_id FROM relatorio WHERE dt_ocorrencia = ? AND fuga = ? AND camera_corporal = ? AND problemas_mentais = ? AND fk_departamento = ?",
                    new BeanPropertyRowMapper<>(Relatorio.class), relatorio.getDataOcorrencia(), relatorio.getFuga(), relatorio.getCameraCorporal(), relatorio.getProblemasMentais(), departamento.getDepartamento_id());
        }
        System.out.println("Id de relatório: %d".formatted(relatorio.getRelatorio_id()));
        //FIM

        //SELECT E INSERT PARA VITIMA
        List<Vitima> vitimas = connection.query("SELECT vitima_id FROM vitima WHERE idade = ? AND etnia = ? AND genero = ? AND armamento = ?",
                new BeanPropertyRowMapper<>(Vitima.class), vitima.getIdade(), vitima.getEtnia(), vitima.getGenero(), vitima.getArmamento());
        if (vitimas.isEmpty()) {
            connection.update("INSERT INTO vitima (idade, etnia, genero, armamento, fk_relatorio) VALUES (?, ?, ?, ?, ?)", vitima.getIdade(), vitima.getEtnia(), vitima.getGenero(), vitima.getArmamento(), relatorio.getRelatorio_id());
            System.out.println("Linha inserida na tabela Vítima com sucesso no banco.");
            vitimas = connection.query("SELECT vitima_id FROM vitima WHERE idade = ? AND etnia = ? AND genero = ? AND armamento = ?",
                    new BeanPropertyRowMapper<>(Vitima.class), vitima.getIdade(), vitima.getEtnia(), vitima.getGenero(), vitima.getArmamento());
        }
        System.out.println("Id de vítima: %d".formatted(vitima.getVitima_id()));
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

            while ((line = br.readLine()) != null) {
                Row currentLine = spreadsheets.createRow(lineIndex++);
                String[] column = line.split(",");

                if (column[0].equals("") || column[0].equals(null)) colunaRelatorio.setDataOcorrencia("");
                else colunaRelatorio.setDataOcorrencia(column[0].trim().toLowerCase());
                if (column[2].equals("") || column[2].equals(null)) colunaVitima.setIdade("");
                else colunaVitima.setIdade(column[2].trim().toLowerCase());
                if (column[3].equals("") || column[3].equals(null)) colunaVitima.setGenero("");
                else colunaVitima.setGenero(column[3].trim().toLowerCase());
                if (column[4].equals("") || column[4].equals(null)) colunaVitima.setArmamento("");
                else colunaVitima.setArmamento(column[4].trim().toLowerCase());
                if (column[5].equals("") || column[5].equals(null)) colunaVitima.setEtnia("");
                else colunaVitima.setEtnia(column[5].trim().toLowerCase());
                if (column[6].equals("") || column[6].equals(null)) colunaCidadeEstado.setCidade("");
                else colunaCidadeEstado.setCidade(column[6].trim().toLowerCase());
                if (column[7].equals("") || column[7].equals(null)) colunaCidadeEstado.setEstado("");
                else colunaCidadeEstado.setEstado(column[7].trim().toLowerCase());
                if (column[8].equals("") || column[8].equals(null)) colunaRelatorio.setFuga("");
                else colunaRelatorio.setFuga(column[8].trim().toLowerCase());
                if (column[9].equals("") || column[9].equals(null)) colunaRelatorio.setCameraCorporal("");
                else colunaRelatorio.setCameraCorporal(column[9].trim().toLowerCase());
                if (column[10].equals("") || column[10].equals(null)) colunaRelatorio.setProblemasMentais("");
                else colunaRelatorio.setProblemasMentais(column[10].trim().toLowerCase());
                if (column[11].equals("") || column[11].equals(null)) colunaDepartamento.setNome("");
                else colunaDepartamento.setNome(column[11].trim().toLowerCase());

                inserirLinhaNoBanco(colunaCidadeEstado, colunaDepartamento, colunaRelatorio, colunaVitima);

                for (int i = 0; i < column.length; i++) {
                    if (i == 2) i++;
                    currentLine.createCell(i).setCellValue(column[i].trim());
                }
            }

            String datasetName = new File(csvFile).getName();
            String xlsxName = datasetName.substring(0, datasetName.lastIndexOf('.')) + ".xlsx";

            try (FileOutputStream writer = new FileOutputStream("src/" + xlsxName)) {
                workbook.write(writer);
                System.out.println("file XLSX created successfully: src/" + xlsxName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

