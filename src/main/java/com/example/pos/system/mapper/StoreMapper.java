package com.example.pos.system.mapper;


import com.example.pos.system.modal.Store;
import com.example.pos.system.payload.dto.StoreDTO;



public class StoreMapper {



    public static StoreDTO toDTO(Store store){


        StoreDTO dto=new StoreDTO();


        dto.setId(store.getId());

        dto.setBrand(store.getBrand());

        dto.setDescription(store.getDescription());

        dto.setStoreType(store.getStoreType());

        dto.setStatus(store.getStatus());

        dto.setContact(store.getContact());


        return dto;

    }



    public static Store toEntity(StoreDTO dto){


        Store store=new Store();


        store.setBrand(dto.getBrand());

        store.setDescription(dto.getDescription());

        store.setStoreType(dto.getStoreType());

        store.setContact(dto.getContact());


        return store;


    }


}