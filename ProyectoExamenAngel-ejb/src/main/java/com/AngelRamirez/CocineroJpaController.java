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
public class CocineroJpaController implements Serializable {

    public CocineroJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cocinero cocinero) throws RollbackFailureException, Exception {
        if (cocinero.getDetallefacturaList() == null) {
            cocinero.setDetallefacturaList(new ArrayList<Detallefactura>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Detallefactura> attachedDetallefacturaList = new ArrayList<Detallefactura>();
            for (Detallefactura detallefacturaListDetallefacturaToAttach : cocinero.getDetallefacturaList()) {
                detallefacturaListDetallefacturaToAttach = em.getReference(detallefacturaListDetallefacturaToAttach.getClass(), detallefacturaListDetallefacturaToAttach.getIddetallefactura());
                attachedDetallefacturaList.add(detallefacturaListDetallefacturaToAttach);
            }
            cocinero.setDetallefacturaList(attachedDetallefacturaList);
            em.persist(cocinero);
            for (Detallefactura detallefacturaListDetallefactura : cocinero.getDetallefacturaList()) {
                Cocinero oldIdcocineroOfDetallefacturaListDetallefactura = detallefacturaListDetallefactura.getIdcocinero();
                detallefacturaListDetallefactura.setIdcocinero(cocinero);
                detallefacturaListDetallefactura = em.merge(detallefacturaListDetallefactura);
                if (oldIdcocineroOfDetallefacturaListDetallefactura != null) {
                    oldIdcocineroOfDetallefacturaListDetallefactura.getDetallefacturaList().remove(detallefacturaListDetallefactura);
                    oldIdcocineroOfDetallefacturaListDetallefactura = em.merge(oldIdcocineroOfDetallefacturaListDetallefactura);
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

    public void edit(Cocinero cocinero) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Cocinero persistentCocinero = em.find(Cocinero.class, cocinero.getIdcocinero());
            List<Detallefactura> detallefacturaListOld = persistentCocinero.getDetallefacturaList();
            List<Detallefactura> detallefacturaListNew = cocinero.getDetallefacturaList();
            List<String> illegalOrphanMessages = null;
            for (Detallefactura detallefacturaListOldDetallefactura : detallefacturaListOld) {
                if (!detallefacturaListNew.contains(detallefacturaListOldDetallefactura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detallefactura " + detallefacturaListOldDetallefactura + " since its idcocinero field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Detallefactura> attachedDetallefacturaListNew = new ArrayList<Detallefactura>();
            for (Detallefactura detallefacturaListNewDetallefacturaToAttach : detallefacturaListNew) {
                detallefacturaListNewDetallefacturaToAttach = em.getReference(detallefacturaListNewDetallefacturaToAttach.getClass(), detallefacturaListNewDetallefacturaToAttach.getIddetallefactura());
                attachedDetallefacturaListNew.add(detallefacturaListNewDetallefacturaToAttach);
            }
            detallefacturaListNew = attachedDetallefacturaListNew;
            cocinero.setDetallefacturaList(detallefacturaListNew);
            cocinero = em.merge(cocinero);
            for (Detallefactura detallefacturaListNewDetallefactura : detallefacturaListNew) {
                if (!detallefacturaListOld.contains(detallefacturaListNewDetallefactura)) {
                    Cocinero oldIdcocineroOfDetallefacturaListNewDetallefactura = detallefacturaListNewDetallefactura.getIdcocinero();
                    detallefacturaListNewDetallefactura.setIdcocinero(cocinero);
                    detallefacturaListNewDetallefactura = em.merge(detallefacturaListNewDetallefactura);
                    if (oldIdcocineroOfDetallefacturaListNewDetallefactura != null && !oldIdcocineroOfDetallefacturaListNewDetallefactura.equals(cocinero)) {
                        oldIdcocineroOfDetallefacturaListNewDetallefactura.getDetallefacturaList().remove(detallefacturaListNewDetallefactura);
                        oldIdcocineroOfDetallefacturaListNewDetallefactura = em.merge(oldIdcocineroOfDetallefacturaListNewDetallefactura);
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
                Integer id = cocinero.getIdcocinero();
                if (findCocinero(id) == null) {
                    throw new NonexistentEntityException("The cocinero with id " + id + " no longer exists.");
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
            Cocinero cocinero;
            try {
                cocinero = em.getReference(Cocinero.class, id);
                cocinero.getIdcocinero();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cocinero with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Detallefactura> detallefacturaListOrphanCheck = cocinero.getDetallefacturaList();
            for (Detallefactura detallefacturaListOrphanCheckDetallefactura : detallefacturaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cocinero (" + cocinero + ") cannot be destroyed since the Detallefactura " + detallefacturaListOrphanCheckDetallefactura + " in its detallefacturaList field has a non-nullable idcocinero field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(cocinero);
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

    public List<Cocinero> findCocineroEntities() {
        return findCocineroEntities(true, -1, -1);
    }

    public List<Cocinero> findCocineroEntities(int maxResults, int firstResult) {
        return findCocineroEntities(false, maxResults, firstResult);
    }

    private List<Cocinero> findCocineroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cocinero.class));
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

    public Cocinero findCocinero(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cocinero.class, id);
        } finally {
            em.close();
        }
    }

    public int getCocineroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cocinero> rt = cq.from(Cocinero.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
