package com.healthcare.radiology_manager.docs;

public final class OpenApiDocConstants {

    public static final String EQUIPMENT_CREATE_REQUEST = """
        {
          "name": "GE Revolution CT Scan v2",
          "type": "CT",
          "serialNumber": "SN-CT-9999",
          "installationDate": "2026-06-19",
          "organizationId": 1,
          "containerId": 3
        }
        """;

    public static final String EQUIPMENT_RESPONSE = """
        {
          "id": 101,
          "name": "GE Revolution CT Scan v2",
          "type": "CT",
          "serialNumber": "SN-CT-9999",
          "installationDate": "2026-06-19",
          "organizationId": 1,
          "containerId": 3
        }
        """;

    public static final String ORGANIZATION_TREE_RESPONSE = """
        {
          "id": 1,
          "name": "San Raffaele Hospital Group",
          "equipment": [
            {
              "id": 4,
              "name": "Hologic Selenia Mammography Van",
              "type": "Mammogram",
              "serialNumber": "SN-MG-404",
              "installationDate": "2022-11-05",
              "organizationId": 1,
              "containerId": null
            }
          ],
          "containers": [
            {
              "id": 1,
              "name": "Plant A",
              "containers": [
                {
                  "id": 2,
                  "name": "Building 2",
                  "containers": [
                    {
                      "id": 3,
                      "name": "Radiology Department",
                      "containers": [],
                      "equipment": [
                        {
                          "id": 1,
                          "name": "GE Revolution CT Scan",
                          "type": "CT",
                          "serialNumber": "SN-CT-101",
                          "installationDate": "2023-01-15",
                          "organizationId": 1,
                          "containerId": 3
                        }
                      ]
                    }
                  ],
                  "equipment": []
                }
              ],
              "equipment": []
            }
          ]
        }
        """;

    public static final String ERROR_RESPONSE_FORBIDDEN = """
        {
          "timestamp": "2026-06-22T09:51:13Z",
          "status": 403,
          "error": "Forbidden",
          "message": "Access Denied: Only ADMIN role can perform write operations. Please provide HTTP header 'X-User-Role: ADMIN'.",
          "correlationId": "d9b23b32-8dfb-4027-a068-dcd6701b22e1"
        }
        """;

    public static final String ERROR_RESPONSE_NOT_FOUND = """
        {
          "timestamp": "2026-06-22T09:51:13Z",
          "status": 404,
          "error": "Not Found",
          "message": "Organization not found with ID: 99",
          "correlationId": "f7d3c8c2-4ebf-4f27-a0d8-dcd7701b22f2"
        }
        """;

    public static final String VALIDATION_ERROR_RESPONSE = """
        {
          "timestamp": "2026-06-22T09:51:13Z",
          "status": 400,
          "error": "Validation Failed",
          "validationErrors": {
            "name": "Equipment name must not be blank",
            "serialNumber": "Serial number must not be blank"
          }
        }
        """;

    private OpenApiDocConstants() {}
}
