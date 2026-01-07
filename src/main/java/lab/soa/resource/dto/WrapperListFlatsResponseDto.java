package lab.soa.resource.dto;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name = "flatsPage")
@XmlAccessorType(XmlAccessType.FIELD)
public class WrapperListFlatsResponseDto {
    @XmlElement(name = "totalElements")
    private Long totalElements;

    @XmlElement(name = "totalPages")
    private Integer totalPages;

    @XmlElement(name = "currentPage")
    private Integer currentPage;

    @XmlElement(name = "pageSize")
    private Integer pageSize;

    @XmlElementWrapper(name = "flats")
    @XmlElement(name = "flat")
    private List<FlatResponseDto> flats;
}
