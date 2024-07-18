package me.jp.jda.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Plugin {
    private String name;
    private String price;
    private String description;
    private String link;
    private String version;
}
