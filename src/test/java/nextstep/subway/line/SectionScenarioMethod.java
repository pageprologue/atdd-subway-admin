package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.assured.RestAssuredApi;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.line.dto.SectionResponse;
import nextstep.subway.station.dto.StationResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.line.LineScenarioMethod.지하철_노선_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

class SectionScenarioMethod {

    public static SectionRequest 지하철_구간_정보(Map<String, Long> stations, String upStationKey, String downStationKey, int distance) {
        return new SectionRequest(stations.get(upStationKey), stations.get(downStationKey), distance);
    }

    public static ExtractableResponse<Response> 지하철_구간_생성_요청(String uri, SectionRequest request) {
        return RestAssuredApi.post(uri + "/sections", request);
    }

    public static String 지하철_구간_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        return response.header("Location");
    }

    public static void 지하철_구간_생성_실패됨(ExtractableResponse<Response> response, int statusCode) {
        assertThat(response.statusCode()).isEqualTo(statusCode);
    }

    public static void 지하철_노선에_등록한_구간_포함됨(String uri, List<String> stationNames) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(uri);
        assertThat(response.jsonPath().getList("stations.name"))
                .isEqualTo(stationNames);
    }

    public static void 지하철_구간_개수_일치됨(ExtractableResponse<Response> response, int size) {
        List<String> result = response.jsonPath().getList("upStation.name");
        assertThat(result.size()).isEqualTo(size);
    }

    public static void 지하철_구간_상행역_일치됨(ExtractableResponse<Response> response, List<String> stationNames) {
        List<String> result = response.jsonPath().getList("upStation.name");
        assertThat(result.size()).isEqualTo(3);
        assertThat(result)
                .isEqualTo(stationNames);
    }

    public static ExtractableResponse<Response> 지하철_구간_조회_요청(String uri) {
        return RestAssuredApi.get(uri + "/sections");
    }

    public static Map<String, Long> 등록된_구간_지하철역(String uri) {
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(uri);
        List<StationResponse> stations = response.jsonPath().getList("stations", StationResponse.class);
        return stations.stream()
                .collect(Collectors.toMap(
                        StationResponse::getName, StationResponse::getId
                ));
    }

    public static ExtractableResponse<Response> 지하철_구간_삭제_요청(String uri, Long sectionId) {
        return RestAssuredApi.delete(uri + "/sections?stationId=" + sectionId);
    }

    public static void 지하철_구간_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    public static void 지하철_구간_삭제_실패됨(ExtractableResponse<Response> response, int statusCode) {
        assertThat(response.statusCode()).isEqualTo(statusCode);
    }

    public static void 지하철_구간_일치됨(String uri, String upStation, String downStation, int distance) {
        ExtractableResponse<Response> response = 지하철_구간_조회_요청(uri);
        assertThat(response.jsonPath().getList(".", SectionResponse.class))
                .extracting("upStation.name", "downStation.name", "distance")
                .contains(tuple(upStation, downStation, distance));
    }
}