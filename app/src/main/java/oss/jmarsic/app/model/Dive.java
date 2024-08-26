package oss.jmarsic.app.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class Dive {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String location;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    private int depth;

    private int duration;

    @Column(length = 1000)
    private String additionalInfo;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
