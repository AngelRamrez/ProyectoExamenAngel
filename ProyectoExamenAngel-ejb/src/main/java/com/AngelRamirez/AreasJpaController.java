/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AngelRamirez;

import com.AngelRamirez.exceptions.IllegalOrphanException;
import com.AngelRamirez.exceptions.NonexistentEntityException;
import com.AngelRamirez.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author programacion
 */
public class AreasJpaController implements Serializable {

    public AreasJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Areas areas) throws RollbackFailureException, Exception {
        if (areas.getMesaList() == null) {
            areas.setMesaList(new ArrayList<Mesa>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Mesa> attachedMesaList = new ArrayList<Mesa>();
            for (Mesa mesaListMesaToAttach : areas.getMesaList()) {
                mesaListMesaToAttach = em.getReference(mesaListMesaToAttach.getClass(), mesaListMesaToAttach.getIdmesa());
                attachedMesaList.add(mesaListMesaToAttach);
            }
            areas.setMesaList(attachedMesaList);
            em.persist(areas);
            for (Mesa mesaListMesa : areas.getMesaList()) {
                Areas oldIdareasOfMesaListMesa = mesaListMesa.getIdareas();
                mesaListMesa.setIdareas(areas);
                mesaListMesa = em.merge(mesaListMesa);
                if (oldIdareasOfMesaListMesa != null) {
                    oldIdareasOfMesaListMesa.getMesaList().remove(mesaListMesa);
                    oldIdareasOfMesaListMesa = em.merge(oldIdareasOfMesaListMesa);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Areas areas) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Areas persistentAreas = em.find(Areas.class, areas.getIdareas());
            List<Mesa> mesaListOld = persistentAreas.getMesaList();
            List<Mesa> mesaListNew = areas.getMesaList();
            List<String> illegalOrphanMessages = null;
            for (Mesa mesaListOldMesa : mesaListOld) {
                if (!mesaListNew.contains(mesaListOldMesa)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Mesa " + mesaListOldMesa + " since its idareas field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Mesa> attachedMesaListNew = new ArrayList<Mesa>();
            for (Mesa mesaListNewMesaToAttach : mesaListNew) {
                mesaListNewMesaToAttach = em.getReference(mesaListNewMesaToAttach.getClass(), mesaListNewMesaToAttach.getIdmesa());
                attachedMesaListNew.add(mesaListNewMesaToAttach);
            }
            mesaListNew = attachedMesaListNew;
            areas.setMesaList(mesaListNew);
            areas = em.merge(areas);
            for (Mesa mesaListNewMesa : mesaListNew) {
                if (!mesaListOld.contains(mesaListNewMesa)) {
                    Areas oldIdareasOfMesaListNewMesa = mesaListNewMesa.getIdareas();
                    mesaListNewMesa.setIdareas(areas);
                    mesaListNewMesa = em.merge(mesaListNewMesa);
                    if (oldIdareasOfMesaListNewMesa != null && !oldIdareasOfMesaListNewMesa.equals(areas)) {
                        oldIdareasOfMesaListNewMesa.getMesaList().remove(mesaListNewMesa);
                        oldIdareasOfMesaListNewMesa = em.merge(oldIdareasOfMesaListNewMesa);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = areas.getIdareas();
                if (findAreas(id) == null) {
                    throw new NonexistentEntityException("The areas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Areas areas;
            try {
                areas = em.getReference(Areas.class, id);
                areas.getIdareas();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The areas with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Mesa> mesaListOrphanCheck = areas.getMesaList();
            for (Mesa mesaListOrphanCheckMesa : mesaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Areas (" + areas + ") cannot be destroyed since the Mesa " + mesaListOrphanCheckMesa + " in its mesaList field has a non-nullable idareas field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(areas);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Areas> findAreasEntities() {
        return findAreasEntities(true, -1, -1);
    }

    public List<Areas> findAreasEntities(int maxResults, int firstResult) {
        return findAreasEntities(false, maxResults, firstResult);
    }

    private List<Areas> findAreasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Areas.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Areas findAreas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Areas.class, id);
        } finally {
            em.close();
        }
    }

    public int getAreasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Areas> rt = cq.from(Areas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
