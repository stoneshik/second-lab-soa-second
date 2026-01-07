package lab.soa.resource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lab.soa.domain.BalconyType;
import lab.soa.domain.Coordinates;
import lab.soa.domain.Flat;
import lab.soa.domain.House;
import lab.soa.domain.PriceType;
import lab.soa.domain.SortType;
import lab.soa.domain.TransportType;
import lab.soa.resource.dto.CoordinatesResponseDto;
import lab.soa.resource.dto.ErrorMessageResponseDto;
import lab.soa.resource.dto.FlatResponseByIdDto;
import lab.soa.resource.dto.FlatResponseDto;
import lab.soa.resource.dto.HouseResponseDto;
import lab.soa.resource.dto.WrapperListFlatsResponseDto;

@Stateless
@Path("/flats")
@Produces(MediaType.APPLICATION_XML)
public class FlatResource {
    @PersistenceContext(unitName = "flatServicePU")
    private EntityManager entityManager;

    /**
     * GET /api/v1/flats/find-with-balcony/{priceType}/{balconyType}
     */
    @GET
    @Path("/find-with-balcony/{priceType}/{balconyType}")
    public Response findWithBalcony(
            @PathParam("priceType") String priceTypeStr,
            @PathParam("balconyType") String balconyTypeStr) {
        try {
            // Валидация параметров
            if (!PriceType.isValidValue(priceTypeStr) || !BalconyType.isValidValue(balconyTypeStr)) {
                ErrorMessageResponseDto error = ErrorMessageResponseDto.builder()
                    .message("Invalid param value")
                    .time(LocalDateTime.now())
                    .build();
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
            }
            PriceType priceType = PriceType.valueOf(priceTypeStr.toUpperCase());
            BalconyType balconyType = BalconyType.valueOf(balconyTypeStr.toUpperCase());
            // Поиск квартиры через Criteria API
            Flat flat = findFlatByPriceAndBalcony(priceType, balconyType);
            if (flat == null) {
                ErrorMessageResponseDto error = ErrorMessageResponseDto.builder()
                    .message("Flat not found")
                    .time(LocalDateTime.now())
                    .build();
                return Response.status(Response.Status.NOT_FOUND)
                    .entity(error)
                    .build();
            }
            // Конвертация в DTO
            FlatResponseByIdDto flatDto = convertToFlatByIdResponseDto(flat);
            System.out.println(flatDto);
            return Response.ok(flatDto).build();
        } catch (NoResultException e) {
            System.out.println(e.getMessage());
            ErrorMessageResponseDto error = ErrorMessageResponseDto.builder()
                .message("No results found")
                .time(LocalDateTime.now())
                .build();
            return Response.status(Response.Status.NOT_FOUND)
                .entity(error)
                .build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ErrorMessageResponseDto error = ErrorMessageResponseDto.builder()
                .message("Internal server error")
                .time(LocalDateTime.now())
                .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
        }
    }

    /**
     * GET /api/v1/flats/get-ordered-by-time-to-metro/{transportType}/{sortType}
     */
    @GET
    @Path("/get-ordered-by-time-to-metro/{transportType}/{sortType}")
    public Response getOrderedByTimeToMetro(
            @PathParam("transportType") String transportTypeStr,
            @PathParam("sortType") String sortTypeStr,
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("size") @DefaultValue("10") Integer size) {
        try {
            // Валидация параметров
            if (!TransportType.isValidValue(transportTypeStr) || !SortType.isValidValue(sortTypeStr)) {
                ErrorMessageResponseDto error = ErrorMessageResponseDto.builder()
                    .message("Invalid param value")
                    .time(LocalDateTime.now())
                    .build();
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
            }
            if (page < 0) {
                ErrorMessageResponseDto error = ErrorMessageResponseDto.builder()
                    .message("Invalid page parameter")
                    .time(LocalDateTime.now())
                    .build();
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
            }
            if (size < 1) {
                ErrorMessageResponseDto error = ErrorMessageResponseDto.builder()
                    .message("Invalid pageSize parameter")
                    .time(LocalDateTime.now())
                    .build();
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
            }
            TransportType transportType = TransportType.valueOf(transportTypeStr.toUpperCase());
            SortType sortType = SortType.valueOf(sortTypeStr.toUpperCase());
            // Получаем общее количество
            Long totalElements = getTotalFlatsCount();
            Integer totalPages = (int) Math.ceil((double) totalElements / size);
            // Проверка корректности страницы
            if (totalElements > 0 && page >= totalPages) {
                ErrorMessageResponseDto error = ErrorMessageResponseDto.builder()
                    .message("Invalid page number")
                    .time(LocalDateTime.now())
                    .build();
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
            }
            // Получаем данные с пагинацией
            List<Flat> flats = getFlatsOrderedByTimeToMetro(
                transportType, sortType, page, size
            );
            // Конвертируем в DTO
            List<FlatResponseDto> flatDtos = flats.stream()
                .map(this::convertToFlatResponseDto)
                .collect(Collectors.toList());
            // Создаем оберточный DTO с пагинацией
            WrapperListFlatsResponseDto response = WrapperListFlatsResponseDto.builder()
                .totalElements(Long.valueOf(flatDtos.size()))
                .totalPages(totalPages)
                .currentPage(page)
                .pageSize(flatDtos.size())
                .flats(flatDtos)
                .build();
            return Response.ok(response).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            ErrorMessageResponseDto error = ErrorMessageResponseDto.builder()
                .message("Internal server error")
                .time(LocalDateTime.now())
                .build();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(error)
                .build();
        }
    }

    /**
     * Вспомогательный метод: поиск квартиры по цене и балкону
     */
    private Flat findFlatByPriceAndBalcony(PriceType priceType, BalconyType balconyType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Flat> query = cb.createQuery(Flat.class);
        Root<Flat> flat = query.from(Flat.class);
        // WHERE clause
        Predicate balconyPredicate = cb.equal(flat.get("balconyType"), balconyType);
        query.where(balconyPredicate);
        // ORDER BY clause
        if (priceType == PriceType.CHEAPEST) {
            query.orderBy(cb.asc(flat.get("price")));
        } else {
            query.orderBy(cb.desc(flat.get("price")));
        }
        TypedQuery<Flat> typedQuery = entityManager.createQuery(query);
        typedQuery.setMaxResults(1);
        try {
            return typedQuery.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Вспомогательный метод: получение квартир, отсортированных по времени до метро
     */
    private List<Flat> getFlatsOrderedByTimeToMetro(
            TransportType transportType,
            SortType sortType,
            int page,
            int pageSize
        ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Flat> query = cb.createQuery(Flat.class);
        Root<Flat> flat = query.from(Flat.class);
        String timeField = (transportType == TransportType.WALKING)
            ? "walkingMinutesToMetro"
            : "transportMinutesToMetro";
        if (sortType == SortType.ASC) {
            query.orderBy(cb.asc(flat.get(timeField)));
        } else {
            query.orderBy(cb.desc(flat.get(timeField)));
        }
        TypedQuery<Flat> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(page * pageSize);
        typedQuery.setMaxResults(pageSize);
        return typedQuery.getResultList();
    }

    /**
     * Вспомогательный метод: общее количество квартир
     */
    private Long getTotalFlatsCount() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Flat> flat = query.from(Flat.class);
        query.select(cb.count(flat));
        return entityManager.createQuery(query).getSingleResult();
    }

    /**
     * Конвертация Flat в FlatResponseDto
     */
    private FlatResponseDto convertToFlatResponseDto(Flat flat) {
        if (flat == null) {
            return null;
        }
        return FlatResponseDto.builder()
            .id(flat.getId())
            .name(flat.getName())
            .coordinates(convertToCoordinatesResponseDto(flat.getCoordinates()))
            .creationDate(flat.getCreationDate())
            .area(flat.getArea())
            .numberOfRooms(flat.getNumberOfRooms())
            .height(flat.getHeight())
            .view(flat.getView() != null ? flat.getView().name() : null)
            .transport(flat.getTransport() != null ? flat.getTransport().name() : null)
            .house(convertToHouseResponseDto(flat.getHouse()))
            .price(flat.getPrice())
            .balconyType(flat.getBalconyType() != null ? flat.getBalconyType().name() : null)
            .walkingMinutesToMetro(flat.getWalkingMinutesToMetro())
            .transportMinutesToMetro(flat.getTransportMinutesToMetro())
            .build();
    }
    private FlatResponseByIdDto convertToFlatByIdResponseDto(Flat flat) {
        if (flat == null) {
            return null;
        }
        return FlatResponseByIdDto.builder()
            .id(flat.getId())
            .name(flat.getName())
            .coordinates(convertToCoordinatesResponseDto(flat.getCoordinates()))
            .creationDate(flat.getCreationDate())
            .area(flat.getArea())
            .numberOfRooms(flat.getNumberOfRooms())
            .height(flat.getHeight())
            .view(flat.getView() != null ? flat.getView().name() : null)
            .transport(flat.getTransport() != null ? flat.getTransport().name() : null)
            .house(convertToHouseResponseDto(flat.getHouse()))
            .price(flat.getPrice())
            .balconyType(flat.getBalconyType() != null ? flat.getBalconyType().name() : null)
            .walkingMinutesToMetro(flat.getWalkingMinutesToMetro())
            .transportMinutesToMetro(flat.getTransportMinutesToMetro())
            .build();
    }

    /**
     * Конвертация Coordinates в CoordinatesResponseDto
     */
    private CoordinatesResponseDto convertToCoordinatesResponseDto(Coordinates coordinates) {
        if (coordinates == null) {
            return null;
        }
        return CoordinatesResponseDto.builder()
            .id(coordinates.getId())
            .x(coordinates.getX())
            .y(coordinates.getY())
            .build();
    }

    /**
     * Конвертация House в HouseResponseDto
     */
    private HouseResponseDto convertToHouseResponseDto(House house) {
        if (house == null) {
            return null;
        }
        return HouseResponseDto.builder()
            .id(house.getId())
            .name(house.getName())
            .year(house.getYear())
            .numberOfFlatsOnFloor(house.getNumberOfFlatsOnFloor())
            .build();
    }

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String ping() {
        System.out.println("=== PING METHOD CALLED ===");
        return "FlatResource is alive! Time: " + java.time.LocalDateTime.now();
    }
}
