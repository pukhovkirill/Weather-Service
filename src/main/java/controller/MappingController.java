package controller;

import java.io.Writer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.web.IWebExchange;

public interface MappingController {

    default void processGet(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp)
            throws Exception{ }


    default void processHead(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp)
            throws Exception{ }

    default void processPost(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp)
            throws Exception{ }

    default void processPut(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp)
            throws Exception{ }

    default void processDelete(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp)
            throws Exception{ }

    default void processConnect(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp)
            throws Exception{ }

    default void processOptions(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp)
            throws Exception{ }

    default void processTrace(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp)
            throws Exception{ }

    default void processPatch(ThymeleafTemplateEngine engine, HttpServletRequest req, HttpServletResponse resp)
            throws Exception{ }
}
