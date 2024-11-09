package com.project.services;

import com.project.model.*;
import com.project.provider.ConnectionProviderS3;
import com.project.provider.DBConnectionProvider;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DatasetToDatabase {
    private final CidadeEstado colunaCidadeEstado = new CidadeEstado();
    private final Departamento colunaDepartamento = new Departamento();
    private final Vitima colunaVitima = new Vitima();
    private final Relatorio colunaRelatorio = new Relatorio();

    DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
    JdbcTemplate connection = dbConnectionProvider.getDatabaseConnection();
    ConnectionProviderS3 connectionProviderS3 = new ConnectionProviderS3();
    ServiceS3 serviceS3 = new ServiceS3(connectionProviderS3);

    private void insertIntoDatabase(CidadeEstado cidadeEstado, Departamento departamento, Relatorio relatorio, Vitima vitima) {
        List<CidadeEstado> cidades = connection.query("SELECT cidade_estado_id FROM cidade_estado WHERE cidade = ? AND estado = ?",
                new BeanPropertyRowMapper<>(CidadeEstado.class), cidadeEstado.getCidade(), cidadeEstado.getEstado());
        if (cidades.isEmpty()) {
            connection.update("INSERT INTO cidade_estado (cidade, estado) VALUES (?, ?)", cidadeEstado.getCidade(), cidadeEstado.getEstado());
            System.out.println("Linha inserida na tabela CidadeEstado com sucesso no banco.");
            cidades = connection.query("SELECT cidade_estado_id FROM cidade_estado WHERE cidade = ? AND estado = ?",
                    new BeanPropertyRowMapper<>(CidadeEstado.class), cidadeEstado.getCidade(), cidadeEstado.getEstado());
        }

        System.out.println("Id de %s, %s: %d".formatted(cidadeEstado.getCidade(), cidadeEstado.getEstado(), cidades.get(0).getCidade_estado_id()));

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

    public void extractAndInsert() {
        String bucket = serviceS3.getFirstBucket();
        String key = serviceS3.getFirstObject(bucket);

        try (InputStream inputStream = serviceS3.getObjectInputStream(bucket, key);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");


            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;

                String[] rowData = new String[row.getPhysicalNumberOfCells()];

                for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                    Cell cell = row.getCell(i);
                    String actualColumn = "";

                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                actualColumn = cell.getStringCellValue();
                                break;
                            case NUMERIC:
                                actualColumn = String.valueOf(cell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                actualColumn = String.valueOf(cell.getBooleanCellValue());
                                break;
                            default:
                                actualColumn = "";
                        }
                    }

                    if (i == 0 && actualColumn.contains("2024")) {

                        if (i == 0 && actualColumn == null || actualColumn.isBlank())
                            colunaRelatorio.setDataOcorrencia(new Date());
                        else if(i == 0) {
                            Date dataFormatada = formato.parse(actualColumn);
                            colunaRelatorio.setDataOcorrencia(dataFormatada);
                        }

                        if (i == 1 && actualColumn.isBlank() || actualColumn == null) colunaVitima.setNome("");
                        else if(i == 1) colunaVitima.setNome(actualColumn);
                        if (i == 2 && actualColumn.isBlank() || actualColumn == null) colunaVitima.setIdade(0);
                        else if(i == 2) colunaVitima.setIdade(Integer.valueOf(actualColumn));
                        if (i == 3 && actualColumn.isBlank() || actualColumn == null) colunaVitima.setGenero("");
                        else if(i == 3) colunaVitima.setGenero(actualColumn);
                        if (i == 4 && actualColumn.isBlank() || actualColumn == null) colunaVitima.setArmamento("");
                        else if(i == 4) colunaVitima.setArmamento(actualColumn);
                        if (i == 5 && actualColumn.isBlank() || actualColumn == null) colunaVitima.setEtnia("");
                        else if(i == 5) colunaVitima.setEtnia(actualColumn);
                        if (i == 6 && actualColumn.isBlank() || actualColumn == null) colunaCidadeEstado.setCidade("");
                        else if(i == 6) colunaCidadeEstado.setCidade(actualColumn);
                        if (i == 7 && actualColumn.isBlank() || actualColumn == null) colunaCidadeEstado.setEstado("");
                        else if(i == 7) colunaCidadeEstado.setEstado(actualColumn);
                        if (i == 8 && actualColumn.isBlank() || actualColumn == null) colunaRelatorio.setFuga("");
                        else if(i == 8) colunaRelatorio.setFuga(actualColumn);
                        if (i == 9 && actualColumn.isBlank() || actualColumn == null) colunaRelatorio.setCameraCorporal(null);
                        else if(i == 9) colunaRelatorio.setCameraCorporal(Boolean.valueOf(actualColumn));
                        if (i == 10 && actualColumn.isBlank() || actualColumn == null) colunaRelatorio.setProblemasMentais(null);
                        else if(i == 10) colunaRelatorio.setProblemasMentais(Boolean.valueOf(actualColumn));

                        if (i == 11 && actualColumn.isBlank() || actualColumn == null) colunaDepartamento.setNome("");
                        else if(i == 11) {
                            String[] column11 = actualColumn.split(",");
                            colunaDepartamento.setNome(column11[0]);
                        }
                    }
                }
                insertIntoDatabase(colunaCidadeEstado, colunaDepartamento, colunaRelatorio, colunaVitima);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("-----------");
        System.out.println("Inserção finalizada");

    }
}






