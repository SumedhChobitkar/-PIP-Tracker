package com.pipTracker.ServiceImpl;

import com.pipTracker.Entity.Pip;
import com.pipTracker.Exception.PipNotFoundException;
import com.pipTracker.Repository.PipRepository;
import com.pipTracker.Service.PipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PipServiceImpl implements PipService {

    @Autowired
    private PipRepository pipRepository;

    @Override
    public Pip createPip(Pip pip) {
        try {
            return pipRepository.save(pip);
        } catch (Exception e) {
            System.err.println("Error while creating PIP: " + e.getMessage());
            throw new RuntimeException("Failed to create PIP.");
        }
    }

    @Override
    public Pip getPipById(Long id) {
        try {
            return pipRepository.findById(id)
                    .orElseThrow(() -> new PipNotFoundException("PIP not found with ID: " + id));
        } catch (PipNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching PIP: " + e.getMessage());
            throw new RuntimeException("Failed to fetch PIP.");
        }
    }

    @Override
    public List<Pip> getAllPips() {
        try {
            return pipRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error retrieving all PIPs: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve PIPs.");
        }
    }

    @Override
    public Pip updatePip(Long id, Pip pipDetails) {
        try {
            Pip pip = pipRepository.findById(id)
                    .orElseThrow(() -> new PipNotFoundException("PIP not found with ID: " + id));

            pip.setStartDate(pipDetails.getStartDate());
            pip.setEndDate(pipDetails.getEndDate());
            pip.setGoals(pipDetails.getGoals());
            pip.setProgress(pipDetails.getProgress());
            pip.setStatus(pipDetails.getStatus());
            pip.setReviewerId(pipDetails.getReviewerId());
            pip.setOutcome(pipDetails.getOutcome());
            pip.setComments(pipDetails.getComments());

            return pipRepository.save(pip);
        } catch (PipNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error updating PIP: " + e.getMessage());
            throw new RuntimeException("Failed to update PIP with ID: " + id);
        }
    }

    @Override
    public void deletePip(Long id) {
        try {
            Pip pip = pipRepository.findById(id)
                    .orElseThrow(() -> new PipNotFoundException("PIP not found with ID: " + id));
            pipRepository.delete(pip);
        } catch (PipNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("Error deleting PIP: " + e.getMessage());
            throw new RuntimeException("Failed to delete PIP with ID: " + id);
        }
    }
}