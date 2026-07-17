package com.example.pos.system.controller;

import com.example.pos.system.modal.User;
import com.example.pos.system.payload.dto.ProductDTO;
import com.example.pos.system.payload.response.ApiResponse;
import com.example.pos.system.service.ProductService;
import com.example.pos.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ProductDTO> create(
            @RequestBody ProductDTO productDTO,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {


        System.out.println("IMAGE FROM REQUEST : "
                + productDTO.getImage());


        User user = userService.getUserFromJwtToken(jwt);


        return ResponseEntity.ok(
                productService.createProduct(productDTO,user)
        );

    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ProductDTO>> getByStoreId(
            @PathVariable Long storeId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                productService.getProductsByStoreId(storeId, user)
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> update(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);
        return ResponseEntity.ok(
                productService.updateProduct(
                        id, productDTO,
                        user
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                productService.getProductById(id, user)
        );
    }

    @GetMapping("/store/{storeId}/search")
    public ResponseEntity<List<ProductDTO>> searchByKeyword(
            @PathVariable Long storeId,
            @RequestParam String keyword,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                productService.searchByKeyword(storeId, keyword, user)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(
            @PathVariable Long id,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        productService.deleteProduct(
                id,
                user
        );

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage("Product deleted successfully..");

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadProductImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String jwt
    ) throws IOException {


        System.out.println("========== IMAGE UPLOAD API HIT ==========");
        System.out.println("FILE NAME : " + file.getOriginalFilename());


        String uploadDir = "uploads/products/";

        File directory = new File(uploadDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }


        String fileName =
                UUID.randomUUID()
                        + "_"
                        + file.getOriginalFilename();


        Path filePath =
                Paths.get(uploadDir, fileName);


        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );


        String imagePath =
                "/uploads/products/" + fileName;


        System.out.println(
                "IMAGE SAVED : " + imagePath
        );


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(imagePath);
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {

        User user = userService.getUserFromJwtToken(jwt);

        return ResponseEntity.ok(
                productService.getAllProducts(user)
        );
    }
}
