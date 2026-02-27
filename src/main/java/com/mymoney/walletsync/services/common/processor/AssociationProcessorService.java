package com.mymoney.walletsync.services.common.processor;

import com.mymoney.walletsync.model.common.dto.CategoryDTO;
import com.mymoney.walletsync.model.common.dto.MovementAssociationDTO;
import com.mymoney.walletsync.model.common.enums.CategoryType;
import com.mymoney.walletsync.model.santander.dto.AssociatedSantanderPaymentDTO;
import com.mymoney.walletsync.model.santander.dto.SantanderPaymentMovementDTO;
import com.mymoney.walletsync.services.common.CategoryService;
import com.mymoney.walletsync.services.common.MovementCategoryAssociationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssociationProcessorService {

    private final MovementCategoryAssociationService movementCategoryAssociationService;
    private final CategoryService categoryService;

    private final String OTHERS_CATEGORY_EXPENSES = "OTHERS_EXPENSES";
    private final String OTHERS_CATEGORY_INCOMES = "OTHERS_INCOMES";

    public List<AssociatedSantanderPaymentDTO> process(List<SantanderPaymentMovementDTO> movements) {

        List<AssociatedSantanderPaymentDTO> listToReturn = new ArrayList<>();

        initEmptyListToReturn(listToReturn);

        List<MovementAssociationDTO> categoryAssociationAll = movementCategoryAssociationService.findAll();

        for (SantanderPaymentMovementDTO santanderPaymentMovementDTO : movements) {

            MovementAssociationDTO movementAssociationDTO = checkIfCategoryAssociationFound(santanderPaymentMovementDTO, categoryAssociationAll);

            // We find a association
            if (movementAssociationDTO != null) {

                setCategoryToMovement(movementAssociationDTO, santanderPaymentMovementDTO, listToReturn);

            } else { //We don't find an association

                setCategoryOthersToMovement(santanderPaymentMovementDTO, listToReturn);

            }

        }

        return listToReturn;
    }

    private void setCategoryOthersToMovement(SantanderPaymentMovementDTO santanderPaymentMovementDTO,
                                             List<AssociatedSantanderPaymentDTO> listToReturn) {

        Optional<AssociatedSantanderPaymentDTO> associatedSantanderPaymentToProcess;

        //INCOME or Expense
        CategoryType categoryTypeToAssign = null;

        if (santanderPaymentMovementDTO.amount().compareTo(BigDecimal.ZERO) > 0) {
            categoryTypeToAssign =  CategoryType.INCOME;
            associatedSantanderPaymentToProcess = listToReturn.stream()
                    .filter(dto -> OTHERS_CATEGORY_INCOMES.equals(dto.getCategory().getCategoryName())
                            && CategoryType.INCOME.toString().equals(dto.getCategory().getCategoryType()))
                    .findFirst();
        }else {
            categoryTypeToAssign =  CategoryType.EXPENSE;
            associatedSantanderPaymentToProcess = listToReturn.stream()
                    .filter(dto -> OTHERS_CATEGORY_EXPENSES.equals(dto.getCategory().getCategoryName())
                            && CategoryType.EXPENSE.toString().equals(dto.getCategory().getCategoryType()))
                    .findFirst();
        }

        if(associatedSantanderPaymentToProcess.isPresent()) {

            associatedSantanderPaymentToProcess.get().getMovements().add(santanderPaymentMovementDTO);

        }else {
            final String categoryNameToSave = categoryTypeToAssign.toString().equals(CategoryType.EXPENSE.toString())
                    ? OTHERS_CATEGORY_EXPENSES : OTHERS_CATEGORY_INCOMES;

            //OTHERS category not found
            //We have to create it
            CategoryDTO saved = categoryService.save(new CategoryDTO(
                    null,
                    categoryNameToSave,
                    categoryTypeToAssign.toString()
            ));
            if(saved == null) {
                throw new RuntimeException("Category OTHERS could not be saved");
            }

            CategoryDTO categoryDTO = categoryService.findById(saved.getId());
            listToReturn.add(
                    new AssociatedSantanderPaymentDTO(
                            new ArrayList<SantanderPaymentMovementDTO>(),
                            categoryDTO
                    )
            );

            final CategoryType categoryTypeToAssignFinal =  categoryTypeToAssign;

            Optional<AssociatedSantanderPaymentDTO> associatedSantanderPaymentOthers = listToReturn.stream()
                    .filter(dto -> categoryNameToSave.equals(dto.getCategory().getCategoryName())
                            && categoryTypeToAssignFinal.toString().equals(dto.getCategory().getCategoryType()))
                    .findFirst();

            if(associatedSantanderPaymentOthers.isPresent()) {
                associatedSantanderPaymentOthers.get().getMovements().add(santanderPaymentMovementDTO);
            }else {
                throw new RuntimeException("Category OTHERS could not be saved and found");
            }
        }
    }



    private void setCategoryToMovement(MovementAssociationDTO movementAssociationDTO,
                                       SantanderPaymentMovementDTO santanderPaymentMovementDTO,
                                       List<AssociatedSantanderPaymentDTO> listToReturn) {

        for (AssociatedSantanderPaymentDTO  associatedSantanderPaymentDTO : listToReturn) {
            if(Objects.equals(
                    associatedSantanderPaymentDTO.getCategory().getId(),
                    movementAssociationDTO.getCategoryId())
            ) {
                associatedSantanderPaymentDTO.getMovements().add(santanderPaymentMovementDTO);
            }
        }

    }

    private void initEmptyListToReturn(List<AssociatedSantanderPaymentDTO> listToReturn) {

        List<CategoryDTO> categoriesAll = categoryService.findAll();
        for(CategoryDTO categoryDTO : categoriesAll) {

            listToReturn.add(new AssociatedSantanderPaymentDTO(
                    new ArrayList<SantanderPaymentMovementDTO>(),
                    categoryDTO
            ));
        }

    }

    private MovementAssociationDTO checkIfCategoryAssociationFound(SantanderPaymentMovementDTO santanderPaymentMovementDTO,
                                                 List<MovementAssociationDTO> categoryAssociationAll) {

        for (MovementAssociationDTO movementAssociationDTO : categoryAssociationAll) {
            String firstElement = santanderPaymentMovementDTO.operationLabel().toUpperCase();
            String secondElement = movementAssociationDTO.getAssociationWord().toUpperCase();

            if(firstElement.contains(secondElement)){
                return movementAssociationDTO;
            }
        }
        return null;

    }


}
