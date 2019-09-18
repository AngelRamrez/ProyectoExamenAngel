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
public class MeseroJpaController implements Serializable {

    public MeseroJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mesero mesero) throws RollbackFailureException, Exception {
        if (mesero.getFacturaList() == null) {
            mesero.setFacturaList(new ArrayList<Factura>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Factura> attachedFacturaList = new ArrayList<Factura>();
            for (Factura facturaListFacturaToAttach : mesero.getFacturaList()) {
                facturaListFacturaToAttach = em.getReference(facturaListFacturaToAttach.getClass(), facturaListFacturaToAttach.getIdfactura());
                attachedFacturaList.add(facturaListFacturaToAttach);
            }
            mesero.setFacturaList(attachedFacturaList);
            em.persist(mesero);
            for (Factura facturaListFactura : mesero.getFacturaList()) {
                Mesero oldIdmeseroOfFacturaListFactura = facturaListFactura.getIdmesero();
                facturaListFactura.setIdmesero(mesero);
                facturaListFactura = em.merge(facturaListFactura);
                if (oldIdmeseroOfFacturaListFactura != null) {
                    oldIdmeseroOfFacturaListFactura.getFacturaList().remove(facturaListFactura);
                    oldIdmeseroOfFacturaListFactura = em.merge(oldIdmeseroOfFacturaListFactura);
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

    public void edit(Mesero mesero) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Mesero persistentMesero = em.find(Mesero.class, mesero.getIdmesero());
            List<Factura> facturaListOld = persistentMesero.getFacturaList();
            List<Factura> facturaListNew = mesero.getFacturaList();
            List<String> illegalOrphanMessages = null;
            for (Factura facturaListOldFactura : facturaListOld) {
                if (!facturaListNew.contains(facturaListOldFactura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Factura " + facturaListOldFactura + " since its idmesero field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Factura> attachedFacturaListNew = new ArrayList<Factura>();
            for (Factura facturaListNewFacturaToAttach : facturaListNew) {
                facturaListNewFacturaToAttach = em.getReference(facturaListNewFacturaToAttach.getClass(), facturaListNewFacturaToAttach.getIdfactura());
                attachedFacturaListNew.add(facturaListNewFacturaToAttach);
            }
            facturaListNew = attachedFacturaListNew;
            mesero.setFacturaList(facturaListNew);
            mesero = em.merge(mesero);
            for (Factura facturaListNewFactura : facturaListNew) {
                if (!facturaListOld.contains(facturaListNewFactura)) {
                    Mesero oldIdmeseroOfFacturaListNewFactura = facturaListNewFactura.getIdmesero();
                    facturaListNewFactura.setIdmesero(mesero);
                    facturaListNewFactura = em.merge(facturaListNewFactura);
                    if (oldIdmeseroOfFacturaListNewFactura != null && !oldIdmeseroOfFacturaListNewFactura.equals(mesero)) {
                        oldIdmeseroOfFacturaListNewFactura.getFacturaList().remove(facturaListNewFactura);
                        oldIdmeseroOfFacturaListNewFactura = em.merge(oldIdmeseroOfFacturaListNewFactura);
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
                Integer id = mesero.getIdmesero();
                if (findMesero(id) == null) {
                    throw new NonexistentEntityException("The mesero with id " + id + " no longer exists.");
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
            Mesero mesero;
            try {
                mesero = em.getReference(Mesero.class, id);
                mesero.getIdmesero();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mesero with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Factura> facturaListOrphanCheck = mesero.getFacturaList();
            for (Factura facturaListOrphanCheckFactura : facturaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Mesero (" + mesero + ") cannot be destroyed since the Factura " + facturaListOrphanCheckFactura + " in its facturaList field has a non-nullable idmesero field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(mesero);
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

    public List<Mesero> findMeseroEntities() {
        return findMeseroEntities(true, -1, -1);
    }

    public List<Mesero> findMeseroEntities(int maxResults, int firstResult) {
        return findMeseroEntities(false, maxResults, firstResult);
    }

    private List<Mesero> findMeseroEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mesero.class));
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

    public Mesero findMesero(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mesero.class, id);
        } finally {
            em.close();
        }
    }

    public int getMeseroCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mesero> rt = cq.from(Mesero.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
