/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AngelRamirez;

import com.AngelRamirez.exceptions.NonexistentEntityException;
import com.AngelRamirez.exceptions.RollbackFailureException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;

/**
 *
 * @author programacion
 */
public class DetallefacturaJpaController implements Serializable {

    public DetallefacturaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detallefactura detallefactura) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Plato idplato = detallefactura.getIdplato();
            if (idplato != null) {
                idplato = em.getReference(idplato.getClass(), idplato.getIdplato());
                detallefactura.setIdplato(idplato);
            }
            Factura idfactura = detallefactura.getIdfactura();
            if (idfactura != null) {
                idfactura = em.getReference(idfactura.getClass(), idfactura.getIdfactura());
                detallefactura.setIdfactura(idfactura);
            }
            Cocinero idcocinero = detallefactura.getIdcocinero();
            if (idcocinero != null) {
                idcocinero = em.getReference(idcocinero.getClass(), idcocinero.getIdcocinero());
                detallefactura.setIdcocinero(idcocinero);
            }
            em.persist(detallefactura);
            if (idplato != null) {
                idplato.getDetallefacturaList().add(detallefactura);
                idplato = em.merge(idplato);
            }
            if (idfactura != null) {
                idfactura.getDetallefacturaList().add(detallefactura);
                idfactura = em.merge(idfactura);
            }
            if (idcocinero != null) {
                idcocinero.getDetallefacturaList().add(detallefactura);
                idcocinero = em.merge(idcocinero);
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

    public void edit(Detallefactura detallefactura) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Detallefactura persistentDetallefactura = em.find(Detallefactura.class, detallefactura.getIddetallefactura());
            Plato idplatoOld = persistentDetallefactura.getIdplato();
            Plato idplatoNew = detallefactura.getIdplato();
            Factura idfacturaOld = persistentDetallefactura.getIdfactura();
            Factura idfacturaNew = detallefactura.getIdfactura();
            Cocinero idcocineroOld = persistentDetallefactura.getIdcocinero();
            Cocinero idcocineroNew = detallefactura.getIdcocinero();
            if (idplatoNew != null) {
                idplatoNew = em.getReference(idplatoNew.getClass(), idplatoNew.getIdplato());
                detallefactura.setIdplato(idplatoNew);
            }
            if (idfacturaNew != null) {
                idfacturaNew = em.getReference(idfacturaNew.getClass(), idfacturaNew.getIdfactura());
                detallefactura.setIdfactura(idfacturaNew);
            }
            if (idcocineroNew != null) {
                idcocineroNew = em.getReference(idcocineroNew.getClass(), idcocineroNew.getIdcocinero());
                detallefactura.setIdcocinero(idcocineroNew);
            }
            detallefactura = em.merge(detallefactura);
            if (idplatoOld != null && !idplatoOld.equals(idplatoNew)) {
                idplatoOld.getDetallefacturaList().remove(detallefactura);
                idplatoOld = em.merge(idplatoOld);
            }
            if (idplatoNew != null && !idplatoNew.equals(idplatoOld)) {
                idplatoNew.getDetallefacturaList().add(detallefactura);
                idplatoNew = em.merge(idplatoNew);
            }
            if (idfacturaOld != null && !idfacturaOld.equals(idfacturaNew)) {
                idfacturaOld.getDetallefacturaList().remove(detallefactura);
                idfacturaOld = em.merge(idfacturaOld);
            }
            if (idfacturaNew != null && !idfacturaNew.equals(idfacturaOld)) {
                idfacturaNew.getDetallefacturaList().add(detallefactura);
                idfacturaNew = em.merge(idfacturaNew);
            }
            if (idcocineroOld != null && !idcocineroOld.equals(idcocineroNew)) {
                idcocineroOld.getDetallefacturaList().remove(detallefactura);
                idcocineroOld = em.merge(idcocineroOld);
            }
            if (idcocineroNew != null && !idcocineroNew.equals(idcocineroOld)) {
                idcocineroNew.getDetallefacturaList().add(detallefactura);
                idcocineroNew = em.merge(idcocineroNew);
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
                Integer id = detallefactura.getIddetallefactura();
                if (findDetallefactura(id) == null) {
                    throw new NonexistentEntityException("The detallefactura with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Detallefactura detallefactura;
            try {
                detallefactura = em.getReference(Detallefactura.class, id);
                detallefactura.getIddetallefactura();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detallefactura with id " + id + " no longer exists.", enfe);
            }
            Plato idplato = detallefactura.getIdplato();
            if (idplato != null) {
                idplato.getDetallefacturaList().remove(detallefactura);
                idplato = em.merge(idplato);
            }
            Factura idfactura = detallefactura.getIdfactura();
            if (idfactura != null) {
                idfactura.getDetallefacturaList().remove(detallefactura);
                idfactura = em.merge(idfactura);
            }
            Cocinero idcocinero = detallefactura.getIdcocinero();
            if (idcocinero != null) {
                idcocinero.getDetallefacturaList().remove(detallefactura);
                idcocinero = em.merge(idcocinero);
            }
            em.remove(detallefactura);
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

    public List<Detallefactura> findDetallefacturaEntities() {
        return findDetallefacturaEntities(true, -1, -1);
    }

    public List<Detallefactura> findDetallefacturaEntities(int maxResults, int firstResult) {
        return findDetallefacturaEntities(false, maxResults, firstResult);
    }

    private List<Detallefactura> findDetallefacturaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detallefactura.class));
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

    public Detallefactura findDetallefactura(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detallefactura.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetallefacturaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detallefactura> rt = cq.from(Detallefactura.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
