/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.AngelRamirez;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author programacion
 */
@Entity
@Table(name = "clientes")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Clientes.findAll", query = "SELECT c FROM Clientes c")
    , @NamedQuery(name = "Clientes.findByNit", query = "SELECT c FROM Clientes c WHERE c.nit = :nit")
    , @NamedQuery(name = "Clientes.findByIdmesa", query = "SELECT c FROM Clientes c WHERE c.idmesa = :idmesa")
    , @NamedQuery(name = "Clientes.findByIdempleados", query = "SELECT c FROM Clientes c WHERE c.idempleados = :idempleados")
    , @NamedQuery(name = "Clientes.findByIdsucursal", query = "SELECT c FROM Clientes c WHERE c.idsucursal = :idsucursal")})
public class Clientes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "nit")
    private Integer nit;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idmesa")
    private int idmesa;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idempleados")
    private int idempleados;
    @Basic(optional = false)
    @NotNull
    @Column(name = "idsucursal")
    private int idsucursal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "nit")
    private List<Factura> facturaList;

    public Clientes() {
    }

    public Clientes(Integer nit) {
        this.nit = nit;
    }

    public Clientes(Integer nit, int idmesa, int idempleados, int idsucursal) {
        this.nit = nit;
        this.idmesa = idmesa;
        this.idempleados = idempleados;
        this.idsucursal = idsucursal;
    }

    public Integer getNit() {
        return nit;
    }

    public void setNit(Integer nit) {
        this.nit = nit;
    }

    public int getIdmesa() {
        return idmesa;
    }

    public void setIdmesa(int idmesa) {
        this.idmesa = idmesa;
    }

    public int getIdempleados() {
        return idempleados;
    }

    public void setIdempleados(int idempleados) {
        this.idempleados = idempleados;
    }

    public int getIdsucursal() {
        return idsucursal;
    }

    public void setIdsucursal(int idsucursal) {
        this.idsucursal = idsucursal;
    }

    @XmlTransient
    public List<Factura> getFacturaList() {
        return facturaList;
    }

    public void setFacturaList(List<Factura> facturaList) {
        this.facturaList = facturaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (nit != null ? nit.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Clientes)) {
            return false;
        }
        Clientes other = (Clientes) object;
        if ((this.nit == null && other.nit != null) || (this.nit != null && !this.nit.equals(other.nit))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.AngelRamirez.Clientes[ nit=" + nit + " ]";
    }
    
}
