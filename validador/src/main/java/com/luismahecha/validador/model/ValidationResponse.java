package com.luismahecha.validador.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter @Setter
public class ValidationResponse {
    private int invalidLines;
    private int validLines;

    public ValidationResponse(int invalidLines, int validLines) {
        this.invalidLines = invalidLines;
        this.validLines = validLines;
    }
}
