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
public class MesaJpaController implements Serializable {

    public MesaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Mesa mesa) throws RollbackFailureException, Exception {
        if (mesa.getFacturaList() == null) {
            mesa.setFacturaList(new ArrayList<Factura>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Areas idareas = mesa.getIdareas();
            if (idareas != null) {
                idareas = em.getReference(idareas.getClass(), idareas.getIdareas());
                mesa.setIdareas(idareas);
            }
            List<Factura> attachedFacturaList = new ArrayList<Factura>();
            for (Factura facturaListFacturaToAttach : mesa.getFacturaList()) {
                facturaListFacturaToAttach = em.getReference(facturaListFacturaToAttach.getClass(), facturaListFacturaToAttach.getIdfactura());
                attachedFacturaList.add(facturaListFacturaToAttach);
            }
            mesa.setFacturaList(attachedFacturaList);
            em.persist(mesa);
            if (idareas != null) {
                idareas.getMesaList().add(mesa);
                idareas = em.merge(idareas);
            }
            for (Factura facturaListFactura : mesa.getFacturaList()) {
                Mesa oldIdmesaOfFacturaListFactura = facturaListFactura.getIdmesa();
                facturaListFactura.setIdmesa(mesa);
                facturaListFactura = em.merge(facturaListFactura);
                if (oldIdmesaOfFacturaListFactura != null) {
                    oldIdmesaOfFacturaListFactura.getFacturaList().remove(facturaListFactura);
                    oldIdmesaOfFacturaListFactura = em.merge(oldIdmesaOfFacturaListFactura);
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

    public void edit(Mesa mesa) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Mesa persistentMesa = em.find(Mesa.class, mesa.getIdmesa());
            Areas idareasOld = persistentMesa.getIdareas();
            Areas idareasNew = mesa.getIdareas();
            List<Factura> facturaListOld = persistentMesa.getFacturaList();
            List<Factura> facturaListNew = mesa.getFacturaList();
            List<String> illegalOrphanMessages = null;
            for (Factura facturaListOldFactura : facturaListOld) {
                if (!facturaListNew.contains(facturaListOldFactura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Factura " + facturaListOldFactura + " since its idmesa field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idareasNew != null) {
                idareasNew = em.getReference(idareasNew.getClass(), idareasNew.getIdareas());
                mesa.setIdareas(idareasNew);
            }
            List<Factura> attachedFacturaListNew = new ArrayList<Factura>();
            for (Factura facturaListNewFacturaToAttach : facturaListNew) {
                facturaListNewFacturaToAttach = em.getReference(facturaListNewFacturaToAttach.getClass(), facturaListNewFacturaToAttach.getIdfactura());
                attachedFacturaListNew.add(facturaListNewFacturaToAttach);
            }
            facturaListNew = attachedFacturaListNew;
            mesa.setFacturaList(facturaListNew);
            mesa = em.merge(mesa);
            if (idareasOld != null && !idareasOld.equals(idareasNew)) {
                idareasOld.getMesaList().remove(mesa);
                idareasOld = em.merge(idareasOld);
            }
            if (idareasNew != null && !idareasNew.equals(idareasOld)) {
                idareasNew.getMesaList().add(mesa);
                idareasNew = em.merge(idareasNew);
            }
            for (Factura facturaListNewFactura : facturaListNew) {
                if (!facturaListOld.contains(facturaListNewFactura)) {
                    Mesa oldIdmesaOfFacturaListNewFactura = facturaListNewFactura.getIdmesa();
                    facturaListNewFactura.setIdmesa(mesa);
                    facturaListNewFactura = em.merge(facturaListNewFactura);
                    if (oldIdmesaOfFacturaListNewFactura != null && !oldIdmesaOfFacturaListNewFactura.equals(mesa)) {
                        oldIdmesaOfFacturaListNewFactura.getFacturaList().remove(facturaListNewFactura);
                        oldIdmesaOfFacturaListNewFactura = em.merge(oldIdmesaOfFacturaListNewFactura);
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
                Integer id = mesa.getIdmesa();
                if (findMesa(id) == null) {
                    throw new NonexistentEntityException("The mesa with id " + id + " no longer exists.");
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
            Mesa mesa;
            try {
                mesa = em.getReference(Mesa.class, id);
                mesa.getIdmesa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The mesa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Factura> facturaListOrphanCheck = mesa.getFacturaList();
            for (Factura facturaListOrphanCheckFactura : facturaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Mesa (" + mesa + ") cannot be destroyed since the Factura " + facturaListOrphanCheckFactura + " in its facturaList field has a non-nullable idmesa field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Areas idareas = mesa.getIdareas();
            if (idareas != null) {
                idareas.getMesaList().remove(mesa);
                idareas = em.merge(idareas);
            }
            em.remove(mesa);
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

    public List<Mesa> findMesaEntities() {
        return findMesaEntities(true, -1, -1);
    }

    public List<Mesa> findMesaEntities(int maxResults, int firstResult) {
        return findMesaEntities(false, maxResults, firstResult);
    }

    private List<Mesa> findMesaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Mesa.class));
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

    public Mesa findMesa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Mesa.class, id);
        } finally {
            em.close();
        }
    }

    public int getMesaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Mesa> rt = cq.from(Mesa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
