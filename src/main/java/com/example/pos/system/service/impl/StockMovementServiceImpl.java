package com.example.pos.system.service.impl;


import com.example.pos.system.modal.*;
import com.example.pos.system.payload.dto.StockMovementDTO;
import com.example.pos.system.repository.*;
import com.example.pos.system.service.StockMovementService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;


@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {


    private final StockMovementRepository movementRepository;

    private final ProductRepository productRepository;

    private final BranchRepository branchRepository;

    private final InventoryRepository inventoryRepository;




    @Override
    public StockMovementDTO createMovement(
            StockMovementDTO dto
    ) throws Exception {


        Product product =
                productRepository.findById(
                                dto.getProductId()
                        )
                        .orElseThrow(
                                ()->new Exception(
                                        "Product not found"
                                )
                        );



        Branch branch =
                branchRepository.findById(
                                dto.getBranchId()
                        )
                        .orElseThrow(
                                ()->new Exception(
                                        "Branch not found"
                                )
                        );




        Inventory inventory =
                inventoryRepository
                        .findByProductIdAndBranchId(
                                product.getId(),
                                branch.getId()
                        );



        if(inventory==null){

            throw new Exception(
                    "Inventory not created"
            );

        }



        // STOCK IN

        if(dto.getType().name()
                .equals("PURCHASE")){


            inventory.setQuantity(
                    inventory.getQuantity()
                            +
                            dto.getQuantity()
            );


        }



        // STOCK OUT

        if(dto.getType().name()
                .equals("SALE")){


            if(inventory.getQuantity()
                    <
                    dto.getQuantity()){


                throw new Exception(
                        "Insufficient stock"
                );

            }


            inventory.setQuantity(
                    inventory.getQuantity()
                            -
                            dto.getQuantity()
            );

        }



        inventoryRepository.save(inventory);



        StockMovement movement =
                StockMovement.builder()
                        .product(product)
                        .branch(branch)
                        .quantity(dto.getQuantity())
                        .type(dto.getType())
                        .description(dto.getDescription())
                        .build();



        StockMovement saved =
                movementRepository.save(
                        movement
                );



        dto.setId(saved.getId());


        return dto;

    }



    @Override
    public List<StockMovementDTO> getByBranch(
            Long branchId
    ){

        return movementRepository
                .findByBranchId(branchId)
                .stream()
                .map(m -> {

                    StockMovementDTO dto =
                            new StockMovementDTO();

                    dto.setId(m.getId());
                    dto.setProductId(
                            m.getProduct().getId()
                    );
                    dto.setBranchId(
                            m.getBranch().getId()
                    );
                    dto.setQuantity(
                            m.getQuantity()
                    );
                    dto.setType(
                            m.getType()
                    );
                    dto.setDescription(
                            m.getDescription()
                    );

                    return dto;

                })
                .toList();

    }

    @Override
    public List<StockMovementDTO> getByProduct(Long productId) {


        return movementRepository
                .findByProductId(productId)
                .stream()
                .map(m -> {

                    StockMovementDTO dto =
                            new StockMovementDTO();

                    dto.setId(m.getId());

                    dto.setProductId(
                            m.getProduct().getId()
                    );

                    dto.setBranchId(
                            m.getBranch().getId()
                    );

                    dto.setQuantity(
                            m.getQuantity()
                    );

                    dto.setType(
                            m.getType()
                    );

                    dto.setDescription(
                            m.getDescription()
                    );

                    return dto;

                })
                .toList();

    }

}