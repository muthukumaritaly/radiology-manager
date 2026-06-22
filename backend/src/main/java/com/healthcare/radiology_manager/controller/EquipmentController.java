package com.healthcare.radiology_manager.controller;

import com.healthcare.radiology_manager.dto.EquipmentCreateRequest;
import com.healthcare.radiology_manager.dto.EquipmentResponse;
import com.healthcare.radiology_manager.dto.ErrorResponse;
import com.healthcare.radiology_manager.dto.ValidationErrorResponse;
import com.healthcare.radiology_manager.docs.OpenApiDocConstants;
import com.healthcare.radiology_manager.service.EquipmentService;
import com.healthcare.radiology_manager.aspect.RequireRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipment")
@Tag(name = "Equipment", description = "Endpoints for registering and managing radiological equipment devices")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping
    @RequireRole("ADMIN")
    @Operation(
        summary = "Register new radiological equipment",
        description = "Registers a new radiological device (such as a CT, MRI, X-ray, or Mammogram) under a specific organization and optionally within a sub-location container. " +
                      "This operation is restricted to users with the **ADMIN** role. Specify this role via the `X-User-Role` header.",
        security = @SecurityRequirement(name = "X-User-Role"),
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON payload representing the equipment details to create",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = EquipmentCreateRequest.class),
                examples = @ExampleObject(name = "Create Equipment Request Example", value = OpenApiDocConstants.EQUIPMENT_CREATE_REQUEST)
            )
        ),
        responses = {
            @ApiResponse(
                responseCode = "201", 
                description = "Equipment registered successfully", 
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EquipmentResponse.class),
                    examples = @ExampleObject(name = "Success Response Example", value = OpenApiDocConstants.EQUIPMENT_RESPONSE)
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "Invalid request payload or schema validation failed", 
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ValidationErrorResponse.class),
                    examples = @ExampleObject(name = "Validation Error Example", value = OpenApiDocConstants.VALIDATION_ERROR_RESPONSE)
                )
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "Access Denied: Only ADMIN role is authorized", 
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Access Denied Example", value = OpenApiDocConstants.ERROR_RESPONSE_FORBIDDEN)
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Associated organization or container not found", 
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Not Found Example", value = OpenApiDocConstants.ERROR_RESPONSE_NOT_FOUND)
                )
            ),
            @ApiResponse(
                responseCode = "409", 
                description = "Conflict: Equipment with this serial number already exists", 
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Conflict Example", value = OpenApiDocConstants.ERROR_RESPONSE_FORBIDDEN)
                )
            )
        }
    )
    public ResponseEntity<EquipmentResponse> createEquipment(
            @Valid @RequestBody EquipmentCreateRequest request) {

        EquipmentResponse response = equipmentService.createEquipment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
