package com.project.model;

import com.project.provider.ConnectionProviderS3;
import com.project.provider.DBConnectionProvider;
import com.project.services.ServiceS3;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Vitima extends Tratativa{
    private String nome;
    private Integer idade;
    private String etnia;
    private String genero;
    private String armamento;
    private Integer vitima_id;

    public Vitima() {}

    public Vitima(Vitima colunaVitima) {}

    @Override
    public void writeLog(String message) throws IOException {
        writerlog.write(message + "\n");
        writerlog.flush();
    }

    @Override
    public void tratativaDados() throws IOException {
        try (InputStream inputStream = serviceS3.getObjectInputStream(bucket, xlsxKey);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

            for (Row row : sheet) {
                if (row.getRowNum() == 0)
                    continue;

                Cell cell1 = row.getCell(1);
                Cell cell2 = row.getCell(2);
                Cell cell3 = row.getCell(3);
                Cell cell4 = row.getCell(4);
                Cell cell5 = row.getCell(5);

                Date cellDtOcorrencia;

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

                cellNomeVitima = cellNomeVitima.replaceAll(" ", "");
                cellGeneroVitima = cellGeneroVitima.replaceAll(" ", "");
                cellArmamento = cellArmamento.replaceAll(" ", "");
                cellEtniaVitima = cellEtniaVitima.replaceAll(" ", "");

//                colunaVitima.setNome(cellNomeVitima);
//                colunaVitima.setIdade(cellIdadeVitima);
//                colunaVitima.setGenero(cellGeneroVitima);
//                colunaVitima.setArmamento(cellArmamento);
//                colunaVitima.setEtnia(cellEtniaVitima);
//
//                insertIntoDatabase(colunaCidadeEstado, colunaDepartamento, colunaRelatorio, colunaVitima);
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getEtnia() {
        return etnia;
    }

    public void setEtnia(String etnia) {
        this.etnia = etnia;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getArmamento() {
        return armamento;
    }

    public void setArmamento(String armamento) {
        this.armamento = armamento;
    }

    public Integer getVitima_id() {
        return vitima_id;
    }

    public void setVitima_id(Integer vitima_id) {
        this.vitima_id = vitima_id;
    }
}