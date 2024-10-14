package com.project.services;

import com.project.model.*;
import com.project.provider.DBConnectionProvider;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TransformCsvToXlsx {

    // Instâncias dos processadores de cada coluna
    private final CidadeEstado colunaCidadeEstado = new CidadeEstado();
    private final Departamento colunaDepartamento = new Departamento();
    private final Vitima colunaVitima = new Vitima();
    private final Relatorio colunaRelatorio = new Relatorio();

    DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
    JdbcTemplate connection = dbConnectionProvider.getDatabaseConnection();

    private void inserirLinhaNoBanco(CidadeEstado cidadeEstado, Departamento departamento, Relatorio relatorio, Vitima vitima) {
        connection.update("INSERT INTO cidade_estado (cidade, estado) VALUES (?, ?)", cidadeEstado.getCidade(), cidadeEstado.getEstado());
        System.out.println("Linha inserida na tabela CidadeEstado com sucesso no banco.");

        connection.update("INSERT INTO departamento (nome) VALUES (?)", departamento.getNome());
        System.out.println("Linha inserida na tabela Departamento com sucesso no banco.");

        connection.update("INSERT INTO relatorio (dt_ocorrencia, fuga, camera_corporal, problemas_mentais) VALUES (?, ?, ?, ?)", relatorio.getDataOcorrencia(), relatorio.getFuga(), relatorio.getCameraCorporal(), relatorio.getProblemasMentais());
        System.out.println("Linha inserida na tabela Relatório com sucesso no banco.");

        connection.update("INSERT INTO vitima (idade, etnia, genero, armamento) VALUES (?, ?, ?, ?)", vitima.getIdade(), vitima.getEtnia(), vitima.getGenero(), vitima.getArmamento());
        System.out.println("Linha inserida na tabela Vítima com sucesso no banco.");
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

                colunaRelatorio.setDataOcorrencia(column[0].trim().toLowerCase());
                colunaVitima.setIdade(column[2].trim().toLowerCase());
                colunaVitima.setGenero(column[3].trim().toLowerCase());
                colunaVitima.setArmamento(column[4].trim().toLowerCase());
                colunaVitima.setEtnia(column[5].trim().toLowerCase());
                colunaCidadeEstado.setCidade(column[6].trim().toLowerCase());
                colunaCidadeEstado.setEstado(column[7].trim().toLowerCase());
                colunaRelatorio.setFuga(column[8].trim().toLowerCase());
                colunaRelatorio.setCameraCorporal(column[9].trim().toLowerCase());
                colunaRelatorio.setProblemasMentais(column[10].trim().toLowerCase());
                colunaDepartamento.setNome(column[11].trim().toLowerCase());

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

