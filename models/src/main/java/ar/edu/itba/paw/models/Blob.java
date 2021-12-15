package ar.edu.itba.paw.models;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "blobs")
public class Blob {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blobs_blob_id_seq")
    @SequenceGenerator(sequenceName = "blobs_blob_id_seq", name = "blobs_blob_id_seq", allocationSize = 1)
    @Column(name = "blob_id")
    private Long id;

    @Lob
    @Type(type = "org.hibernate.type.BinaryType")
    @Column(name = "picture", nullable = false)
    @Basic(fetch = FetchType.LAZY, optional = false)
    private byte[] picture;


    public Blob() {
        // Hibernate
    }

    public Blob(byte[] picture) {
        this.picture = picture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }
}
