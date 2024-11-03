package com.kindsonthegenius.fleetmsv2.api;

import com.itextpdf.text.DocumentException;
import com.kindsonthegenius.fleetmsv2.security.models.*;
import com.kindsonthegenius.fleetmsv2.security.services.UserService;
import com.kindsonthegenius.fleetmsv2.services.ExportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Tag(name = "User Management", description = "APIs for managing users")
@RestController
@RequestMapping("/api/users")
public class UserRestController {
    private static final String X_CONTENT_TYPE_OPTIONS = "X-Content-Type-Options";
    
    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
     
    @Autowired
    private UserService userService;
    
    @Autowired
    private ExportService exportService;
     
    @ValidateRequest
    @Operation(summary = "Get all users")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Fetching all users");
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable @Min(1) Integer id) {
        logger.info("Fetching user with id: {}", id);
        return ResponseEntity.ok(userService.findById(id));
    }

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody @Valid User user) {
        logger.info("Creating new user");
        return ResponseEntity.status(HttpStatus.CREATED)
                           .body(userService.save(user));
    }

    @Operation(summary = "Update existing user")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable @Min(1) Integer id, 
                                         @RequestBody @Valid User user) {
        logger.info("Updating user with id: {}", id);
        return ResponseEntity.ok(userService.update(id, user));
    }

    @Operation(summary = "Export users to Excel")
    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportToExcel() {
        try {
            logger.info("Exporting users to Excel");
            ByteArrayInputStream stream = exportService.usersToExcel(userService.findAll());
            
            HttpHeaders headers = getSecureHeaders("users.xlsx", "application/vnd.ms-excel");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(stream));
        } catch (IOException e) {
            logger.error("Error exporting users to Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Export users to PDF")
    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportToPdf() {
        try {
            logger.info("Exporting users to PDF");
            ByteArrayInputStream stream = exportService.usersToPdf(userService.findAll());
            
            HttpHeaders headers = getSecureHeaders("users.pdf", MediaType.APPLICATION_PDF_VALUE);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new InputStreamResource(stream));
        } catch (DocumentException e) {
            logger.error("Error exporting users to PDF", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private HttpHeaders getSecureHeaders(String filename, String contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add(HttpHeaders.CONTENT_TYPE, contentType);
        headers.add("X-Content-Type-Options", "nosniff");   
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        return headers;
    }
    

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleExceptions(Exception ex) {
        logger.error("Error processing request", ex);
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                           .body(error);
    }
}
