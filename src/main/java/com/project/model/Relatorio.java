package com.project.model;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Relatorio extends Tratativa{
    private Date dataOcorrencia;
    private String fuga;
    private Boolean cameraCorporal;
    private Boolean problemasMentais;
    private Integer relatorio_id;

    public Relatorio() {}

    public Relatorio (Relatorio colunaRelatorio) {}

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

                Cell cell8 = row.getCell(8);
                Cell cell9 = row.getCell(9);
                Cell cell10 = row.getCell(10);

                Date cellDtOcorrencia;

                cellDtOcorrencia = new Date();

                Date dtLimite;

                dtLimite = formato.parse("2024-01-01");

                if (cellDtOcorrencia.before(dtLimite))
                    continue;

                String cellFuga = (cell8 != null && cell8.getCellType() == CellType.STRING) ? cell8.getStringCellValue().toUpperCase() : "";
                Boolean cellCameraCorporal = (cell9 != null && cell9.getCellType() == CellType.STRING) ? Boolean.valueOf(cell9.getStringCellValue()) : null;
                Boolean cellProblemasMentais = (cell10 != null && cell10.getCellType() == CellType.STRING) ? Boolean.valueOf(cell10.getStringCellValue()) : null;

                cellFuga = cellFuga.replaceAll(" ", "");


//                colunaRelatorio.setDataOcorrencia(cellDtOcorrencia);
//                colunaRelatorio.setFuga(cellFuga);
//                colunaRelatorio.setCameraCorporal(cellCameraCorporal);
//                colunaRelatorio.setProblemasMentais(cellProblemasMentais);
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

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }

    public String getFuga() {
        return fuga;
    }

    public void setFuga(String fuga) {
        this.fuga = fuga;
    }

    public Boolean getCameraCorporal() {
        return cameraCorporal;
    }

    public void setCameraCorporal(Boolean cameraCorporal) {
        this.cameraCorporal = cameraCorporal;
    }

    public Boolean getProblemasMentais() {
        return problemasMentais;
    }

    public void setProblemasMentais(Boolean problemasMentais) {
        this.problemasMentais = problemasMentais;
    }

    public Integer getRelatorio_id() {
        return relatorio_id;
    }

    public void setRelatorio_id(Integer relatorio_id) {
        this.relatorio_id = relatorio_id;
    }

    @Override
    public String toString() {
        return "Relatorio{" +
                "dataOcorrencia=" + dataOcorrencia +
                ", fuga='" + fuga + '\'' +
                ", cameraCorporal=" + cameraCorporal +
                ", problemasMentais=" + problemasMentais +
                ", relatorio_id=" + relatorio_id +
                '}';
    }
}
