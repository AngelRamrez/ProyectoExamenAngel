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
public class ClientesJpaController implements Serializable {

    public ClientesJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Clientes clientes) throws RollbackFailureException, Exception {
        if (clientes.getFacturaList() == null) {
            clientes.setFacturaList(new ArrayList<Factura>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Factura> attachedFacturaList = new ArrayList<Factura>();
            for (Factura facturaListFacturaToAttach : clientes.getFacturaList()) {
                facturaListFacturaToAttach = em.getReference(facturaListFacturaToAttach.getClass(), facturaListFacturaToAttach.getIdfactura());
                attachedFacturaList.add(facturaListFacturaToAttach);
            }
            clientes.setFacturaList(attachedFacturaList);
            em.persist(clientes);
            for (Factura facturaListFactura : clientes.getFacturaList()) {
                Clientes oldNitOfFacturaListFactura = facturaListFactura.getNit();
                facturaListFactura.setNit(clientes);
                facturaListFactura = em.merge(facturaListFactura);
                if (oldNitOfFacturaListFactura != null) {
                    oldNitOfFacturaListFactura.getFacturaList().remove(facturaListFactura);
                    oldNitOfFacturaListFactura = em.merge(oldNitOfFacturaListFactura);
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

    public void edit(Clientes clientes) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Clientes persistentClientes = em.find(Clientes.class, clientes.getNit());
            List<Factura> facturaListOld = persistentClientes.getFacturaList();
            List<Factura> facturaListNew = clientes.getFacturaList();
            List<String> illegalOrphanMessages = null;
            for (Factura facturaListOldFactura : facturaListOld) {
                if (!facturaListNew.contains(facturaListOldFactura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Factura " + facturaListOldFactura + " since its nit field is not nullable.");
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
            clientes.setFacturaList(facturaListNew);
            clientes = em.merge(clientes);
            for (Factura facturaListNewFactura : facturaListNew) {
                if (!facturaListOld.contains(facturaListNewFactura)) {
                    Clientes oldNitOfFacturaListNewFactura = facturaListNewFactura.getNit();
                    facturaListNewFactura.setNit(clientes);
                    facturaListNewFactura = em.merge(facturaListNewFactura);
                    if (oldNitOfFacturaListNewFactura != null && !oldNitOfFacturaListNewFactura.equals(clientes)) {
                        oldNitOfFacturaListNewFactura.getFacturaList().remove(facturaListNewFactura);
                        oldNitOfFacturaListNewFactura = em.merge(oldNitOfFacturaListNewFactura);
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
                Integer id = clientes.getNit();
                if (findClientes(id) == null) {
                    throw new NonexistentEntityException("The clientes with id " + id + " no longer exists.");
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
            Clientes clientes;
            try {
                clientes = em.getReference(Clientes.class, id);
                clientes.getNit();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The clientes with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Factura> facturaListOrphanCheck = clientes.getFacturaList();
            for (Factura facturaListOrphanCheckFactura : facturaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Clientes (" + clientes + ") cannot be destroyed since the Factura " + facturaListOrphanCheckFactura + " in its facturaList field has a non-nullable nit field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(clientes);
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

    public List<Clientes> findClientesEntities() {
        return findClientesEntities(true, -1, -1);
    }

    public List<Clientes> findClientesEntities(int maxResults, int firstResult) {
        return findClientesEntities(false, maxResults, firstResult);
    }

    private List<Clientes> findClientesEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Clientes.class));
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

    public Clientes findClientes(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Clientes.class, id);
        } finally {
            em.close();
        }
    }

    public int getClientesCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Clientes> rt = cq.from(Clientes.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
