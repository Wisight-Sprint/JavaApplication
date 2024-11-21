package com.project.services;

import com.project.model.*;
import com.project.provider.ConnectionProviderS3;
import com.project.provider.DBConnectionProvider;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
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

    String bucket = serviceS3.getFirstBucket();
    String xlsxKey = serviceS3.getFirstXlsxKey(bucket);

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    BufferedWriter writerlog = new BufferedWriter(new OutputStreamWriter(byteArrayOutputStream));

    Integer logLineCounter = 0;
    public DatasetToDatabase() throws IOException {
    }

    private void writeLog(String message) throws IOException {
        writerlog.write(message + "\n");
        writerlog.flush();
    }



    private void insertIntoDatabase(CidadeEstado cidadeEstado, Departamento departamento, Relatorio relatorio, Vitima vitima) throws IOException {
        boolean inserted = false;
        List<CidadeEstado> cidades = connection.query("SELECT cidade_estado_id FROM cidade_estado WHERE cidade = ? AND estado = ?",
                new BeanPropertyRowMapper<>(CidadeEstado.class), cidadeEstado.getCidade(), cidadeEstado.getEstado());
        if (cidades.isEmpty()) {
            connection.update("INSERT INTO cidade_estado (cidade, estado) VALUES (?, ?)", cidadeEstado.getCidade(), cidadeEstado.getEstado());
            System.out.println("Linha inserida na tabela CidadeEstado com sucesso no banco.");
            cidades = connection.query("SELECT cidade_estado_id FROM cidade_estado WHERE cidade = ? AND estado = ?",
                    new BeanPropertyRowMapper<>(CidadeEstado.class), cidadeEstado.getCidade(), cidadeEstado.getEstado());
            inserted = true;
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
            inserted = true;
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
            inserted = true;
        }

        System.out.printf("Id de relatório: %d%n", relatorios.get(0).getRelatorio_id());

        List<Vitima> vitimas = connection.query("SELECT vitima_id FROM vitima WHERE nome = ? AND idade = ? AND etnia = ? AND genero = ? AND armamento = ?",
                new BeanPropertyRowMapper<>(Vitima.class), vitima.getNome(), vitima.getIdade(), vitima.getEtnia(), vitima.getGenero(), vitima.getArmamento());
        if (vitimas.isEmpty()) {
            connection.update("INSERT INTO vitima (nome, idade, etnia, genero, armamento, fk_relatorio, fk_departamento) VALUES (?, ?, ?, ?, ?, ?, ?)", vitima.getNome(), vitima.getIdade(), vitima.getEtnia(), vitima.getGenero(), vitima.getArmamento(), relatorios.get(0).getRelatorio_id(), departamentos.get(0).getDepartamento_id());
            System.out.println("Linha inserida na tabela Vítima com sucesso no banco.");
            vitimas = connection.query("SELECT vitima_id FROM vitima WHERE nome = ? AND idade = ? AND etnia = ? AND genero = ? AND armamento = ?",
                    new BeanPropertyRowMapper<>(Vitima.class), vitima.getNome(), vitima.getIdade(), vitima.getEtnia(), vitima.getGenero(), vitima.getArmamento());
            inserted = true;
        }
        System.out.println("Id de vítima: %d".formatted(vitimas.get(0).getVitima_id()));
        logLineCounter++;

        if (inserted) {
            writeLog("Linha %d do DataSet inserida no banco".formatted(logLineCounter));
        } else {
            writeLog("Linha %d do DataSet lida no banco".formatted(logLineCounter));
        }
        //FIM

        cidades.clear();
        departamentos.clear();
        relatorios.clear();
        vitimas.clear();

    }

    public void extractAndInsert() throws IOException {
        try (InputStream inputStream = serviceS3.getObjectInputStream(bucket, xlsxKey);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;

                Cell cell0 = row.getCell(0);
                Cell cell1 = row.getCell(1);
                Cell cell2 = row.getCell(2);
                Cell cell3 = row.getCell(3);
                Cell cell4 = row.getCell(4);
                Cell cell5 = row.getCell(5);
                Cell cell6 = row.getCell(6);
                Cell cell7 = row.getCell(7);
                Cell cell8 = row.getCell(8);
                Cell cell9 = row.getCell(9);
                Cell cell10 = row.getCell(10);
                Cell cell11 = row.getCell(11);

                Date cellDtOcorrencia;
                if (cell0 != null && cell0.getCellType() == CellType.NUMERIC)
                    cellDtOcorrencia = cell0.getDateCellValue();
                else if (cell0 != null && cell0.getCellType() == CellType.STRING)
                    cellDtOcorrencia = formato.parse(cell0.getStringCellValue());
                else
                    cellDtOcorrencia = new Date();

                Date dtLimite;
                dtLimite = formato.parse("2024-01-01");

                if (cellDtOcorrencia.before(dtLimite))
                    continue;

                String cellNomeVitima = (cell1 != null && cell1.getCellType() == CellType.STRING) ? cell1.getStringCellValue().toUpperCase() : "";
                Integer cellIdadeVitima = (cell2 != null && cell2.getCellType() == CellType.NUMERIC) ? (int) cell2.getNumericCellValue() : 0;
                String cellGeneroVitima = (cell3 != null && cell3.getCellType() == CellType.STRING) ? cell3.getStringCellValue().toUpperCase() : "";
                String cellArmamento = (cell4 != null && cell4.getCellType() == CellType.STRING) ? cell4.getStringCellValue().toUpperCase() : "";
                String cellEtniaVitima = (cell5 != null && cell5.getCellType() == CellType.STRING) ? cell5.getStringCellValue().toUpperCase() : "";
                String cellCidade = (cell6 != null && cell6.getCellType() == CellType.STRING) ? cell6.getStringCellValue().toUpperCase() : "";
                String cellEstado = (cell7 != null && cell7.getCellType() == CellType.STRING) ? cell7.getStringCellValue().toUpperCase() : "";
                String cellFuga = (cell8 != null && cell8.getCellType() == CellType.STRING) ? cell8.getStringCellValue().toUpperCase() : "";
                Boolean cellCameraCorporal = (cell9 != null && cell9.getCellType() == CellType.STRING) ? Boolean.valueOf(cell9.getStringCellValue()) : null;
                Boolean cellProblemasMentais = (cell10 != null && cell10.getCellType() == CellType.STRING) ? Boolean.valueOf(cell10.getStringCellValue()) : null;

                cellNomeVitima = cellNomeVitima.replaceAll(" ", "");
                cellGeneroVitima = cellGeneroVitima.replaceAll(" ", "");
                cellArmamento = cellArmamento.replaceAll(" ", "");
                cellEtniaVitima = cellEtniaVitima.replaceAll(" ", "");
                cellCidade = cellCidade.replaceAll(" ", "");
                cellEstado = cellEstado.replaceAll(" ", "");
                cellFuga = cellFuga.replaceAll(" ", "");

                String[] nomePrimeiroDep = new String[]{(cell11 != null && cell11.getCellType() == CellType.STRING) ? cell11.getStringCellValue() : ""};
                nomePrimeiroDep = nomePrimeiroDep[0].split(",");
                String cellDepartamentoNome = nomePrimeiroDep[0].trim();

                colunaCidadeEstado.setCidade(cellCidade);
                colunaCidadeEstado.setEstado(cellEstado);
                colunaDepartamento.setNome(cellDepartamentoNome);

                colunaRelatorio.setDataOcorrencia(cellDtOcorrencia);
                colunaRelatorio.setFuga(cellFuga);
                colunaRelatorio.setCameraCorporal(cellCameraCorporal);
                colunaRelatorio.setProblemasMentais(cellProblemasMentais);

                colunaVitima.setNome(cellNomeVitima);
                colunaVitima.setIdade(cellIdadeVitima);
                colunaVitima.setGenero(cellGeneroVitima);
                colunaVitima.setArmamento(cellArmamento);
                colunaVitima.setEtnia(cellEtniaVitima);

                insertIntoDatabase(colunaCidadeEstado, colunaDepartamento, colunaRelatorio, colunaVitima);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writerlog.flush();
            String logKey = serviceS3.createLogKey();
            serviceS3.createLog(bucket, logKey, byteArrayOutputStream);
        }

        System.out.println("-----------\nInserção finalizada");
        writerlog.close();
    }
}