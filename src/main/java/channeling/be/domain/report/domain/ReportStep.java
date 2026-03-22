package channeling.be.domain.report.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportStep {
    OVERVIEW("overview"),
    ANALYSIS("analysis");

    private final String value;

    ReportStep(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
