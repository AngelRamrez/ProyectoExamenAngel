/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AngelRamirez.sb;

import com.AngelRamirez.Mesero;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author programacion
 */
@Local
public interface MeseroFacadeLocal {

    void create(Mesero mesero);

    void edit(Mesero mesero);

    void remove(Mesero mesero);

    Mesero find(Object id);

    List<Mesero> findAll();

    List<Mesero> findRange(int[] range);

    int count();
    
}
