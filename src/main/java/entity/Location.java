package entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "locations")
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "latitude", nullable = false, unique = true)
    private Double latitude;

    @Column(name = "longitude", nullable = false, unique = true)
    private Double longitude;

    @ManyToMany(mappedBy = "locations")
    private List<User> users;
}
