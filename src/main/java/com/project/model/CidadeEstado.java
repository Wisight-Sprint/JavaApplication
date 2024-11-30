package com.project.model;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CidadeEstado extends Tratativa{
    private String cidade;
    private String estado;
    private Integer cidade_estado_id;
    private final CidadeEstado colunaCidadeEstado = new CidadeEstado();

    public CidadeEstado() {}

    public CidadeEstado(CidadeEstado colunaCidadeEstado) {}

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getCidade_estado_id() {
        return cidade_estado_id;
    }

    public void setCidade_estado_id(Integer cidade_estado_id) {
        this.cidade_estado_id = cidade_estado_id;
    }

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

                Cell cell6 = row.getCell(6);
                Cell cell7 = row.getCell(7);


                Date cellDtOcorrencia;

                cellDtOcorrencia = new Date();

                Date dtLimite;

                dtLimite = formato.parse("2024-01-01");

                if (cellDtOcorrencia.before(dtLimite))
                    continue;

                String cellCidade = (cell6 != null && cell6.getCellType() == CellType.STRING) ? cell6.getStringCellValue().toUpperCase() : "";
                String cellEstado = (cell7 != null && cell7.getCellType() == CellType.STRING) ? cell7.getStringCellValue().toUpperCase() : "";

                cellCidade = cellCidade.replaceAll(" ", "");
                cellEstado = cellEstado.replaceAll(" ", "");

//                colunaCidadeEstado.setCidade(cellCidade);
//                colunaCidadeEstado.setEstado(cellEstado);
//
//                insertIntoDatabase(colunaCidadeEstado);
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
