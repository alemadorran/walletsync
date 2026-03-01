package com.mymoney.walletsync.controller;

import com.mymoney.walletsync.model.santander.dto.SantanderPaymentMovementDTO;
import com.mymoney.walletsync.services.santander.SantanderPaymentMovementService;
import com.mymoney.walletsync.services.santander.pdfProcessor.PdfParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/payments/santander")
@RequiredArgsConstructor
public class SantanderFileUploadController {

    private final SantanderPaymentMovementService santanderService;

    private final PdfParserService pdfParserService;

    private final  SantanderPaymentMovementService santanderPaymentMovementService;

    @PostMapping("/upload-pdf")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Por favor seleccione un archivo PDF.");
        }

        try {

            // 1. Aquí llamarías a tu servicio de parseo de PDF
            List<SantanderPaymentMovementDTO> movimientos = pdfParserService.extractMovements(file);

            //Verificar que los movimientos no se han registrado ya

            // 2. Llamar al servicio proporcionado para guardar los datos
            santanderPaymentMovementService.saveList(movimientos);

            return ResponseEntity.ok("Archivo PDF procesado y movimientos guardados exitosamente.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el archivo: " + e.getMessage());
        }
    }
}
