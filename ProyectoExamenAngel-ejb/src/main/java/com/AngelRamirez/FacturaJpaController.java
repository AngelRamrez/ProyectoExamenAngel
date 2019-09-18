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
public class FacturaJpaController implements Serializable {

    public FacturaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Factura factura) throws RollbackFailureException, Exception {
        if (factura.getDetallefacturaList() == null) {
            factura.setDetallefacturaList(new ArrayList<Detallefactura>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Clientes nit = factura.getNit();
            if (nit != null) {
                nit = em.getReference(nit.getClass(), nit.getNit());
                factura.setNit(nit);
            }
            Mesa idmesa = factura.getIdmesa();
            if (idmesa != null) {
                idmesa = em.getReference(idmesa.getClass(), idmesa.getIdmesa());
                factura.setIdmesa(idmesa);
            }
            Sucursal idsucursal = factura.getIdsucursal();
            if (idsucursal != null) {
                idsucursal = em.getReference(idsucursal.getClass(), idsucursal.getIdsucursal());
                factura.setIdsucursal(idsucursal);
            }
            Mesero idmesero = factura.getIdmesero();
            if (idmesero != null) {
                idmesero = em.getReference(idmesero.getClass(), idmesero.getIdmesero());
                factura.setIdmesero(idmesero);
            }
            List<Detallefactura> attachedDetallefacturaList = new ArrayList<Detallefactura>();
            for (Detallefactura detallefacturaListDetallefacturaToAttach : factura.getDetallefacturaList()) {
                detallefacturaListDetallefacturaToAttach = em.getReference(detallefacturaListDetallefacturaToAttach.getClass(), detallefacturaListDetallefacturaToAttach.getIddetallefactura());
                attachedDetallefacturaList.add(detallefacturaListDetallefacturaToAttach);
            }
            factura.setDetallefacturaList(attachedDetallefacturaList);
            em.persist(factura);
            if (nit != null) {
                nit.getFacturaList().add(factura);
                nit = em.merge(nit);
            }
            if (idmesa != null) {
                idmesa.getFacturaList().add(factura);
                idmesa = em.merge(idmesa);
            }
            if (idsucursal != null) {
                idsucursal.getFacturaList().add(factura);
                idsucursal = em.merge(idsucursal);
            }
            if (idmesero != null) {
                idmesero.getFacturaList().add(factura);
                idmesero = em.merge(idmesero);
            }
            for (Detallefactura detallefacturaListDetallefactura : factura.getDetallefacturaList()) {
                Factura oldIdfacturaOfDetallefacturaListDetallefactura = detallefacturaListDetallefactura.getIdfactura();
                detallefacturaListDetallefactura.setIdfactura(factura);
                detallefacturaListDetallefactura = em.merge(detallefacturaListDetallefactura);
                if (oldIdfacturaOfDetallefacturaListDetallefactura != null) {
                    oldIdfacturaOfDetallefacturaListDetallefactura.getDetallefacturaList().remove(detallefacturaListDetallefactura);
                    oldIdfacturaOfDetallefacturaListDetallefactura = em.merge(oldIdfacturaOfDetallefacturaListDetallefactura);
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

    public void edit(Factura factura) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Factura persistentFactura = em.find(Factura.class, factura.getIdfactura());
            Clientes nitOld = persistentFactura.getNit();
            Clientes nitNew = factura.getNit();
            Mesa idmesaOld = persistentFactura.getIdmesa();
            Mesa idmesaNew = factura.getIdmesa();
            Sucursal idsucursalOld = persistentFactura.getIdsucursal();
            Sucursal idsucursalNew = factura.getIdsucursal();
            Mesero idmeseroOld = persistentFactura.getIdmesero();
            Mesero idmeseroNew = factura.getIdmesero();
            List<Detallefactura> detallefacturaListOld = persistentFactura.getDetallefacturaList();
            List<Detallefactura> detallefacturaListNew = factura.getDetallefacturaList();
            List<String> illegalOrphanMessages = null;
            for (Detallefactura detallefacturaListOldDetallefactura : detallefacturaListOld) {
                if (!detallefacturaListNew.contains(detallefacturaListOldDetallefactura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detallefactura " + detallefacturaListOldDetallefactura + " since its idfactura field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (nitNew != null) {
                nitNew = em.getReference(nitNew.getClass(), nitNew.getNit());
                factura.setNit(nitNew);
            }
            if (idmesaNew != null) {
                idmesaNew = em.getReference(idmesaNew.getClass(), idmesaNew.getIdmesa());
                factura.setIdmesa(idmesaNew);
            }
            if (idsucursalNew != null) {
                idsucursalNew = em.getReference(idsucursalNew.getClass(), idsucursalNew.getIdsucursal());
                factura.setIdsucursal(idsucursalNew);
            }
            if (idmeseroNew != null) {
                idmeseroNew = em.getReference(idmeseroNew.getClass(), idmeseroNew.getIdmesero());
                factura.setIdmesero(idmeseroNew);
            }
            List<Detallefactura> attachedDetallefacturaListNew = new ArrayList<Detallefactura>();
            for (Detallefactura detallefacturaListNewDetallefacturaToAttach : detallefacturaListNew) {
                detallefacturaListNewDetallefacturaToAttach = em.getReference(detallefacturaListNewDetallefacturaToAttach.getClass(), detallefacturaListNewDetallefacturaToAttach.getIddetallefactura());
                attachedDetallefacturaListNew.add(detallefacturaListNewDetallefacturaToAttach);
            }
            detallefacturaListNew = attachedDetallefacturaListNew;
            factura.setDetallefacturaList(detallefacturaListNew);
            factura = em.merge(factura);
            if (nitOld != null && !nitOld.equals(nitNew)) {
                nitOld.getFacturaList().remove(factura);
                nitOld = em.merge(nitOld);
            }
            if (nitNew != null && !nitNew.equals(nitOld)) {
                nitNew.getFacturaList().add(factura);
                nitNew = em.merge(nitNew);
            }
            if (idmesaOld != null && !idmesaOld.equals(idmesaNew)) {
                idmesaOld.getFacturaList().remove(factura);
                idmesaOld = em.merge(idmesaOld);
            }
            if (idmesaNew != null && !idmesaNew.equals(idmesaOld)) {
                idmesaNew.getFacturaList().add(factura);
                idmesaNew = em.merge(idmesaNew);
            }
            if (idsucursalOld != null && !idsucursalOld.equals(idsucursalNew)) {
                idsucursalOld.getFacturaList().remove(factura);
                idsucursalOld = em.merge(idsucursalOld);
            }
            if (idsucursalNew != null && !idsucursalNew.equals(idsucursalOld)) {
                idsucursalNew.getFacturaList().add(factura);
                idsucursalNew = em.merge(idsucursalNew);
            }
            if (idmeseroOld != null && !idmeseroOld.equals(idmeseroNew)) {
                idmeseroOld.getFacturaList().remove(factura);
                idmeseroOld = em.merge(idmeseroOld);
            }
            if (idmeseroNew != null && !idmeseroNew.equals(idmeseroOld)) {
                idmeseroNew.getFacturaList().add(factura);
                idmeseroNew = em.merge(idmeseroNew);
            }
            for (Detallefactura detallefacturaListNewDetallefactura : detallefacturaListNew) {
                if (!detallefacturaListOld.contains(detallefacturaListNewDetallefactura)) {
                    Factura oldIdfacturaOfDetallefacturaListNewDetallefactura = detallefacturaListNewDetallefactura.getIdfactura();
                    detallefacturaListNewDetallefactura.setIdfactura(factura);
                    detallefacturaListNewDetallefactura = em.merge(detallefacturaListNewDetallefactura);
                    if (oldIdfacturaOfDetallefacturaListNewDetallefactura != null && !oldIdfacturaOfDetallefacturaListNewDetallefactura.equals(factura)) {
                        oldIdfacturaOfDetallefacturaListNewDetallefactura.getDetallefacturaList().remove(detallefacturaListNewDetallefactura);
                        oldIdfacturaOfDetallefacturaListNewDetallefactura = em.merge(oldIdfacturaOfDetallefacturaListNewDetallefactura);
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
                Integer id = factura.getIdfactura();
                if (findFactura(id) == null) {
                    throw new NonexistentEntityException("The factura with id " + id + " no longer exists.");
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
            Factura factura;
            try {
                factura = em.getReference(Factura.class, id);
                factura.getIdfactura();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The factura with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Detallefactura> detallefacturaListOrphanCheck = factura.getDetallefacturaList();
            for (Detallefactura detallefacturaListOrphanCheckDetallefactura : detallefacturaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Factura (" + factura + ") cannot be destroyed since the Detallefactura " + detallefacturaListOrphanCheckDetallefactura + " in its detallefacturaList field has a non-nullable idfactura field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Clientes nit = factura.getNit();
            if (nit != null) {
                nit.getFacturaList().remove(factura);
                nit = em.merge(nit);
            }
            Mesa idmesa = factura.getIdmesa();
            if (idmesa != null) {
                idmesa.getFacturaList().remove(factura);
                idmesa = em.merge(idmesa);
            }
            Sucursal idsucursal = factura.getIdsucursal();
            if (idsucursal != null) {
                idsucursal.getFacturaList().remove(factura);
                idsucursal = em.merge(idsucursal);
            }
            Mesero idmesero = factura.getIdmesero();
            if (idmesero != null) {
                idmesero.getFacturaList().remove(factura);
                idmesero = em.merge(idmesero);
            }
            em.remove(factura);
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

    public List<Factura> findFacturaEntities() {
        return findFacturaEntities(true, -1, -1);
    }

    public List<Factura> findFacturaEntities(int maxResults, int firstResult) {
        return findFacturaEntities(false, maxResults, firstResult);
    }

    private List<Factura> findFacturaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Factura.class));
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

    public Factura findFactura(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Factura.class, id);
        } finally {
            em.close();
        }
    }

    public int getFacturaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Factura> rt = cq.from(Factura.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
