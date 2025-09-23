
package com.pipTracker.Service;

import com.pipTracker.Entity.Pip;
import java.util.List;

public interface PipService {
    Pip createPip(Pip pip);
    Pip getPipById(Long id);
    List<Pip> getAllPips();
    Pip updatePip(Long id, Pip pipDetails);
    void deletePip(Long id);
}



