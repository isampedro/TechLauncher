package ar.edu.itba.paw.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "verification_token")
public class VerificationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "verification_token_token_id_seq")
    @SequenceGenerator(sequenceName = "verification_token_token_id_seq", name = "verification_token_token_id_seq", allocationSize = 1)
    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "token",nullable = false)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "exp_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryDay;

    public VerificationToken(){
        //For Hibernate
    }

    public VerificationToken(String token, User user, Date expiryDay) {
        this.token = token;
        this.user = user;
        this.expiryDay = expiryDay;
    }

    public Long getTokenId() {
        return tokenId;
    }

    public String getToken() {
        return token;
    }

    public long getUserId() {
        return user.getId();
    }

    public Date getExpiryDay() {
        return expiryDay;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpiryDay(Date expiryDay) {
        this.expiryDay = expiryDay;
    }
}
