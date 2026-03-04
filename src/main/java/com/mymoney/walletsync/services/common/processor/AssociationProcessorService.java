package com.mymoney.walletsync.services.common.processor;

import com.mymoney.walletsync.model.common.dto.CategoryDTO;
import com.mymoney.walletsync.model.common.dto.MovementAssociationDTO;
import com.mymoney.walletsync.model.common.enums.CategoryType;
import com.mymoney.walletsync.model.santander.dto.AssociatedSantanderPaymentByMonthDTO;
import com.mymoney.walletsync.model.santander.dto.AssociatedSantanderPaymentByYearDTO;
import com.mymoney.walletsync.model.santander.dto.AssociatedSantanderPaymentDTO;
import com.mymoney.walletsync.model.santander.dto.SantanderPaymentMovementDTO;
import com.mymoney.walletsync.services.common.CategoryService;
import com.mymoney.walletsync.services.common.MovementCategoryAssociationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Month;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssociationProcessorService {

    private final MovementCategoryAssociationService movementCategoryAssociationService;
    private final CategoryService categoryService;

    private final String OTHERS_CATEGORY_EXPENSES = "OTHERS_EXPENSES";
    private final String OTHERS_CATEGORY_INCOMES = "OTHERS_INCOMES";

    /**
     * Proceso principal: Clasifica una lista de movimientos bancarios.
     * Incluye medición de tiempo de ejecución para monitoreo de performance.
     */
    public List<AssociatedSantanderPaymentDTO> process(List<SantanderPaymentMovementDTO> movements) {

        if(movements.isEmpty()) {
            log.debug("Sin movimientos para procesar.");
            return Collections.emptyList();
        }

        long startTime = System.currentTimeMillis(); // Inicio del cronómetro
        log.debug("Iniciando procesamiento de asociación para {} movimientos", movements.size());

        List<AssociatedSantanderPaymentDTO> listToReturn = new ArrayList<>();

        try {
            // Preparamos el contenedor con todas las categorías existentes
            initEmptyListToReturn(listToReturn);

            // Cargamos reglas de asociación
            List<MovementAssociationDTO> categoryAssociationAll = movementCategoryAssociationService.findAll();

            for (SantanderPaymentMovementDTO movement : movements) {
                MovementAssociationDTO association = checkIfCategoryAssociationFound(movement, categoryAssociationAll);

                if (association != null) {
                    setCategoryToMovement(association, movement, listToReturn);
                } else {
                    setCategoryOthersToMovement(movement, listToReturn);
                }
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.debug("Procesamiento completado exitosamente en {} ms", duration);

        } catch (Exception e) {
            log.error("Error inesperado durante el procesamiento de asociaciones: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar asociaciones de movimientos", e);
        }

        return listToReturn;
    }

    /**
     * Maneja movimientos que no encajan en ninguna regla conocida.
     * Separa automáticamente entre Ingresos y Gastos "Otros".
     */
    private void setCategoryOthersToMovement(SantanderPaymentMovementDTO movement,
                                             List<AssociatedSantanderPaymentDTO> listToReturn) {
        Optional<AssociatedSantanderPaymentDTO> associatedDTO;
        CategoryType categoryTypeToAssign;

        // Determinamos si es ingreso o gasto por el signo del importe
        if (movement.amount().compareTo(BigDecimal.ZERO) > 0) {
            categoryTypeToAssign = CategoryType.INCOME;
            associatedDTO = listToReturn.stream()
                    .filter(dto -> OTHERS_CATEGORY_INCOMES.equals(dto.getCategory().getCategoryName()))
                    .findFirst();
        } else {
            categoryTypeToAssign = CategoryType.EXPENSE;
            associatedDTO = listToReturn.stream()
                    .filter(dto -> OTHERS_CATEGORY_EXPENSES.equals(dto.getCategory().getCategoryName()))
                    .findFirst();
        }

        if (associatedDTO.isPresent()) {
            associatedDTO.get().getMovements().add(movement);
        } else {
            // Caso excepcional: La categoría "OTHERS" no existe en la DB, procedemos a crearla al vuelo
            log.warn("Categoría por defecto '{}' no encontrada. Creándola en el sistema...",
                    categoryTypeToAssign == CategoryType.INCOME ? OTHERS_CATEGORY_INCOMES : OTHERS_CATEGORY_EXPENSES);

            final String categoryNameToSave = (categoryTypeToAssign == CategoryType.EXPENSE)
                    ? OTHERS_CATEGORY_EXPENSES : OTHERS_CATEGORY_INCOMES;

            CategoryDTO saved = categoryService.save(new CategoryDTO(null, categoryNameToSave, categoryTypeToAssign.toString()));

            if (saved == null) {
                log.error("Fallo crítico: No se pudo crear la categoría de respaldo {}", categoryNameToSave);
                throw new RuntimeException("Category OTHERS could not be saved");
            }

            CategoryDTO categoryDTO = categoryService.findById(saved.getId());
            AssociatedSantanderPaymentDTO newAssociated = new AssociatedSantanderPaymentDTO(new ArrayList<>(), categoryDTO);
            newAssociated.getMovements().add(movement);
            listToReturn.add(newAssociated);
        }
    }

    /**
     * Agrega el movimiento a la lista de la categoría que le corresponde.
     */
    private void setCategoryToMovement(MovementAssociationDTO association,
                                       SantanderPaymentMovementDTO movement,
                                       List<AssociatedSantanderPaymentDTO> listToReturn) {
        for (AssociatedSantanderPaymentDTO associated : listToReturn) {
            if (Objects.equals(associated.getCategory().getId(), association.getCategoryId())) {
                associated.getMovements().add(movement);
                return; // Salimos una vez encontrado para evitar iteraciones innecesarias
            }
        }
    }

    /**
     * Inicializa la lista de retorno con todas las categorías disponibles en el sistema (vacías).
     */
    private void initEmptyListToReturn(List<AssociatedSantanderPaymentDTO> listToReturn) {
        log.debug("Inicializando contenedores para todas las categorías disponibles.");
        List<CategoryDTO> categoriesAll = categoryService.findAll();
        for (CategoryDTO categoryDTO : categoriesAll) {
            listToReturn.add(new AssociatedSantanderPaymentDTO(new ArrayList<>(), categoryDTO));
        }
    }

    /**
     * Lógica de matching: Comprueba si el texto del movimiento contiene la palabra clave de asociación.
     */
    private MovementAssociationDTO checkIfCategoryAssociationFound(SantanderPaymentMovementDTO movement,
                                                                   List<MovementAssociationDTO> associations) {
        String label = movement.operationLabel().toUpperCase();

        for (MovementAssociationDTO association : associations) {
            String keyword = association.getAssociationWord().toUpperCase();
            if (label.contains(keyword)) {
                return association;
            }
        }
        return null;
    }

    /**
     * Genera un reporte anual agrupando los movimientos por meses.
     */
    public AssociatedSantanderPaymentByYearDTO processByYear(List<SantanderPaymentMovementDTO> movements, Long year) {
        log.debug("Generando reporte anual para el año: {}", year);
        long startTime = System.currentTimeMillis();
        List<AssociatedSantanderPaymentByMonthDTO> monthlyReports = new ArrayList<>();

        for (Month month : Month.values()) {
            List<SantanderPaymentMovementDTO> movementsOfMonth = getPaymentsByMonth(month, movements);

            log.debug("Mes {}: Encontrados {} movimientos", month, movementsOfMonth.size());

            monthlyReports.add(new AssociatedSantanderPaymentByMonthDTO(
                    month.getValue(),
                    process(movementsOfMonth) // Reutilizamos la lógica de asociación por categoría
            ));
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.debug("Reporte anual generado para el año: {}. En {} ms", year, duration);
        return new AssociatedSantanderPaymentByYearDTO(year, monthlyReports);
    }

    /**
     * Filtra una lista de movimientos quedándose solo con los de un mes específico.
     */
    private List<SantanderPaymentMovementDTO> getPaymentsByMonth(Month month, List<SantanderPaymentMovementDTO> movements) {
        List<SantanderPaymentMovementDTO> filtered = new ArrayList<>();
        for (SantanderPaymentMovementDTO m : movements) {
            if (m.operationDate().getMonth().equals(month)) {
                filtered.add(m);
            }
        }
        return filtered;
    }
}