package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
public class Section {

    protected static final int MIN_DISTANCE = 0;

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Station upStation;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Station downStation;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, int distance, Line line) {
        validateDistance(distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    private void validateDistance(int distance) {
        if (distance <= MIN_DISTANCE) {
            throw new IllegalArgumentException("지하천 구간 사이의 거리는 " + MIN_DISTANCE + "보다 커야 합니다.");
        }
    }

    public static Section of(Station upStation, Station downStation, int distance, Line line) {
        return new Section(upStation, downStation, distance, line);
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public Line getLine() {
        return line;
    }
}
