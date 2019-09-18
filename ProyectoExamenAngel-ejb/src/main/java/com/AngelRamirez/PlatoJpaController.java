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
public class PlatoJpaController implements Serializable {

    public PlatoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Plato plato) throws RollbackFailureException, Exception {
        if (plato.getDetallefacturaList() == null) {
            plato.setDetallefacturaList(new ArrayList<Detallefactura>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Detallefactura> attachedDetallefacturaList = new ArrayList<Detallefactura>();
            for (Detallefactura detallefacturaListDetallefacturaToAttach : plato.getDetallefacturaList()) {
                detallefacturaListDetallefacturaToAttach = em.getReference(detallefacturaListDetallefacturaToAttach.getClass(), detallefacturaListDetallefacturaToAttach.getIddetallefactura());
                attachedDetallefacturaList.add(detallefacturaListDetallefacturaToAttach);
            }
            plato.setDetallefacturaList(attachedDetallefacturaList);
            em.persist(plato);
            for (Detallefactura detallefacturaListDetallefactura : plato.getDetallefacturaList()) {
                Plato oldIdplatoOfDetallefacturaListDetallefactura = detallefacturaListDetallefactura.getIdplato();
                detallefacturaListDetallefactura.setIdplato(plato);
                detallefacturaListDetallefactura = em.merge(detallefacturaListDetallefactura);
                if (oldIdplatoOfDetallefacturaListDetallefactura != null) {
                    oldIdplatoOfDetallefacturaListDetallefactura.getDetallefacturaList().remove(detallefacturaListDetallefactura);
                    oldIdplatoOfDetallefacturaListDetallefactura = em.merge(oldIdplatoOfDetallefacturaListDetallefactura);
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

    public void edit(Plato plato) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Plato persistentPlato = em.find(Plato.class, plato.getIdplato());
            List<Detallefactura> detallefacturaListOld = persistentPlato.getDetallefacturaList();
            List<Detallefactura> detallefacturaListNew = plato.getDetallefacturaList();
            List<String> illegalOrphanMessages = null;
            for (Detallefactura detallefacturaListOldDetallefactura : detallefacturaListOld) {
                if (!detallefacturaListNew.contains(detallefacturaListOldDetallefactura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detallefactura " + detallefacturaListOldDetallefactura + " since its idplato field is not nullable.");
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
            plato.setDetallefacturaList(detallefacturaListNew);
            plato = em.merge(plato);
            for (Detallefactura detallefacturaListNewDetallefactura : detallefacturaListNew) {
                if (!detallefacturaListOld.contains(detallefacturaListNewDetallefactura)) {
                    Plato oldIdplatoOfDetallefacturaListNewDetallefactura = detallefacturaListNewDetallefactura.getIdplato();
                    detallefacturaListNewDetallefactura.setIdplato(plato);
                    detallefacturaListNewDetallefactura = em.merge(detallefacturaListNewDetallefactura);
                    if (oldIdplatoOfDetallefacturaListNewDetallefactura != null && !oldIdplatoOfDetallefacturaListNewDetallefactura.equals(plato)) {
                        oldIdplatoOfDetallefacturaListNewDetallefactura.getDetallefacturaList().remove(detallefacturaListNewDetallefactura);
                        oldIdplatoOfDetallefacturaListNewDetallefactura = em.merge(oldIdplatoOfDetallefacturaListNewDetallefactura);
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
                Integer id = plato.getIdplato();
                if (findPlato(id) == null) {
                    throw new NonexistentEntityException("The plato with id " + id + " no longer exists.");
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
            Plato plato;
            try {
                plato = em.getReference(Plato.class, id);
                plato.getIdplato();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The plato with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Detallefactura> detallefacturaListOrphanCheck = plato.getDetallefacturaList();
            for (Detallefactura detallefacturaListOrphanCheckDetallefactura : detallefacturaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Plato (" + plato + ") cannot be destroyed since the Detallefactura " + detallefacturaListOrphanCheckDetallefactura + " in its detallefacturaList field has a non-nullable idplato field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(plato);
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

    public List<Plato> findPlatoEntities() {
        return findPlatoEntities(true, -1, -1);
    }

    public List<Plato> findPlatoEntities(int maxResults, int firstResult) {
        return findPlatoEntities(false, maxResults, firstResult);
    }

    private List<Plato> findPlatoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Plato.class));
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

    public Plato findPlato(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Plato.class, id);
        } finally {
            em.close();
        }
    }

    public int getPlatoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Plato> rt = cq.from(Plato.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
