package at.rueckgr.smarthome.events.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DatabaseCredentials {
    private String url;
    private String username;
    private String password;
    private String driver;
}
