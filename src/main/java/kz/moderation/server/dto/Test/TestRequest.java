package kz.moderation.server.dto.Test;

import kz.moderation.server.entity.tests.Question;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestRequest {
    private String testName;
    List<Question> questions;
}
