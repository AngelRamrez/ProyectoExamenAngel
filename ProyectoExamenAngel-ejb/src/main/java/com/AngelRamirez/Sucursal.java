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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author programacion
 */
@Entity
@Table(name = "sucursal")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sucursal.findAll", query = "SELECT s FROM Sucursal s")
    , @NamedQuery(name = "Sucursal.findByIdsucursal", query = "SELECT s FROM Sucursal s WHERE s.idsucursal = :idsucursal")
    , @NamedQuery(name = "Sucursal.findByNombresucursal", query = "SELECT s FROM Sucursal s WHERE s.nombresucursal = :nombresucursal")
    , @NamedQuery(name = "Sucursal.findByUbicacion", query = "SELECT s FROM Sucursal s WHERE s.ubicacion = :ubicacion")})
public class Sucursal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idsucursal")
    private Integer idsucursal;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "nombresucursal")
    private String nombresucursal;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "ubicacion")
    private String ubicacion;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idsucursal")
    private List<Factura> facturaList;

    public Sucursal() {
    }

    public Sucursal(Integer idsucursal) {
        this.idsucursal = idsucursal;
    }

    public Sucursal(Integer idsucursal, String nombresucursal, String ubicacion) {
        this.idsucursal = idsucursal;
        this.nombresucursal = nombresucursal;
        this.ubicacion = ubicacion;
    }

    public Integer getIdsucursal() {
        return idsucursal;
    }

    public void setIdsucursal(Integer idsucursal) {
        this.idsucursal = idsucursal;
    }

    public String getNombresucursal() {
        return nombresucursal;
    }

    public void setNombresucursal(String nombresucursal) {
        this.nombresucursal = nombresucursal;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
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
        hash += (idsucursal != null ? idsucursal.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sucursal)) {
            return false;
        }
        Sucursal other = (Sucursal) object;
        if ((this.idsucursal == null && other.idsucursal != null) || (this.idsucursal != null && !this.idsucursal.equals(other.idsucursal))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.AngelRamirez.Sucursal[ idsucursal=" + idsucursal + " ]";
    }
    
}
