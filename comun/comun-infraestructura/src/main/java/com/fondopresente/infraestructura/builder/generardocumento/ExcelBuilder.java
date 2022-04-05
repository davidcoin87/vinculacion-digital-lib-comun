package com.fondopresente.infraestructura.builder.generardocumento;

import com.fondopresente.infraestructura.excepcion.ExcepcionTecnica;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelBuilder {
    private static final String ERROR_OBTENIENDO_EL_VALOR_DE_OBJETO = "Error obteniendo valor del objeto para la celda";
    private static final int ANCHO_CELDA = 6000;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelBuilder.class);

    private final XSSFWorkbook wb;
    private XSSFSheet sheet;
    private int columnaInicialFilaHorizontal;

    public ExcelBuilder() {
        this.wb = new XSSFWorkbook();
    }

    public XSSFWorkbook getXSSFSheet() {
        return wb;
    }

    public void agregarHoja(String nombreHoja){
        this.sheet = wb.createSheet(nombreHoja);
    }

    public void agregarTitulosHorizontales(String[] headerHorizontal, int filaInicial){
        crearTituloColummaHorizontal(headerHorizontal, filaInicial);
    }
    public void agregarTituloColummaVertical(String[] headerVertical, Object[] datosVertical, int columnaInicial, int filaInicial){
        crearTituloYDatoColummaVertical(headerVertical, datosVertical, columnaInicial, filaInicial);
    }

    public void agregarDatosHorizontales(List<?> filas, int filaInicial, int columnaInicial){
        crearFilasHoja(filas, filaInicial, columnaInicial);
    }

    private void crearTituloColummaHorizontal(String[] headerHorizontal, int filaInicial) {
        Font font;
        CellStyle cellStyle;
        cellStyle = wb.createCellStyle();
        font = wb.createFont();
        font.setBold(true);
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        Row row = sheet.createRow(filaInicial);
        int i = 0;

        for (String titulo : headerHorizontal) {
            sheet.setColumnWidth(i, ANCHO_CELDA);
            Cell cell = row.createCell(i, CellType.STRING);
            cell.setCellValue(titulo);
            cell.setCellStyle(cellStyle);
            i++;
        }
    }
    private void crearTituloYDatoColummaVertical(String[] headerVertical, Object[] datosVertical, int columnaInicial, int filaInicial) {
        for (String titulo : headerVertical) {
            Row row = sheet.createRow(filaInicial);
            sheet.setColumnWidth(columnaInicial, ANCHO_CELDA);
            crearValorCeldaVertical(titulo, columnaInicial, true, row);
            crearValorCeldaVertical(datosVertical[filaInicial], columnaInicial + 1, false, row);
            filaInicial++;
        }
    }

    private void crearValorCeldaVertical(Object object, int columnaInicial, Boolean isNegrita, Row row) {
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(isNegrita);
        cellStyle.setFont(font);
        Cell cell = row.createCell(columnaInicial, CellType.STRING);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        if(isNumero(object)){
            cell = row.createCell(columnaInicial, CellType.NUMERIC);
            cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        }
        cell.setCellValue(String.valueOf(object));
        cell.setCellStyle(cellStyle);
    }

    private void crearFilasHoja(List<?> filas, int filaInicial, int columnaInicial) {
        AtomicInteger filaInicialDatos = new AtomicInteger(filaInicial);

        filas.forEach(fila ->{
            columnaInicialFilaHorizontal = columnaInicial;
            Row row = this.sheet.createRow(filaInicialDatos.get());
            Field[] fields = fila.getClass().getDeclaredFields();
            for (int j = 0; j < fields.length; j++) {
                crearValorCelda(fila, row, fields, j);
            }
            filaInicialDatos.getAndIncrement();
        });
    }

    private void crearValorCelda(Object fila, Row row, Field[] fields, int j) {
        Font font;
        CellStyle cellStyle = this.wb.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.GENERAL);
        font = this.wb.createFont();
        font.setBold(false);
        cellStyle.setFont(font);
        try {
            Field field = fields[j];
            if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isFinal(field.getModifiers())) {
                field.setAccessible(true);
                Cell  cell = row.createCell(columnaInicialFilaHorizontal, CellType.STRING);
                if(isNumero(field.get(fila))){
                    cell = row.createCell(columnaInicialFilaHorizontal, CellType.NUMERIC);
                    cellStyle.setAlignment(HorizontalAlignment.RIGHT);
                }
                cell.setCellValue(String.valueOf(field.get(fila)));
                cell.setCellStyle(cellStyle);
                field.setAccessible(false);
                columnaInicialFilaHorizontal++;
            }
        } catch (IllegalAccessException e) {
            LOGGER.error(ERROR_OBTENIENDO_EL_VALOR_DE_OBJETO, e);
            throw new ExcepcionTecnica(ERROR_OBTENIENDO_EL_VALOR_DE_OBJETO, e);
        }
    }

    private boolean isNumero(Object object){
        return (object instanceof Long || object instanceof Integer || object instanceof Double || object instanceof Short);
    }
}
