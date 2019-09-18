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
@Table(name = "plato")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Plato.findAll", query = "SELECT p FROM Plato p")
    , @NamedQuery(name = "Plato.findByIdplato", query = "SELECT p FROM Plato p WHERE p.idplato = :idplato")
    , @NamedQuery(name = "Plato.findByDescripcion", query = "SELECT p FROM Plato p WHERE p.descripcion = :descripcion")
    , @NamedQuery(name = "Plato.findByPrecio", query = "SELECT p FROM Plato p WHERE p.precio = :precio")})
public class Plato implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "idplato")
    private Integer idplato;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @NotNull
    @Column(name = "precio")
    private long precio;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idplato")
    private List<Detallefactura> detallefacturaList;

    public Plato() {
    }

    public Plato(Integer idplato) {
        this.idplato = idplato;
    }

    public Plato(Integer idplato, String descripcion, long precio) {
        this.idplato = idplato;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public Integer getIdplato() {
        return idplato;
    }

    public void setIdplato(Integer idplato) {
        this.idplato = idplato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public long getPrecio() {
        return precio;
    }

    public void setPrecio(long precio) {
        this.precio = precio;
    }

    @XmlTransient
    public List<Detallefactura> getDetallefacturaList() {
        return detallefacturaList;
    }

    public void setDetallefacturaList(List<Detallefactura> detallefacturaList) {
        this.detallefacturaList = detallefacturaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idplato != null ? idplato.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Plato)) {
            return false;
        }
        Plato other = (Plato) object;
        if ((this.idplato == null && other.idplato != null) || (this.idplato != null && !this.idplato.equals(other.idplato))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.AngelRamirez.Plato[ idplato=" + idplato + " ]";
    }
    
}
