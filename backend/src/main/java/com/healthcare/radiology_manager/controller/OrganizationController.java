package com.healthcare.radiology_manager.controller;

import com.healthcare.radiology_manager.dto.ErrorResponse;
import com.healthcare.radiology_manager.dto.OrganizationResponse;
import com.healthcare.radiology_manager.dto.OrganizationTreeResponse;
import com.healthcare.radiology_manager.docs.OpenApiDocConstants;
import com.healthcare.radiology_manager.service.OrganizationService;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organizations")
@Tag(name = "Organizations", description = "Endpoints for retrieving organization location and equipment hierarchies")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/{id}/tree")
    @Operation(
        summary = "Retrieve organization hierarchy tree",
        description = "Fetches the full hierarchical tree structure of an organization by ID. The response contains direct equipment, " +
                      "and recursive nested containers (like departments, buildings, and rooms) containing their respective equipment.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Hierarchy tree retrieved successfully", 
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = OrganizationTreeResponse.class),
                    examples = @ExampleObject(name = "Success Response Example", value = OpenApiDocConstants.ORGANIZATION_TREE_RESPONSE)
                )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Organization not found", 
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Not Found Response Example", value = OpenApiDocConstants.ERROR_RESPONSE_NOT_FOUND)
                )
            ),
            @ApiResponse(
                responseCode = "500", 
                description = "Internal server error", 
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(name = "Internal Error Response Example", value = OpenApiDocConstants.ERROR_RESPONSE_FORBIDDEN)
                )
            )
        }
    )
    public ResponseEntity<OrganizationTreeResponse> getOrganizationTree(
            @PathVariable 
            @Parameter(description = "Unique database ID of the organization", required = true, example = "1") 
            Long id) {
        OrganizationTreeResponse response = organizationService.getOrganizationTree(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "List all organizations",
        description = "Fetches a list of all registered organizations.",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Organizations retrieved successfully", 
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = OrganizationResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "500", 
                description = "Internal server error", 
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = ErrorResponse.class)
                )
            )
        }
    )
    public ResponseEntity<List<OrganizationResponse>> getAllOrganizations() {
        List<OrganizationResponse> responses = organizationService.getAllOrganizations();
        return ResponseEntity.ok(responses);
    }
}
