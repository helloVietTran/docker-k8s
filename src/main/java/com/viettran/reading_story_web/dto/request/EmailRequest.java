package com.viettran.reading_story_web.dto.request;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailRequest {
    Sender sender; // người gửi
    List<Receiver> to; // người nhận
    String subject; // tiêu đề
    String htmlContent; // mã html
}
