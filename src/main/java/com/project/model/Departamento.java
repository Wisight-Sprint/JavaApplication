package com.project.model;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Departamento extends Tratativa{
    private String nome;
    private Integer departamento_id;

    public Departamento() {}

    public Departamento(Departamento colunaDepartamento) {}

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

                Cell cell11 = row.getCell(11);

                Date cellDtOcorrencia;

                cellDtOcorrencia = new Date();

                Date dtLimite;

                dtLimite = formato.parse("2024-01-01");

                if (cellDtOcorrencia.before(dtLimite))
                    continue;

                String[] nomePrimeiroDep = new String[]{(cell11 != null && cell11.getCellType() == CellType.STRING) ? cell11.getStringCellValue() : ""};
                nomePrimeiroDep = nomePrimeiroDep[0].split(",");
                String cellDepartamentoNome = nomePrimeiroDep[0].trim();

//                colunaDepartamento.setNome(cellDepartamentoNome);
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

    public Integer getDepartamento_id() {
        return departamento_id;
    }

    public void setDepartamento_id(Integer departamento_id) {
        this.departamento_id = departamento_id;
    }
}
