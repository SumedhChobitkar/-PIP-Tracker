package com.pipTracker.Controller;

import com.pipTracker.Entity.Pip;
import com.pipTracker.Exception.PipNotFoundException;
import com.pipTracker.Service.PipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/pip") //Actual path for all requests
public class PipController {

    @Autowired
    private PipService pipService;

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

    //Get One PIP - GET /api/pip/getById/{id}
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