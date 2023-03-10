package fcode.backend.management.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class UpdateAnnouncementRequest {
    private Integer id;
    private String title;
    private String description;
    private String infoGroup;
    private String infoUserId;
    private String location;
    private String imageUrl;
    private Boolean sendEmailWhenUpdate;
    private String mail;
    private String mailTitle;
}
