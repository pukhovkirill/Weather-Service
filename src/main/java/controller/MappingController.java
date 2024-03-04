package controller;

import java.io.Writer;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.web.IWebExchange;

public interface MappingController {

    void process(final IWebExchange webExchange, final ITemplateEngine templateEngine, final Writer writer)
            throws Exception;

}
