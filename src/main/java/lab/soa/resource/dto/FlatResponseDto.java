package lab.soa.resource.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlAccessorType(XmlAccessType.FIELD)
public class FlatResponseDto {
    @XmlElement(name = "id")
    private Long id;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "coordinates")
    private CoordinatesResponseDto coordinates;

    @XmlElement(name = "creationDate")
    private String creationDate;

    @XmlElement(name = "area")
    private Integer area;

    @XmlElement(name = "numberOfRooms")
    private Integer numberOfRooms;

    @XmlElement(name = "height")
    private Integer height;

    @XmlElement(name = "view")
    private String view;

    @XmlElement(name = "transport")
    private String transport;

    @XmlElement(name = "house")
    private HouseResponseDto house;

    @XmlElement(name = "price")
    private BigDecimal price;

    @XmlElement(name = "balconyType")
    private String balconyType;

    @XmlElement(name = "walkingMinutesToMetro")
    private Integer walkingMinutesToMetro;

    @XmlElement(name = "transportMinutesToMetro")
    private Integer transportMinutesToMetro;
}
