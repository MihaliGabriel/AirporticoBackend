package main.model;

import main.utils.Util;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="mltxt_airplane",  uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ref_language", "ref_airplane", "ref_text_type"})}
        )
public class AirplaneTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="ref_language", nullable = false)
    private Language language;

    @Column(name="text", nullable = false)
    private String text;

    @Convert(converter = TextTypeConverter.class)
    @ManyToOne
    @JoinColumn(name="ref_text_type", nullable = false)
    private TextTypeEntity textType;

    @ManyToOne
    @JoinColumn(name="ref_airplane", nullable = false)
    private Airplane airplane;

    @CreationTimestamp
    @Column(name="created_at", nullable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;


    public Long getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = Util.getLong(id);
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public TextTypeEntity getTextType() {
        return textType;
    }

    public void setTextType(TextTypeEntity textType) {
        this.textType = textType;
    }

    public Airplane getAirplane() {
        return airplane;
    }

    public void setAirplane(Airplane airplane) {
        this.airplane = airplane;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AirplaneTranslation{" +
                "id=" + id +
                ", language=" + language +
                ", text='" + text + '\'' +
                ", textType=" + textType +
                ", airplane=" + airplane +
                ", createdAt=" + createdAt +
                '}';
    }
}
