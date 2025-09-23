/*
package com.pipTracker.Controller;

import com.pipTracker.Entity.Pip;
import com.pipTracker.Exception.PipNotFoundException;
import com.pipTracker.Service.PipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="PIP APIs",description = "Operations related to Performance Improvement Plans")
@RestController
@CrossOrigin("*")
@RequestMapping("/api/pip") //Actual path for all requests
public class PipController {

    @Autowired
    private PipService pipService;


    @Operation(summary = "Create PIP",description= "Creates a new Performance Improvement Plan.\n\n" +
         "Eg: POST http://localhost:8080/api/pip/save")
    @ApiResponse(responseCode = "200",description = "PIP created successfully")
    //Create PIP - POST /api/pip
    @PostMapping("/save")
    public ResponseEntity<?> createPip(@RequestBody Pip pip) {
        try {
            Pip created = pipService.createPip(pip);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to create PIP: " + e.getMessage());
        }
    }


    @Operation(summary = "Get All PIPs",description = "Fetches all Performance Improvement plans.\n\n" +
          "Eg: GET http://localhost:8080/api/pip/get")
    @ApiResponse(responseCode = "200",description = "PIPs fetched successfully")
    //Get All PIPs - GET /api/pip
    @GetMapping("/get")
    public ResponseEntity<?> getAllPips() {
        try {
            List<Pip> pips = pipService.getAllPips();
            return ResponseEntity.ok(pips);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve PIPs");
        }
    }


    @Operation(summary = "Get PIP By ID",description = "Fetches a PIP by its ID.\n\n" +
            "Eg: GET http://localhost:8080/api/pip/getById/{id}")
    @ApiResponse(responseCode = "200",description = "PIP found")
    @ApiResponse(responseCode = "400",description = "PIP not found")
    //Get One PIP - GET/api/pip/getById/{id}
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getPipById(@PathVariable Long id) {
        try {
            Pip pip = pipService.getPipById(id);
            return ResponseEntity.ok(pip);
        } catch (PipNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving PIP");
        }
    }


    @Operation(summary = "Update PIP",description = "Updates an existing PIP by ID.\n\n" +
            "Eg: PUT http://localhost:8080/api/pip/update/{id}")
    @ApiResponse(responseCode = "200",description = "PIP updated successfully")
    @ApiResponse(responseCode = "400",description = "PIP not found")
    //Update PIP - PUT /api/pip/update/{id}
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePip(@PathVariable Long id, @RequestBody Pip pipDetails) {
        try {
            Pip updated = pipService.updatePip(id, pipDetails);
            return ResponseEntity.ok(updated);
        } catch (PipNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update PIP");
        }
    }


    @Operation(summary = "Delete PIP",description = "Deletes a PIP by its ID.\n\n" +
            "Eg: DELETE http://local:8080/api/pip/delete/{id}")
    @ApiResponse(responseCode = "200",description = "PIP deleted successfully")
    @ApiResponse(responseCode = "404",description = "PIP not found")
    //Delete PIP - DELETE /api/pip/delete{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePip(@PathVariable Long id) {
        try {
            pipService.deletePip(id);
            return ResponseEntity.ok("PIP deleted successfully");
        } catch (PipNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete PIP");
        }
    }
}

 */

package com.pipTracker.Controller;

import com.pipTracker.Entity.Pip;
import com.pipTracker.Exception.PipNotFoundException;
import com.pipTracker.Service.PipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="PIP APIs", description = "Operations related to Performance Improvement Plans")
@RestController
@CrossOrigin("*")
@RequestMapping("/api/pip")
public class PipController {

    @Autowired
    private PipService pipService;

    @Operation(summary = "Create PIP", description= "Creates a new Performance Improvement Plan.\n\nEg: " +
            "POST http://localhost:8080/api/pip/save")
    @ApiResponse(responseCode = "200", description = "PIP created successfully")
    @PostMapping("/save")
    @PreAuthorize("hasAnyRole('HR','MANAGER')")
    public ResponseEntity<?> createPip(@RequestBody Pip pip) {
        try {
            Pip created = pipService.createPip(pip);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to create PIP: " + e.getMessage());
        }
    }

    @Operation(summary = "Get All PIPs", description = "Fetches all Performance Improvement plans.\n\nEg: " +
            "GET http://localhost:8080/api/pip/get")
    @ApiResponse(responseCode = "200", description = "PIPs fetched successfully")
    @GetMapping("/get")
    @PreAuthorize("hasAnyRole('HR','MANAGER','ADMIN')")
    public ResponseEntity<?> getAllPips() {
        try {
            List<Pip> pips = pipService.getAllPips();
            return ResponseEntity.ok(pips);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve PIPs");
        }
    }

    @Operation(summary = "Get PIP By ID", description = "Fetches a PIP by its ID.\n\nEg: " +
            "GET http://localhost:8080/api/pip/getById/{id}")
    @ApiResponse(responseCode = "200", description = "PIP found")
    @ApiResponse(responseCode = "400", description = "PIP not found")
    @GetMapping("/getById/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getPipById(@PathVariable Long id) {
        try {
            Pip pip = pipService.getPipById(id);
            return ResponseEntity.ok(pip);
        } catch (PipNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error retrieving PIP");
        }
    }

    @Operation(summary = "Update PIP", description = "Updates an existing PIP by ID.\n\nEg:" +
            " PUT http://localhost:8080/api/pip/update/{id}")
    @ApiResponse(responseCode = "200", description = "PIP updated successfully")
    @ApiResponse(responseCode = "400", description = "PIP not found")
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('HR','MANAGER')")
    public ResponseEntity<?> updatePip(@PathVariable Long id, @RequestBody Pip pipDetails) {
        try {
            Pip updated = pipService.updatePip(id, pipDetails);
            return ResponseEntity.ok(updated);
        } catch (PipNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update PIP");
        }
    }

    @Operation(summary = "Delete PIP", description = "Deletes a PIP by its ID.\n\nEg: " +
            "DELETE http://localhost:8080/api/pip/delete/{id}")
    @ApiResponse(responseCode = "200", description = "PIP deleted successfully")
    @ApiResponse(responseCode = "404", description = "PIP not found")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('HR','MANAGER')")
    public ResponseEntity<?> deletePip(@PathVariable Long id) {
        try {
            pipService.deletePip(id);
            return ResponseEntity.ok("PIP deleted successfully");
        } catch (PipNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete PIP");
        }
    }
}



