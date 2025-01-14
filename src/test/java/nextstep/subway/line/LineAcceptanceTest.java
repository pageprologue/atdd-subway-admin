package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineEditRequest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.TreeMap;

import static nextstep.subway.line.LineScenarioMethod.*;
import static nextstep.subway.station.StationScenarioMethod.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@DisplayName("지하철 노선 인수 테스트")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, Long> stations = 지하철_역_정보("강남", "광교");
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("광교"), 13);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("지하철 노선이 이미 등록되어 있는 경우 지하철 노선 생성에 실패한다.")
    @Test
    void createLineWithSameLine() {
        // given
        Map<String, Long> stations = 지하철_역_정보("강남", "광교");
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("광교"), 13);
        지하철_노선_등록되어_있음(신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성_실패됨(response, BAD_REQUEST.value());
    }

    @DisplayName("지하철 역이 등록되어 있지 않은 경우 지하철 노선 생성에 실패한다.")
    @Test
    void createLineWithoutTerminus() {
        // given
        Map<String, Long> emptyStations = 등록되지_않은_지하철_역();
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", emptyStations.get("상행"), emptyStations.get("하행"), 13);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성_실패됨(response, NOT_FOUND.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findLines() {
        // given
        TreeMap<String, Long> stations = 지하철_역_여러개_등록되어_있음(new StationRequest("강남"), new StationRequest("광교"),
                                                                 new StationRequest("신설동"), new StationRequest("까치산"));
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("광교"), 13);
        LineRequest 서울2호선 = 지하철_노선_정보("서울2호선", "bg-green-600", stations.get("신설동"), stations.get("까치산"), 51);

        지하철_노선_등록되어_있음(신분당선);
        지하철_노선_등록되어_있음(서울2호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("/lines");

        // then
        지하철_노선_조회_응답됨(response);
        지하철_노선_목록_조회_결과_포함됨(response, 신분당선);
        지하철_노선_목록_조회_결과_포함됨(response, 서울2호선);
    }

    @DisplayName("지하철 구간을 상행역 부터 하행역 순으로 정렬한 노선을 조회한다.")
    @Test
    void findLine() {
        // given
        Map<String, Long> stations = 지하철_역_정보("신설동", "까치산");
        LineRequest 서울2호선 = 지하철_노선_정보("서울2호선", "bg-green-600", stations.get("신설동"), stations.get("까치산"), 3);
        String createdLocationUri = 지하철_노선_등록되어_있음(서울2호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdLocationUri);

        // then
        지하철_노선_조회_응답됨(response);
        지하철_노선_조회_결과_일치됨(response, 서울2호선);
        지하철_노선_구간_정렬됨(response, stations);
    }

    @DisplayName("지하철 노선이 등록되지 않은 경우 지하철 노선 조회가 실패한다.")
    @Test
    void findLineValidateNotFound() {
        // given
        String notFoundUri = "lines/1";

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(notFoundUri);

        // then
        지하철_노선_조회_실패됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, Long> stations = 지하철_역_정보("강남", "광교");
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", stations.get("강남"), stations.get("광교"), 13);

        String createdLocationUri = 지하철_노선_등록되어_있음(신분당선);
        LineEditRequest 구분당선 = new LineEditRequest("구분당선", "bg-blue-600");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdLocationUri, 구분당선);

        // then
        지하철_노선_수정됨(response);
        지하철_노선_수정_결과_일치됨(createdLocationUri, 구분당선);
    }

    @DisplayName("지하철 노선이 등록되지 않은 경우 지하철 노선 수정이 실패한다.")
    @Test
    void updateLineValidateNotFound() {
        // given
        String notFoundUri = "lines/1";
        LineEditRequest 구분당선 = new LineEditRequest("구분당선", "bg-blue-600");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(notFoundUri, 구분당선);

        // then
        지하철_노선_수정_실패됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, Long> stations = 지하철_역_정보("신설동", "까치산");
        LineRequest 서울2호선 = 지하철_노선_정보("서울2호선", "bg-yellow-600", stations.get("신설동"), stations.get("까치산"), 51);
        String createdLocationUri = 지하철_노선_등록되어_있음(서울2호선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createdLocationUri);

        // then
        지하철_노선_삭제됨(response);
    }

    @DisplayName("지하철 노선이 등록되지 않은 경우 지하철 노선 제거가 실패한다.")
    @Test
    void deleteLineValidateEmptyResult() {
        // given
        String notFoundUri = "lines/1";

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(notFoundUri);

        // then
        지하철_노선_삭제_실패됨(response);
    }
}
