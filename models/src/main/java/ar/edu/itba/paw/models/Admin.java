package ar.edu.itba.paw.models;

import javax.persistence.*;

@Entity
@Table(name = "admins")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admins_admin_id_seq")
    @SequenceGenerator(sequenceName = "admins_admin_id_seq", name = "admins_admin_id_seq", allocationSize = 1)
    @Column(name = "admin_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "user_id")
    private User user;
}
