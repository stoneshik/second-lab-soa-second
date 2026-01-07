package lab.soa.resource.dto;

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
public class CoordinatesResponseDto {
    @XmlElement(name = "id")
    private Long id;

    @XmlElement(name = "x")
    private Float x;

    @XmlElement(name = "y")
    private Long y;
}
