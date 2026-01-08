package lab.soa.resource.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
@XmlRootElement(name = "error")
@XmlAccessorType(XmlAccessType.FIELD)
public class ErrorMessageResponseDto {
    @XmlElement(name = "message")
    private String message;

    @Builder.Default
    @XmlElement(name = "time")
    private String time = LocalDateTime.now().toString();

    @Builder.Default
    @XmlElementWrapper(name = "violations")
    @XmlElement(name = "violation")
    private List<String> violations = new ArrayList<>();
}
