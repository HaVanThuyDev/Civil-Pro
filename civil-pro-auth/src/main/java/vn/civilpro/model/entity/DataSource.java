package vn.civilpro.model.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "data_souses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DataSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Lob
    @Column(name = "DATA")
    private byte[] data;

    @Column(name = "MEDIA_URL")
    private String imageUrl;

    @Column(name = "MEDIA_TYPE")
    private String mediaType;

    @Column(name = "PUBLIC_ID")
    private String publicId;

    @PostPersist
    private void onPostPersist() {
        if (this.imageUrl == null && "IMAGE".equals(this.mediaType)) {
            this.imageUrl = "/api/main/public/image/" + this.id;
        }
    }

}
