package com.errabi.productservice.services;

import com.errabi.productservice.entities.Product;
import com.errabi.productservice.exceptions.EntityNotFoundException;
import com.errabi.productservice.repositories.SimpleProductRepository;
import com.errabi.productservice.web.dtos.ProductDTO;
import com.errabi.productservice.web.dtos.ResponseInfo;
import com.errabi.productservice.web.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.errabi.productservice.utils.ProductConstant.SYSTEM_ERROR_DESCRIPTION;
import static com.errabi.productservice.web.mapper.ProductMapper.buildSuccessResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final SimpleProductRepository productRepository;
    private final ProductMapper productMapper;

    public ResponseInfo saveProduct(ProductDTO productDto) {
        log.info("Saving product {}", productDto);
        Product productToSave = productMapper.toEntity(productDto);
        productRepository.save(productToSave);
       return buildSuccessResponse(productDto);
    }

    public ResponseInfo updateProduct(ProductDTO productDto) {
        log.info("Updating product {}", productDto);
        Product productToUpdate = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(SYSTEM_ERROR_DESCRIPTION));
        productMapper.updateFromDto(productDto, productToUpdate);
        productRepository.update(productToUpdate);
        return buildSuccessResponse();
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product {}", id);
        Product productToDelete = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SYSTEM_ERROR_DESCRIPTION));
        productRepository.deleteById(productToDelete.getId());
        buildSuccessResponse();
    }

    public ProductDTO getProductById(Long id) {
        log.info("Fetching product {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(SYSTEM_ERROR_DESCRIPTION));
        return productMapper.toDto(product);
    }

    public ResponseInfo getAllProducts(Pageable pageable) {
        log.info("Fetching all products");
        Page<Product> productPage = productRepository.findAll(pageable);
        if(!productPage.isEmpty()){
            return buildSuccessResponse(productPage.map(productMapper::toDto));
        }else{
            return buildSuccessResponse(Page.empty());
        }
    }
}
