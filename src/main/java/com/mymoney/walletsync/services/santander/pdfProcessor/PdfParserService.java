package com.mymoney.walletsync.services.santander.pdfProcessor;

import com.mymoney.walletsync.model.santander.dto.SantanderPaymentMovementDTO;
import lombok.extern.slf4j.Slf4j; // Importante para usar log.info, log.error, etc.
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j // Esta anotación de Lombok crea automáticamente la variable 'log'
public class PdfParserService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern DATE_PATTERN = Pattern.compile("(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(-?\\d+(?:\\.\\d{3})*,\\d{2})$");

    public List<SantanderPaymentMovementDTO> extractMovements(MultipartFile file) throws IOException {
        log.info("Iniciando el procesamiento del archivo PDF: {}", file.getOriginalFilename());

        List<SantanderPaymentMovementDTO> movements = new ArrayList<>();
        List<LocalDate> operationDateList = new ArrayList<>();
        List<String> operationLabelList = new ArrayList<>();
        List<BigDecimal> operationAmountList = new ArrayList<>();
        List<BigDecimal> operationBalanceList = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            String[] lines = text.split("\\r?\\n");

            log.debug("Se han extraído {} líneas del PDF", lines.length);

            for (String line : lines) {
                String trimmedLine = line.trim();

                // 1. FECHA
                Matcher dateMatcher = DATE_PATTERN.matcher(trimmedLine);
                if (dateMatcher.find()) {
                    // group(0) contiene solo la parte que coincide (ej: "08/10/2025")
                    String dateOnly = dateMatcher.group(0);
                    operationDateList.add(LocalDate.parse(dateOnly, DATE_FORMATTER));
                }

                // 2. ETIQUETA/CONCEPTO
                if(trimmedLine.contains("Compra") || trimmedLine.contains("Devolucion") ||
                        trimmedLine.contains("Transaccion") || trimmedLine.contains("Bizum") ||
                        trimmedLine.contains("Recibo")){
                    operationLabelList.add(trimmedLine);
                }

                // 3. IMPORTES
                if(trimmedLine.contains(" EUR ")){
                    String[] partes = trimmedLine.split("EUR");
                    if (partes.length >= 2) {
                        String importeConDescripcion = partes[0].trim();
                        Matcher matcher = AMOUNT_PATTERN.matcher(importeConDescripcion);

                        if (matcher.find()) {
                            operationAmountList.add(cleanAndParse(matcher.group(1)));
                            operationBalanceList.add(cleanAndParse(partes[1].trim()));
                        } else {
                            log.warn("Se encontró 'EUR' pero el patrón de importe falló en la línea: {}", trimmedLine);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error crítico al procesar el PDF {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new RuntimeException("Error en el procesamiento de PDF", e);
        }

        //Los dos primeros valores de operationAmountList y operationBalanceList deben ser desechados.
        operationAmountList.removeFirst();
        operationAmountList.removeFirst();
        operationBalanceList.removeFirst();
        operationBalanceList.removeFirst();

        int size = operationDateList.size();
        log.info("Extracción finalizada. Datos encontrados: Fechas({}), Etiquetas({}), Importes({})",
                size, operationLabelList.size(), operationAmountList.size());

        if(size == operationLabelList.size() && size == operationAmountList.size() && size == operationBalanceList.size()){
            for(int i = 0; i < size; i++){
                movements.add(new SantanderPaymentMovementDTO(
                        null,
                        operationDateList.get(i),
                        operationLabelList.get(i),
                        operationAmountList.get(i),
                        operationBalanceList.get(i)
                ));
            }
            log.info("Sincronización exitosa: {} movimientos listos para guardar.", movements.size());
        } else {
            log.error("DESINCRONIZACIÓN DETECTADA: Las listas tienen tamaños diferentes. No se pueden emparejar los datos.");
        }

        return movements;
    }

    private BigDecimal cleanAndParse(String value) {
        try {
            String cleanValue = value.replace(".", "").replace(",", ".");
            cleanValue = cleanValue.replaceAll("[^\\d.-]", "");
            return new BigDecimal(cleanValue);
        } catch (Exception e) {
            log.error("Fallo al convertir el valor '{}' a BigDecimal", value);
            return BigDecimal.ZERO;
        }
    }

    public boolean isDateFormat(String text) {
        return DATE_PATTERN.matcher(text).find();
    }
}