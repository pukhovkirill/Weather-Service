package controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.web.IWebExchange;

import java.io.Writer;

@Getter
@Setter
@AllArgsConstructor
public class ThymeleafTemplateEngine {
    private IWebExchange webExchange;
    private ITemplateEngine templateEngine;
    private Writer writer;
}
